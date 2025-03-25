/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ntn.services;

import com.ntn.controllers.Utils;
import com.ntn.pojo.Event;
import com.ntn.pojo.JdbcUtils;
import com.ntn.pojo.Ticket;
import com.ntn.pojo.Venue;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author admin
 */
public class ListEventService {

    public List<Event> getEvent() throws SQLException {
        List<Event> events = new ArrayList<>();
        try (Connection conn = JdbcUtils.getConnection()) {
            PreparedStatement stm = conn.prepareStatement(
                    "SELECT e.id AS event_id, e.name AS event_name, e.start_date, e.end_date, e.max_attendees, "
                    + "v.id AS venue_id, v.name AS venue_name, "
                    + "(e.max_attendees - COALESCE(SUM(t.quantity), 0)) AS registerUser " // Đếm số lượng đăng ký, tránh NULL
                    + "FROM event e "
                    + "JOIN venue v ON e.venue_id = v.id "
                    + "LEFT JOIN ticket t ON t.event_id = e.id "
                    + "WHERE e.is_active = 1 "
                    + "GROUP BY e.id, e.name, e.start_date, e.end_date, e.max_attendees, v.id, v.name " // Đảm bảo GROUP BY
            );

            ResultSet rs = stm.executeQuery();

            while (rs.next()) {

                Event e = new Event(
                        rs.getInt("event_id"), // Lấy đúng id của event
                        rs.getString("event_name"), // Lấy đúng tên event
                        rs.getTimestamp("start_date"),
                        rs.getTimestamp("end_date"),
                        rs.getInt("max_attendees")
                );
                e.setRegisterUser(rs.getInt("registerUser"));
                e.setVenue(new Venue(rs.getInt("venue_id"), rs.getString("venue_name"), 0));
                events.add(e);
            }
        }
        return events;
    }

//    public List<Ticket> getTicketEvent() throws SQLException {
//        List<Ticket> tickets = new ArrayList<>();
//        try (Connection conn = JdbcUtils.getConnection()) {
//            PreparedStatement stm = conn.prepareStatement(
//                    "SELECT ticket.id as ticket_id, ticket."
//            );
//
//            ResultSet rs = stm.executeQuery();
//
//            while (rs.next()) {
//
//            }
//        }
//        return tickets;
//    }

    public Map<String, Object[]> getTicketInfo(int eventId) {
        Map<String, Object[]> ticketInfo = new HashMap<>();
        try (Connection conn = JdbcUtils.getConnection()) {
            String sql = "Select tt.name as ticket_type, t.quantity as ticket_quantity, t.price as ticket_price "
                + "FROM ticket t "
                + "JOIN tickettype tt ON t.ticket_type_id = tt.id "
                + "WHERE t.event_id = ? ";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, eventId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String type = rs.getString("ticket_type");
                int quantity = rs.getInt("ticket_quantity");
                BigDecimal price = rs.getBigDecimal("ticket_price");
                ticketInfo.put(type,new Object[]{quantity,price});
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return ticketInfo;
    }
}
