package com.skupina1.notificationservice.service;

import com.skupina1.notificationservice.model.Notification;
import com.skupina1.notificationservice.repository.NotificationRepository;
import com.skupina1.notificationservice.sse.UserSseRegistry;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.sse.OutboundSseEvent;
import jakarta.ws.rs.sse.Sse;
import jakarta.ws.rs.sse.SseEventSink;

public class BroadcastingService {

    private final Sse sse;
    private final NotificationRepository repository = new NotificationRepository();

    public BroadcastingService(Sse sse){
        this.sse = sse;
    }

    public void sendToUser(Notification notification) {

        /*
        for (var sink : UserSseRegistry.getSinks(notification.getRecipient())) {
            if (!sink.isClosed()) {
                OutboundSseEvent event = sse.newEventBuilder()
                        .name("notification")
                        .mediaType(MediaType.APPLICATION_JSON_TYPE)
                        .data(notification)
                        .build();
                sink.send(event);
            }
        }
         */

        for (SseEventSink sink : UserSseRegistry.getSinks(notification.getRecipient())) {
            try {
                if (!sink.isClosed()) {
                    sink.send(sse.newEventBuilder()
                            .name("notification")
                            .mediaType(MediaType.APPLICATION_JSON_TYPE)
                            .data(notification)
                            .build());
                } else {
                    System.out.println("Sink closed.");
                    // cleanup closed sink
                    UserSseRegistry.removeSink(notification.getRecipient(), sink);
                }
            } catch (Exception e) {
                UserSseRegistry.removeSink(notification.getRecipient(), sink);
            }
        }
    }
}
