/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ntn.services;

import com.ntn.pojo.DTO.NotificationDTO;
import com.ntn.pojo.JdbcUtils;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author NHAT
 */
public class NotificationServices {

    public void sendNotificationForUser(String content, String type, int eventId) throws SQLException {
        try (Connection conn = JdbcUtils.getConnection()) {
            EventServices eventServices = new EventServices();
            List<Integer> registerIds = eventServices.getRegisterByEventId(eventId);
            if (!registerIds.isEmpty()) {
                conn.setAutoCommit(false);
                String sql = "INSERT INTO notification(content,notification_type,register_id) VALUES (?,?,?)";
                PreparedStatement stm = conn.prepareCall(sql);

                for (Integer registerId : registerIds) {
                    stm.setString(1, content);
                    stm.setString(2, type);
                    stm.setInt(3, registerId);
                    stm.executeUpdate();
                }
                conn.commit();
            }
        }
    }

    public boolean isUsersRemider(List<Integer> registerIds) throws SQLException {
        try (Connection conn = JdbcUtils.getConnection()) {
            if (!registerIds.isEmpty()) {

                String placeholders = registerIds.stream()
                        .map(id -> "?") // Tạo ?,?,?
                        .collect(Collectors.joining(", "));
                
                String sql = "SELECT COUNT(*) FROM eventmanagement.notification\n"
                        + "JOIN registration ON registration.id = notification.register_id\n"
                        + "WHERE notification.notification_type = 'REMINDER' AND registration.id IN (" + placeholders + ")";
                
                PreparedStatement stm = conn.prepareCall(sql);

                for (int i = 0; i < registerIds.size(); i++) {
                    stm.setInt(i + 1, registerIds.get(i));
                }

                ResultSet rs = stm.executeQuery();
                if (rs.next()) {
                    return rs.getInt(1) > 0 && rs.getInt(1) == registerIds.size(); // có kết quả và kết quả bằng số user đăng kí trong sự kiện
                }

            }
        }
        return false;
    }

    public List<NotificationDTO> getNotifications(Integer userId) throws SQLException {
        List<NotificationDTO> notifications = new ArrayList<>();
        try (Connection conn = JdbcUtils.getConnection()) {
            String sql = "SELECT notification.id, notification.content, notification.created_date, user.username,event.name "
                    + "FROM notification "
                    + "JOIN registration ON registration.id = notification.register_id "
                    + "JOIN ticket ON ticket.id = registration.ticket_id "
                    + "JOIN event ON event.id = ticket.event_id "
                    + "JOIN user ON user.id = registration.user_id";
            
            if(userId != null)
                sql += " WHERE user.id = ?";
            
            sql+=" ORDER BY event.name";
            
            PreparedStatement stm = conn.prepareStatement(sql);
            if(userId != null)
                stm.setInt(1, userId);
            
            ResultSet rs = stm.executeQuery();
            while (rs.next()) {
                NotificationDTO n = new NotificationDTO(rs.getInt("id"),
                        rs.getString("content"), rs.getTimestamp("created_date"),
                        rs.getString("username"), rs.getString("name"));
                notifications.add(n);
            }

            return notifications;
        }
    }
}
