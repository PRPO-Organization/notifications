package com.skupina1.notificationservice.repository;

import com.skupina1.notificationservice.db.Database;
import com.skupina1.notificationservice.model.Notification;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Singleton;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class NotificationRepository {

    public boolean logNotification(Notification notification) {
        String sql = "INSERT INTO notifications (recipient, subject, body, created_at) VALUES (?, ?, ?, ?)";

        try (Connection con = Database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, notification.getRecipient());
            ps.setString(2, notification.getSubject());
            ps.setString(3, notification.getBody());
            ps.setObject(4, notification.getCreatedAt());
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public ArrayList<Notification> getNotifications(){
        String sql = "SELECT * FROM notifications";
        ArrayList<Notification> notifications = new ArrayList<Notification>();
        try (Connection con = Database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();

            while(rs.next()){
                String to = rs.getString("recipient");
                String subject = rs.getString("subject");
                String body = rs.getString("body");

                Notification n = new Notification(to, subject, body);
                n.setCreatedAt(rs.getObject("created_at", LocalDateTime.class));
                n.setId(rs.getInt("id"));
                notifications.add(n);
            }

            return notifications;
        } catch (SQLException e) {
            e.printStackTrace();
            return notifications;
        }
    }

    public ArrayList<Notification> getNotificationsToEmail(String email){
        String sql = "SELECT * FROM notifications WHERE recipient = ?";
        ArrayList<Notification> notifications = new ArrayList<Notification>();
        try (Connection con = Database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();

            while(rs.next()){
                String to = rs.getString("recipient");
                String subject = rs.getString("subject");
                String body = rs.getString("body");

                Notification n = new Notification(to, subject, body);
                n.setCreatedAt(rs.getObject("created_at", LocalDateTime.class));
                n.setId(rs.getInt("id"));
                notifications.add(n);
            }

            return notifications;
        } catch (SQLException e) {
            e.printStackTrace();
            return notifications;
        }
    }

    public ArrayList<Notification> getUnreadNotificationsToEmail(String email){
        String sql = "SELECT * FROM notifications WHERE recipient = ? AND read = false";
        ArrayList<Notification> notifications = new ArrayList<>();

        try (Connection con = Database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();

            while(rs.next()){
                String to = rs.getString("recipient");
                String subject = rs.getString("subject");
                String body = rs.getString("body");

                Notification n = new Notification(to, subject, body);
                n.setCreatedAt(rs.getObject("created_at", LocalDateTime.class));
                n.setId(rs.getInt("id"));
                n.setRead(rs.getBoolean("read")); // set the read status
                notifications.add(n);
            }

            return notifications;
        } catch (SQLException e) {
            e.printStackTrace();
            return notifications;
        }
    }

    public int markNotificationsAsRead(String email) {
        String sql = "UPDATE notifications SET read = true WHERE recipient = ? AND read = false";

        try (Connection con = Database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, email);
            int rowsUpdated = ps.executeUpdate();

            return rowsUpdated;

        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    public int deleteOlderThan(int days){
        String sql = """
                DELETE FROM notifications WHERE created_at < NOW() - INTERVAL '%d days'
                """.formatted(days);

        try (Connection con = Database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            return ps.executeUpdate();
        }catch(SQLException e){
            e.printStackTrace();
            return 0;
        }
    }
}
