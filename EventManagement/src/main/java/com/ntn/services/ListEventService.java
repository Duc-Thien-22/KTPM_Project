/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ntn.services;

import com.ntn.pojo.Event;
import com.ntn.pojo.JdbcUtils;
import com.ntn.pojo.Venue;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author admin
 */
public class ListEventService {

    public List<Event> getEvent() throws SQLException {
        List<Event> events = new ArrayList<>();
        try (Connection conn = JdbcUtils.getConnection()) {
            PreparedStatement stm = conn.prepareStatement(
               "SELECT e.id AS event_id, e.name AS event_name, e.start_date, e.end_date, e.max_attendees, e.is_active, "
            + "v.id AS venue_id, v.name AS venue_name, "
            + "COALESCE(COUNT(r.user_id), 0) AS registered_users "  // Đếm số lượng đăng ký, tránh NULL
            + "FROM event e "
            + "JOIN venue v ON e.venue_id = v.id "
            + "LEFT JOIN registration r ON e.id = r.event_id "  // Dùng LEFT JOIN để giữ tất cả sự kiện
            + "WHERE e.end_date > NOW() "
            + "GROUP BY e.id, e.name, e.start_date, e.end_date, e.max_attendees, e.is_active, v.id, v.name " // Đảm bảo GROUP BY
            + "ORDER BY e.start_date ASC"
                    
            );

            ResultSet rs = stm.executeQuery();

            while (rs.next()) {
                //Venue venue = new Venue(rs.getInt("venue_id"), rs.getString("venue_name"));

                Event e = new Event(
                        rs.getInt("event_id"), // Lấy đúng id của event
                        rs.getString("event_name"), // Lấy đúng tên event
                        rs.getTimestamp("start_date"),
                        rs.getTimestamp("end_date"),
                        rs.getInt("max_attendees"),
                        rs.getBoolean("is_active")
                        
                );
                e.setRegisteredUsers(rs.getInt("registered_users"));
                events.add(e);
            }
        }
        return events;
    }
}
