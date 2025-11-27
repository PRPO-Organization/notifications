package com.skupina1.notificationservice.service;

import com.skupina1.notificationservice.model.Notification;
import com.skupina1.notificationservice.repository.NotificationRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import java.util.ArrayList;
import java.util.List;

public class NotificationService {

    private final EmailService emailService = new EmailService();
    private final NotificationRepository repository = new NotificationRepository();

    public boolean sendNotification(String recipient, String subject, String body) {
        // only log for now
        Notification notification = new Notification(recipient, subject, body);
        boolean logged = repository.logNotification(notification);
        boolean sent = emailService.sendEmail(recipient, subject, body);
        //boolean sent = true;

        if(logged)
            System.out.println("Notification logged for: " + recipient);
        else
            System.out.println("Notification logging failed");

        if(sent)
            System.out.println("Notification sent for: " + recipient);
        else
            System.out.println("Notification sending failed");

        return logged && sent;
    }

    public boolean sendNotificationAsHtml(String recipient, String subject, String body){
        Notification notification = new Notification(recipient, subject, body);
        boolean logged = repository.logNotification(notification);
        boolean sent = emailService.sendEmailAsHtml(recipient, subject, body);
        //boolean sent = true;

        if(logged)
            System.out.println("Notification logged for: " + recipient);
        else
            System.out.println("Notification logging failed");

        if(sent)
            System.out.println("Notification sent for: " + recipient);
        else
            System.out.println("Notification sending failed");

        return logged && sent;
    }

    public ArrayList<Notification> getNotifications(){
        return repository.getNotifications();
    }

    public ArrayList<Notification> getNotificationsToEmail(String email){
        return repository.getNotificationsToEmail(email);
    }

    public int deleteOlderThan(int days){
        return repository.deleteOlderThan(days);
    }
}