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

    //lay thong tin su kien
//    public List<Event> getEvent() throws SQLException {
//        List<Event> events = new ArrayList<>();
//        try (Connection conn = JdbcUtils.getConnection()) {
//            PreparedStatement stm = conn.prepareStatement(
//                    "SELECT e.id AS event_id, e.name AS event_name, e.start_date, e.end_date, e.max_attendees, "
//                    + "v.id AS venue_id, v.name AS venue_name, "
//                    + "(e.max_attendees - COALESCE(SUM(t.quantity), 0)) AS registerUser " // Đếm số lượng đăng ký, tránh NULL
//                    + "FROM event e "
//                    + "JOIN venue v ON e.venue_id = v.id "
//                    + "LEFT JOIN ticket t ON t.event_id = e.id "
//                    + "WHERE e.is_active = 1 "
//                    + "GROUP BY e.id, e.name, e.start_date, e.end_date, e.max_attendees, v.id, v.name " // Đảm bảo GROUP BY
//            );
//
//            ResultSet rs = stm.executeQuery();
//
//            while (rs.next()) {
//
//                Event e = new Event(
//                        rs.getInt("event_id"), // Lấy đúng id của event
//                        rs.getString("event_name"), // Lấy đúng tên event
//                        rs.getTimestamp("start_date"),
//                        rs.getTimestamp("end_date"),
//                        rs.getInt("max_attendees")
//                );
//                e.setRegisterUser(rs.getInt("registerUser"));
//                e.setVenue(new Venue(rs.getInt("venue_id"), rs.getString("venue_name"), 0));
//                events.add(e);
//            }
//        }
//        return events;
//    }
    
    public List<Event> getEvent(String kw) throws SQLException {
    List<Event> events = new ArrayList<>();
    String sql = "SELECT e.id AS event_id, e.name AS event_name, e.start_date, e.end_date, e.max_attendees, "
               + "v.id AS venue_id, v.name AS venue_name, "
               + "(e.max_attendees - COALESCE(SUM(t.quantity), 0)) AS registerUser "
               + "FROM event e "
               + "JOIN venue v ON e.venue_id = v.id "
               + "LEFT JOIN ticket t ON t.event_id = e.id "
               + "WHERE e.is_active = 1 ";

    // Nếu có từ khóa tìm kiếm
    if (kw != null && !kw.trim().isEmpty()) {
        sql += " AND (e.name LIKE ?) ";
    }
    
    sql += " GROUP BY e.id, e.name, e.start_date, e.end_date, e.max_attendees, v.id, v.name";

    try (Connection conn = JdbcUtils.getConnection();
         PreparedStatement stm = conn.prepareStatement(sql)) {
        
        if (kw != null && !kw.trim().isEmpty()) {
            String searchKey = "%" + kw + "%";
            stm.setString(1, searchKey);
        }

        ResultSet rs = stm.executeQuery();
        while (rs.next()) {
            Event e = new Event(
                rs.getInt("event_id"),
                rs.getString("event_name"),
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


    //lay thong tin ve
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
                ticketInfo.put(type, new Object[]{quantity, price});
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return ticketInfo;
    }

    public boolean processPayment(int eventId, String ticketType, int userId) {
        try (Connection conn = JdbcUtils.getConnection()) {
            conn.setAutoCommit(false); // Bắt đầu transaction

            // Lấy ticket_id từ eventId và ticketType
            String getTicketIdSQL = "SELECT t.id FROM ticket t "
                    + "JOIN tickettype tt ON t.ticket_type_id = tt.id "
                    + "WHERE t.event_id = ? AND tt.name = ? AND t.quantity > 0 LIMIT 1";
            PreparedStatement getTicketIdStmt = conn.prepareStatement(getTicketIdSQL);
            getTicketIdStmt.setInt(1, eventId);
            getTicketIdStmt.setString(2, ticketType);
            ResultSet rs = getTicketIdStmt.executeQuery();

            if (!rs.next()) {
                conn.rollback(); // Hủy transaction nếu không tìm thấy vé
                return false;
            }

            int ticketId = rs.getInt("id");

            // Cập nhật số lượng vé
            String updateTicketSQL = "UPDATE ticket SET quantity = quantity - 1 WHERE id = ?";
            PreparedStatement updateTicketStmt = conn.prepareStatement(updateTicketSQL);
            updateTicketStmt.setInt(1, ticketId);
            int ticketUpdated = updateTicketStmt.executeUpdate();
            if (ticketUpdated == 0) {
                conn.rollback();
                return false;
            }

            // Thêm vào bảng registration
            String insertRegistrationSQL = "INSERT INTO registration (user_id, ticket_id, created_date, updated_date) "
                    + "VALUES (?, ?, NOW(), NOW())";
            PreparedStatement insertRegStmt = conn.prepareStatement(insertRegistrationSQL, PreparedStatement.RETURN_GENERATED_KEYS);
            insertRegStmt.setInt(1, userId);
            insertRegStmt.setInt(2, ticketId);
            int regInserted = insertRegStmt.executeUpdate();

            if (regInserted == 0) {
                conn.rollback();
                return false;
            }

            // Lấy registration_id vừa tạo
            ResultSet regKeys = insertRegStmt.getGeneratedKeys();
            int registrationId;
            if (regKeys.next()) {
                registrationId = regKeys.getInt(1);
            } else {
                conn.rollback();
                return false;
            }

            // Thêm vào bảng payment
            String insertPaymentSQL = "INSERT INTO payment (register_id, is_payment, is_refunded,created_date, updated_date) "
                    + "VALUES (?, 1, 0, NOW(), NOW())";
            PreparedStatement insertPayStmt = conn.prepareStatement(insertPaymentSQL);
            insertPayStmt.setInt(1, registrationId);
            int payInserted = insertPayStmt.executeUpdate();

            if (payInserted == 0) {
                conn.rollback();
                return false;
            }

            conn.commit(); // Xác nhận transaction
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean checkStatusRegis(int eventId, int userId) throws SQLException{
        try (Connection conn = JdbcUtils.getConnection()){
            PreparedStatement stm = conn.prepareStatement("SELECT COUNT(*) FROM registration r "
                    + "JOIN ticket t on t.id = r.ticket_id "
                    + "JOIN event e1 on t.event_id = e1.id "
                    + "JOIN event e2 on e2.id = ? "
                    + "WHERE r.user_id = ? "
                    + "AND (e1.start_date < e2.end_date and e1.end_date > e2.start_date)");
            stm.setInt(1,eventId);
            stm.setInt(2, userId);
            ResultSet rs = stm.executeQuery();
            if(rs.next()){
                return rs.getInt(1) > 0;
            }
            return false; // Không có xung đột
        }
    }
    
    
}
