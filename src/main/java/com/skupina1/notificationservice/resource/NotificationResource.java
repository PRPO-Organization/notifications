package com.skupina1.notificationservice.resource;

import com.skupina1.notificationservice.model.Notification;
import com.skupina1.notificationservice.security.JwtUtils;
import com.skupina1.notificationservice.service.BroadcastingService;
import com.skupina1.notificationservice.service.NotificationService;
import com.skupina1.notificationservice.sse.UserSseRegistry;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwt;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.sse.Sse;
import jakarta.ws.rs.sse.SseEventSink;

import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Path("/notifications")
@Consumes(MediaType.APPLICATION_JSON)
public class NotificationResource {

    @Context
    private Sse sse;

    private final NotificationService service = new NotificationService();
    private final BroadcastingService broadcastingService;

    public NotificationResource(@Context Sse sse){
        this.broadcastingService = new BroadcastingService(sse);
    }



    @POST
    @Path("/send")
    @RolesAllowed("ADMIN")
    @Produces(MediaType.APPLICATION_JSON)
    public Response send(Notification notification) {
        boolean sent = service.sendNotification(notification.getRecipient(), notification.getSubject(), notification.getBody());
        broadcastingService.sendToUser(notification);

        if(!sent)
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"Notification logging failed\"}")
                    .build();

        return Response.ok("{\"message\":\"Notification logged\"}").build();
    }

    @POST
    @Path("/send-html")
    @RolesAllowed("ADMIN")
    @Produces(MediaType.APPLICATION_JSON)
    public Response sendHtml(Notification notification) {
        boolean sent = service.sendNotificationAsHtml(notification.getRecipient(), notification.getSubject(), notification.getBody());

        if(!sent)
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"Notification logging failed\"}")
                    .build();

        return Response.ok("{\"message\":\"Notification logged\"}").build();
    }

    @GET
    @RolesAllowed("ADMIN")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllNotifications(){
        ArrayList<Notification> notifications = service.getNotifications();

        if(notifications.isEmpty())
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"message\":\"No notifications found\"}")
                    .build();

        return Response.ok(notifications).build();
    }

    @GET
    @Path("/{email}")
    @RolesAllowed("ADMIN")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getNotificationsToEmail(@PathParam("email") String email){
        ArrayList<Notification> notifications = service.getNotificationsToEmail(email);

        if(notifications.isEmpty())
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"message\":\"No notifications found\"}")
                    .build();

        return Response.ok(notifications).build();
    }

    @GET
    @Path("/me")
    @RolesAllowed({"CUSTOMER, DRIVER"})
    @Produces(MediaType.APPLICATION_JSON)
    public Response getOwnNotifications(@Context ContainerRequestContext ctx){
        Claims claims = (Claims) ctx.getProperty("claims");
        if (claims == null) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("{\"error\":\"Missing token claims\"}")
                    .build();
        }

        String email = claims.getSubject();
        if (email == null) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("{\"error\":\"Token missing email claim\"}")
                    .build();
        }

        ArrayList<Notification> notifications = service.getNotificationsToEmail(email);

        if (notifications.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"message\":\"No notifications found\"}")
                    .build();
        }

        return Response.ok(notifications).build();
    }

    @GET
    @Path("/me/unread")
    @RolesAllowed({"CUSTOMER, DRIVER"})
    @Produces(MediaType.APPLICATION_JSON)
    public Response getOwnUnreadNotifications(@Context ContainerRequestContext ctx){
        Claims claims = (Claims) ctx.getProperty("claims");
        if (claims == null) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("{\"error\":\"Missing token claims\"}")
                    .build();
        }

        String email = claims.getSubject();
        if (email == null) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("{\"error\":\"Token missing email claim\"}")
                    .build();
        }

        ArrayList<Notification> notifications = service.getUnreadNotificationsToEmail(email);

        if (notifications.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"message\":\"No notifications found\"}")
                    .build();
        }

        return Response.ok(notifications).build();
    }

    @GET
    @Path("/stream")
    @Produces(MediaType.SERVER_SENT_EVENTS)
    public void stream(@Context SseEventSink eventSink, @QueryParam("token") String token,
                       @Context Sse sse){

        if (eventSink == null || sse == null) {
            throw new IllegalStateException("Sse injection failed");
        }

        String email = JwtUtils.validateToken(token).getSubject();
        if (email == null) {
            eventSink.close();
            System.out.println("Invalid token: Email not found");
            return;
        }

        UserSseRegistry.add(email, eventSink);

        //eventSink.onClose(() -> UserSseRegistry.remove(email));

        // Send initial hello so EventSource opens immediately
        eventSink.send(sse.newEvent("init", "connected"));
    }

    @PUT
    @Path("/me")
    @RolesAllowed({"CUSTOMER, DRIVER, ADMIN"})
    @Produces(MediaType.APPLICATION_JSON)
    public Response markNotificationsAsRead(@Context ContainerRequestContext ctx){
        Claims claims = (Claims) ctx.getProperty("claims");
        if (claims == null) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("{\"error\":\"Missing token claims\"}")
                    .build();
        }

        String email = claims.getSubject();
        if (email == null) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("{\"error\":\"Token missing email claim\"}")
                    .build();
        }

        int marked = service.markNotificationsAsRead(email);
        if (marked == 0) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"error\":\"No unread notifications found\"}")
                    .build();
        }
        return Response.ok("{\"message\":\"" + marked + " notifications marked as read\"}").build();
    }

    @DELETE
    @Path("/{days}")
    @RolesAllowed("ADMIN")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteOlderThan(@PathParam("days") int days){
        int deletedCount = service.deleteOlderThan(days);

        if(deletedCount == 0){
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"message\":\"No notifications older than specified time\"}")
                    .build();
        }

        return Response.ok("{\"deleted\":\"" + deletedCount + "\"}").build();
    }
}
