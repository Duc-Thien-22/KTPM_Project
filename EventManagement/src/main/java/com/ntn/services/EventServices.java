/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ntn.services;

import com.ntn.pojo.Event;
import com.ntn.pojo.JdbcUtils;      
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author NHAT
 */
public class EventServices {

    // lưu ý xong đi sửa lại hàm này
    public List<Event> getEvents(int num, String kw) throws SQLException {
        List<Event> events = new ArrayList<>();
        try (Connection conn = JdbcUtils.getConnection()) {
//            String sql = "SELECT*FROM event ORDER BY start_date DESC";
            PreparedStatement stm;

            if (num == 0) {
                stm = conn.prepareCall("SELECT*FROM event WHERE name like concat('%',?,'%') ORDER BY start_date desc");
                stm.setString(1, kw);
            } else {
                stm = conn.prepareCall("SELECT*FROM event ORDER BY rand() LIMIT ?");
                stm.setInt(1, num);
            }

            ResultSet rs = stm.executeQuery();
            while (rs.next()) {
                VenueServices v = new VenueServices();
                Event e = new Event(rs.getInt("id"), rs.getString("name"), rs.getTimestamp("start_date"), rs.getTimestamp("end_date"), rs.getInt("max_attendees"), rs.getBoolean("is_active"));
                e.setVenue(v.getVenueById(rs.getInt("venue_id")));
                events.add(e);
            }
        }

        return events;
    }
    public List<Event> getEvents() throws SQLException {
        List<Event> events = new ArrayList<>();
        try (Connection conn = JdbcUtils.getConnection()) {
            String sql = "SELECT*FROM event WHERE is_active = True";
            PreparedStatement stm = conn.prepareCall(sql);

            ResultSet rs = stm.executeQuery();
            while (rs.next()) {
                VenueServices v = new VenueServices();
                Event e = new Event(rs.getInt("id"), rs.getString("name"), rs.getTimestamp("start_date"), rs.getTimestamp("end_date"), rs.getInt("max_attendees"), rs.getBoolean("is_active"));
                e.setVenue(v.getVenueById(rs.getInt("venue_id")));
                events.add(e);
            }
        }

        return events;
    }
    public int addEvent(Event e) throws SQLException {
        try (Connection conn = JdbcUtils.getConnection()) {
            String sql = "INSERT INTO event (name, start_date, end_date, max_attendees, venue_id) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement stm = conn.prepareCall(sql);

            stm.setString(1, e.getName());
            stm.setTimestamp(2, e.getStartDate());
            stm.setTimestamp(3, e.getEndDate());
            stm.setInt(4, e.getMaxAttendees());
            stm.setInt(5, e.getVenue().getId());

            return stm.executeUpdate();
        }
    }

    public Event checkVenueAndDateTime(int venueId, Timestamp startDate, int eventId) throws SQLException {
        try (Connection conn = JdbcUtils.getConnection()) {
            String sql = "SELECT * FROM event WHERE venue_id = ? AND DATE_FORMAT(?, '%Y-%m-%d %H:%i') BETWEEN DATE_FORMAT(start_date, '%Y-%m-%d %H:%i')AND DATE_FORMAT(end_date, '%Y-%m-%d %H:%i') AND id != ?";
            PreparedStatement stm = conn.prepareCall(sql);
            stm.setInt(1, venueId);
            stm.setTimestamp(2, startDate);
            stm.setInt(3, eventId);

            ResultSet rs = stm.executeQuery();
            if (rs.next()) {
                VenueServices v = new VenueServices();
                Event e = new Event(rs.getInt("id"), rs.getString("name"), rs.getTimestamp("start_date"), rs.getTimestamp("end_date"), rs.getInt("max_attendees"), rs.getBoolean("is_active"));
                e.setVenue(v.getVenueById(rs.getInt("venue_id")));
                return e;
            }
        }

        return null;
    }

    public int deleteEventById(int eventId) throws SQLException {
        int rs = -1;
        try (Connection conn = JdbcUtils.getConnection()) {
            String sql = "DELETE FROM event WHERE id = ?";
            PreparedStatement stm = conn.prepareCall(sql);
            stm.setInt(1, eventId);

            rs = stm.executeUpdate();
        }
        return rs;
    }

    public int updateEventById(Event e) throws SQLException {
        int rs = -1;
        try (Connection conn = JdbcUtils.getConnection()) {
            String sql = "UPDATE event SET name = ?, start_date = ? , end_date = ?, max_attendees = ? , venue_id = ?, is_active = ? WHERE id = ?";
            PreparedStatement stm = conn.prepareCall(sql);
            stm.setString(1, e.getName());
            stm.setTimestamp(2, e.getStartDate());
            stm.setTimestamp(3, e.getEndDate());
            stm.setInt(4, e.getMaxAttendees());
            stm.setInt(5, e.getVenue().getId());
            stm.setBoolean(6, e.getIsActive());
            stm.setInt(7, e.getId());

            rs = stm.executeUpdate();
        }
        return rs;
    }

    public List<Integer> getRegisterByEventId(int eventId) throws SQLException {
        List<Integer> registerIds = new ArrayList<>();

        try (Connection conn = JdbcUtils.getConnection()) {
            String sql = "SELECT registration.id FROM event "
                    + "JOIN ticket ON event.id = ticket.event_id "
                    + "JOIN registration ON registration.ticket_id = ticket.id "
                    + "WHERE event.id = ?";
            PreparedStatement stm = conn.prepareCall(sql);
            
            stm.setInt(1, eventId);
            ResultSet rs = stm.executeQuery();
            while(rs.next()){
                registerIds.add(rs.getInt("id"));
            }   
        }
        return registerIds;
    }
    
    public void refundeMoneyToUsers(List<Integer>registerIds) throws SQLException{
        try (Connection conn = JdbcUtils.getConnection()) {
            if (!registerIds.isEmpty()) {
                String placeholders = registerIds.stream()
                        .map(id -> "?") // Tạo ?,?,?
                        .collect(Collectors.joining(", "));
                
                String sql = "UPDATE payment SET is_refunded = 1 WHERE register_id IN (" + placeholders + ")";
                
                PreparedStatement stm = conn.prepareCall(sql);
                
                for (int i = 0; i < registerIds.size(); i++) {
                    stm.setInt(i + 1, registerIds.get(i));
                }
                stm.executeUpdate();
            }
        }
    }
}
