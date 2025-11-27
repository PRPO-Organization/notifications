package com.skupina1.notificationservice;

import com.skupina1.notificationservice.provider.ObjectMapperProvider;
import com.skupina1.notificationservice.repository.NotificationRepository;
import com.skupina1.notificationservice.security.JwtAuthFilter;
import com.skupina1.notificationservice.service.EmailService;
import com.skupina1.notificationservice.service.NotificationService;
import jakarta.activation.spi.MailcapRegistryProvider;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;

public class App {
    public static final String BASE_URI = "http://0.0.0.0:8081/";
    private static final Logger log = LoggerFactory.getLogger(App.class);

    public static void main(String[] args) {

        ResourceConfig rc = new ResourceConfig().packages("com.skupina1.notificationservice.resource")
                .register(JacksonFeature.class)
                .register(ObjectMapperProvider.class)
                .register(JwtAuthFilter.class);

        HttpServer server = GrizzlyHttpServerFactory.createHttpServer(URI.create(BASE_URI), rc);

        try {
            server.start();
            System.out.println("Notification service running at " + BASE_URI);
            Thread.currentThread().join(); // Keep main thread alive
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            server.shutdown();
        }
    }
}