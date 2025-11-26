package com.skupina1.notificationservice.resource;

import com.skupina1.notificationservice.model.Notification;
import com.skupina1.notificationservice.service.NotificationService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.ArrayList;

@Path("/notifications")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class NotificationResource {

    private final NotificationService service = new NotificationService();

    @POST
    @Path("/send")
    public Response send(Notification notification) {
        boolean sent = service.sendNotification(notification.getRecipient(), notification.getSubject(), notification.getBody());

        if(!sent)
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"Notification logging failed\"}")
                    .build();

        return Response.ok("{\"message\":\"Notification logged\"}").build();
    }

    @GET
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
    public Response getNotificationsToEmail(@PathParam("email") String email){
        ArrayList<Notification> notifications = service.getNotificationsToEmail(email);

        if(notifications.isEmpty())
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"message\":\"No notifications found\"}")
                    .build();

        return Response.ok(notifications).build();
    }
}
