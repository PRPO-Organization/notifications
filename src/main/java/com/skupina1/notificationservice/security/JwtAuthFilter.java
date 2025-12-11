package com.skupina1.notificationservice.security;

import io.jsonwebtoken.Claims;
import jakarta.annotation.Priority;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ResourceInfo;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;

import java.io.IOException;

@Provider
@Priority(Priorities.AUTHENTICATION)
public class JwtAuthFilter implements ContainerRequestFilter {

    private static final String SECRET = "53V3NPR41535T0L0RDJUR1CL0NGM4YHER31GNF0RH15RUL3W45PR0M153D";

    @Context
    ResourceInfo resourceInfo;

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {

        String path = requestContext.getUriInfo().getPath();
        if (path.equals("notifications/stream")) {
            return;
        }

        if (requestContext.getMethod().equalsIgnoreCase("OPTIONS")) {
            return; // no auth required for OPTIONS
        }

        String authHeader = requestContext.getHeaderString("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            abort(requestContext, Response.Status.UNAUTHORIZED, "Missing or invalid Authorization header");
            return;
        }

        String token = authHeader.substring("Bearer ".length());

        Claims claims;
        try {
            claims = JwtUtils.validateToken(token);
        } catch (Exception e) {
            abort(requestContext, Response.Status.UNAUTHORIZED, "Invalid or expired token: " + e);
            return;
        }

        requestContext.setProperty("claims", claims);
        String role = claims.get("role", String.class);
        /*
        if (!"ADMIN".equals(role)) {
            abort(requestContext, Response.Status.FORBIDDEN, "Insufficient permissions");
            return;
        }
        */

        RolesAllowed rolesAllowed = getRolesAllowed();
        if (rolesAllowed != null) {
            System.out.println(rolesAllowed);
            if (!isRoleAllowed(role, rolesAllowed.value())) {
                System.out.println("CHECK getRolesAllowed");
                abort(requestContext, Response.Status.FORBIDDEN, "Insufficient permissions");
                return;
            }
        }

        // if everything passed, request continues
    }

    private RolesAllowed getRolesAllowed(){
        RolesAllowed ra = resourceInfo.getResourceMethod().getAnnotation(RolesAllowed.class);
        if (ra != null)
            return ra;

        // Then check class-level annotation
        return resourceInfo.getResourceClass().getAnnotation(RolesAllowed.class);
    }

    private boolean isRoleAllowed(String userRole, String[] allowedRoles) {
        if ("ADMIN".equals(userRole)) return true;

        for (String role : allowedRoles) {
            if (role.equals(userRole)) return true;
        }
        return false;
    }

    private void abort(ContainerRequestContext ctx, Response.Status status, String msg) {
        ctx.abortWith(Response.status(status)
                .type(MediaType.APPLICATION_JSON)
                .entity("{\"error\": \"" + msg + "\"}")
                .build());
    }
}
