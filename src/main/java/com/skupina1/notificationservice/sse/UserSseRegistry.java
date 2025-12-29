package com.skupina1.notificationservice.sse;

import jakarta.ws.rs.sse.SseEventSink;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class UserSseRegistry {

    // Map email -> list of SSE connections
    private static final ConcurrentHashMap<String, SseEventSink> sinks = new ConcurrentHashMap<>();

    public static void add(String email, SseEventSink sink) {
        sinks.put(email, sink);
    }

    public static void remove(String email) {
        sinks.remove(email);
    }

    public static SseEventSink get(String email) {
        return sinks.get(email);
    }
}
