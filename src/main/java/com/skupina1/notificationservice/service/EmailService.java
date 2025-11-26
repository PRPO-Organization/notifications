package com.skupina1.notificationservice.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Singleton;
import jakarta.mail.*;
import jakarta.mail.internet.*;
import jakarta.ws.rs.ApplicationPath;

import java.util.Properties;

public class EmailService {
    private Session session;
    private String from;

    public EmailService(){
        init();
    }

    private void init(){
        String host = System.getenv().getOrDefault("SMTP_HOST", "smtp.gmail.com");
        String port = System.getenv().getOrDefault("SMTP_PORT", "587");
        String user = System.getenv().getOrDefault("SMTP_USER", "");
        String pass = System.getenv().getOrDefault("SMTP_PASS", "");
        this.from = System.getenv().getOrDefault("SMTP_FROM", user);

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", port);

        this.session = Session.getInstance(
                props,
                new Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(user, pass);
                    }
                }
        );
    }

    public boolean sendEmail(String to, String subject, String body) {
        try {
            Message msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress(from));
            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            msg.setSubject(subject);
            msg.setText(body);

            Transport.send(msg);
            return true;
        } catch (MessagingException e) {
            System.out.println("Messaging error: " + e);
            return false;
        }
    }
}
