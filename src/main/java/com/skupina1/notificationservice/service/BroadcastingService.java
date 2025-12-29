package com.skupina1.notificationservice.service;

import com.skupina1.notificationservice.model.Notification;
import com.skupina1.notificationservice.repository.NotificationRepository;
import com.skupina1.notificationservice.sse.UserSseRegistry;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.sse.OutboundSseEvent;
import jakarta.ws.rs.sse.Sse;
import jakarta.ws.rs.sse.SseEventSink;

public class BroadcastingService {

    private final Sse sse;

    public BroadcastingService(Sse sse){
        this.sse = sse;
    }

    public void sendToUser(Notification notification) {
        SseEventSink sink = UserSseRegistry.get(notification.getRecipient());
        if (sink == null || sink.isClosed()) return;

        OutboundSseEvent event = sse.newEventBuilder()
                .name("notification")
                .mediaType(MediaType.APPLICATION_JSON_TYPE)
                .data(Notification.class, notification)
                .build();

        sink.send(event);
    }
}
