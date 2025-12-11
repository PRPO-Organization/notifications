package com.skupina1.notificationservice.sse;

import jakarta.ws.rs.sse.SseEventSink;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class UserSseRegistry {

    // Map email -> list of SSE connections
    private static final ConcurrentHashMap<String, CopyOnWriteArrayList<SseEventSink>> userSinks = new ConcurrentHashMap<>();

    public static void addSink(String email, SseEventSink sink) {
        userSinks.computeIfAbsent(email, k -> new CopyOnWriteArrayList<>()).add(sink);
    }

    public static void removeSink(String email, SseEventSink sink) {
        List<SseEventSink> sinks = userSinks.get(email);
        if (sinks != null) {
            sinks.remove(sink);
            if (sinks.isEmpty()) {
                userSinks.remove(email);
            }
        }
    }

    public static List<SseEventSink> getSinks(String email) {
        return userSinks.getOrDefault(email, new CopyOnWriteArrayList<>());
    }
}
