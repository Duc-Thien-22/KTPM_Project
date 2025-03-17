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
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author NHAT
 */
public class EventServices {

    public List<Event> getEvents() throws SQLException {
        List<Event> events = new ArrayList<>();
        try (Connection conn = JdbcUtils.getConnection()) {
            String sql = "SELECT*FROM event";
            Statement stm = conn.createStatement();

            ResultSet rs = stm.executeQuery(sql);
            while (rs.next()) {
                VenueServices v = new VenueServices();
                Event e = new Event(rs.getInt("id"), rs.getString("name"), rs.getTimestamp("start_date"), rs.getTimestamp("end_date"), rs.getInt("max_attendees"), rs.getBoolean("is_active"));
                e.setVenue(v.getVenueById(rs.getInt("venue_id")));
                events.add(e);
            }
        }

        return events;
    }

    public int createEvent(String name, Timestamp startDate, Timestamp endDate, int maxAttendees, int venue_id) throws SQLException {
        try (Connection conn = JdbcUtils.getConnection()) {
            String sql = "INSERT INTO event (name, start_date, end_date, max_attendees, venue_id) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement stm = conn.prepareCall(sql);

            stm.setString(1, name);
            stm.setTimestamp(2, startDate);
            stm.setTimestamp(3, endDate);
            stm.setInt(4, maxAttendees);
            stm.setInt(5, venue_id);

            return stm.executeUpdate();
        }
    }

    public Event checkVenueAndDateTime(int venueId, Timestamp startDate) throws SQLException {
        try (Connection conn = JdbcUtils.getConnection()) {
            String sql = "SELECT * FROM event WHERE venue_id = ? AND ? BETWEEN start_date AND end_date";
            PreparedStatement stm = conn.prepareCall(sql);
            stm.setInt(1, venueId);
            stm.setTimestamp(2, startDate);

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
}
