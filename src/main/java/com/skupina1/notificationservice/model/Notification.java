package com.skupina1.notificationservice.model;

import java.time.LocalDateTime;

public class Notification {
    private int id;
    private String recipient;
    private String subject;
    private String body;
    private LocalDateTime createdAt;
    private boolean isRead;

    public Notification() {}

    public Notification(String recipient, String subject, String body) {
        this.recipient = recipient;
        this.subject = subject;
        this.body = body;
        this.createdAt = LocalDateTime.now();
        this.isRead = false;
    }

    public int getId() {return this.id;}
    public String getRecipient() {return this.recipient;}
    public String getBody() {return this.body;}
    public String getSubject() {return this.subject;}
    public LocalDateTime getCreatedAt() {return this.createdAt;}
    public boolean isRead() {return this.isRead;}

    public void setRecipient(String recipient) {this.recipient = recipient;}
    public void setSubject(String subject) {this.subject = subject;}
    public void setBody(String body) {this.body = body;}
    public void setId(int id) {this.id = id;}
    public void setCreatedAt(LocalDateTime createdAt) {this.createdAt = createdAt;}
    public void setRead(boolean read) {this.isRead = read;}
}
