/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ntn.services;

import com.ntn.pojo.JdbcUtils;
import com.ntn.pojo.Ticket;
import com.ntn.pojo.Tickettype;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author NHAT
 */
public class TicketServices {

    public List<Tickettype> getTicketTypes() throws SQLException {
        List<Tickettype> ticketTypes = new ArrayList<>();
        try (Connection conn = JdbcUtils.getConnection()) {
            String sql = "SELECT*FROM tickettype";
            Statement stm = conn.createStatement();

            ResultSet rs = stm.executeQuery(sql);

            while (rs.next()) {
                ticketTypes.add(new Tickettype(rs.getInt("id"), rs.getString("name")));
            }
        }

        return ticketTypes;
    }

    public List<Integer> getTicketById(int eventId) throws SQLException {
        List<Integer> tickets = new ArrayList<>();

        try (Connection conn = JdbcUtils.getConnection()) {
            String sql = "SELECT* FROM ticket WHERE event_id = ?";
            PreparedStatement stm = conn.prepareCall(sql);

            stm.setInt(1, eventId);

            ResultSet rs = stm.executeQuery();
            while (rs.next()) {
                tickets.add(rs.getInt("id"));
            }
        }
        return tickets;
    }

    public boolean addTickets(List<Ticket> tickets) throws SQLException {
        boolean success = false;
        try (Connection conn = JdbcUtils.getConnection()) {
            conn.setAutoCommit(false);

            String sql = "INSERT INTO event (name, start_date, end_date, max_attendees, venue_id) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement stm = conn.prepareCall(sql);

            stm.setString(1, tickets.get(0).getEventId().getName());
            stm.setTimestamp(2, tickets.get(0).getEventId().getStartDate());
            stm.setTimestamp(3, tickets.get(0).getEventId().getEndDate());
            stm.setInt(4, tickets.get(0).getEventId().getMaxAttendees());
            stm.setInt(5, tickets.get(0).getEventId().getVenue().getId());

            int rowsInserted = stm.executeUpdate();

            if (rowsInserted > 0) {

                ResultSet regKeys = stm.getGeneratedKeys();
                if (regKeys.next()) {
                    int eventId = regKeys.getInt(1);
                    String sql_ = "INSERT INTO ticket (event_id,ticket_type_id,quantity,price) VALUES (?,?,?,?)";
                    PreparedStatement stm_ = conn.prepareCall(sql_);
                    for (Ticket ticket : tickets) {

                        stm_.setInt(1, eventId);
                        stm_.setInt(2, ticket.getTicketTypeId().getId());
                        stm_.setInt(3, ticket.getQuantity());
                        stm_.setBigDecimal(4, ticket.getPrice());
                        stm_.executeUpdate();
                    }

                }
            }
            conn.commit();
            success = true;
        }
        return success;
    }

    public boolean updateTickets(List<Ticket> tickets) throws SQLException {
        boolean success = false;
        try (Connection conn = JdbcUtils.getConnection()) {
            conn.setAutoCommit(false);

            String sql = "UPDATE event SET name = ?, start_date = ? , end_date = ?, max_attendees = ? , venue_id = ?, is_active = ? WHERE id = ?";
            PreparedStatement stm = conn.prepareCall(sql);

            stm.setString(1, tickets.get(0).getEventId().getName());
            stm.setTimestamp(2, tickets.get(0).getEventId().getStartDate());
            stm.setTimestamp(3, tickets.get(0).getEventId().getEndDate());
            stm.setInt(4, tickets.get(0).getEventId().getMaxAttendees());
            stm.setInt(5, tickets.get(0).getEventId().getVenue().getId());
            stm.setBoolean(6, tickets.get(0).getEventId().getIsActive());
            stm.setInt(7, tickets.get(0).getEventId().getId());

            int rowsInserted = stm.executeUpdate();

            if (rowsInserted > 0) {

                String sql_ = "UPDATE ticket SET quantity = ?, price = ? WHERE id = ?";
                PreparedStatement stm_ = conn.prepareCall(sql_);
                for (Ticket ticket : tickets) {

                    stm_.setInt(1, ticket.getQuantity());
                    stm_.setBigDecimal(2, ticket.getPrice());
                    stm_.setInt(3, ticket.getId());
                    stm_.executeUpdate();
                }
            }
            conn.commit();
            success = true;
        }
        return success;
    }

    public List<Ticket> getTicketByEventId(int eventId) throws SQLException {
        List<Ticket> tickets = new ArrayList<>();
        try (Connection conn = JdbcUtils.getConnection()) {
            String sql = "SELECT*FROM ticket WHERE event_id = ?";
            PreparedStatement stm = conn.prepareCall(sql);
            stm.setInt(1, eventId);

            ResultSet rs = stm.executeQuery();

            while (rs.next()) {
                tickets.add(new Ticket(rs.getInt("id"),
                        rs.getInt("quantity"),
                        rs.getBigDecimal("price")));
            }
        }

        return tickets;
    }
}
