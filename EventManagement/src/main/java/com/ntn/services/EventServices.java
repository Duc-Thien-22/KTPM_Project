/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ntn.services;

import com.ntn.pojo.DTO.EventDTO;
import com.ntn.pojo.Event;
import com.ntn.pojo.JdbcUtils;
import com.ntn.pojo.Venue;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    public Event checkVenueAndDateTime(int venueId, Timestamp startDate, Timestamp endDate, int eventId) throws SQLException {
        try (Connection conn = JdbcUtils.getConnection()) {
            String sql = "SELECT * FROM event WHERE venue_id = ? "
                    + "AND ( DATE_FORMAT(?, '%Y-%m-%d %H:%i') BETWEEN DATE_FORMAT(start_date, '%Y-%m-%d %H:%i') AND DATE_FORMAT(end_date, '%Y-%m-%d %H:%i') "
                    + "OR DATE_FORMAT(?, '%Y-%m-%d %H:%i') BETWEEN DATE_FORMAT(start_date, '%Y-%m-%d %H:%i') AND DATE_FORMAT(end_date, '%Y-%m-%d %H:%i') "
                    + "OR DATE_FORMAT(start_date, '%Y-%m-%d %H:%i') BETWEEN DATE_FORMAT(?, '%Y-%m-%d %H:%i') AND DATE_FORMAT(?, '%Y-%m-%d %H:%i') "
                    + "OR DATE_FORMAT(end_date, '%Y-%m-%d %H:%i') BETWEEN DATE_FORMAT(?, '%Y-%m-%d %H:%i') AND DATE_FORMAT(?, '%Y-%m-%d %H:%i') "
                    + " ) AND id != ?";
            PreparedStatement stm = conn.prepareCall(sql);
            stm.setInt(1, venueId);
            stm.setTimestamp(2, startDate);
            stm.setTimestamp(3, endDate);
            stm.setTimestamp(4, startDate);
            stm.setTimestamp(5, endDate);
            stm.setTimestamp(6, startDate);
            stm.setTimestamp(7, endDate);
            stm.setInt(8, eventId);

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
            while (rs.next()) {
                registerIds.add(rs.getInt("id"));
            }
        }
        return registerIds;
    }

    public int refundeMoneyToUsers(List<Integer> registerIds) throws SQLException {
        int updatedCount = 0;
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
                updatedCount = stm.executeUpdate();
            }
        }
        return updatedCount;
    }

    public List<EventDTO> getEvents(String kw) throws SQLException {
        List<EventDTO> events = new ArrayList<>();
        String sql = "SELECT e.id AS event_id, e.name AS event_name, e.start_date, e.end_date, e.max_attendees, "
                + "v.id AS venue_id, v.name AS venue_name, "
                + "(e.max_attendees - COALESCE(SUM(t.quantity), 0)) AS registerUser "
                + "FROM event e "
                + "JOIN venue v ON e.venue_id = v.id "
                + "LEFT JOIN ticket t ON t.event_id = e.id "
                + "WHERE e.start_date > Now() And e.is_active = 1 ";

        // Nếu có từ khóa tìm kiếm
        if (kw != null && !kw.trim().isEmpty()) {
            sql += " AND (e.name LIKE ?) ";
        }

        sql += " GROUP BY e.id, e.name, e.start_date, e.end_date, e.max_attendees, v.id, v.name";

        try (Connection conn = JdbcUtils.getConnection(); PreparedStatement stm = conn.prepareStatement(sql)) {

            if (kw != null && !kw.trim().isEmpty()) {
                String searchKey = "%" + kw + "%";
                stm.setString(1, searchKey);
            }

            ResultSet rs = stm.executeQuery();
            while (rs.next()) {
                EventDTO e = new EventDTO(rs.getInt("event_id"), rs.getString("event_name"), rs.getTimestamp("start_date"),
                        rs.getTimestamp("end_date"),
                        rs.getInt("max_attendees"),
                        rs.getInt("registerUser"),
                        new Venue(rs.getInt("venue_id"), rs.getString("venue_name"), 0)
                );
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

    public boolean checkStatusRegis(int eventId, int userId) throws SQLException {
        try (Connection conn = JdbcUtils.getConnection()) {
            PreparedStatement stm = conn.prepareStatement("SELECT COUNT(*) FROM registration r "
                    + "JOIN ticket t on t.id = r.ticket_id "
                    + "JOIN event e1 on t.event_id = e1.id "
                    + "JOIN event e2 on e2.id = ? "
                    + "WHERE r.user_id = ? "
                    + "AND (e1.start_date < e2.end_date and e1.end_date > e2.start_date)");
            stm.setInt(1, eventId);
            stm.setInt(2, userId);
            ResultSet rs = stm.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            return false; // Không có xung đột
        }
    }

    public List<EventDTO> getEvents(int userId) throws SQLException {
        List<EventDTO> events = new ArrayList<>();
        try (Connection conn = JdbcUtils.getConnection()) {
            String sql = "SELECT event.id, event.name,event.start_date, event.end_date,venue.id, venue.name, tickettype.name "
                    + "from event "
                    + "join venue on venue.id = event.venue_id "
                    + "join ticket on ticket.event_id = event.id "
                    + "join tickettype on ticket.ticket_type_id = tickettype.id "
                    + "join registration on registration.ticket_id = ticket.id "
                    + "where registration.user_id = ?";
            PreparedStatement stm = conn.prepareCall(sql);
            stm.setInt(1, userId);
            ResultSet rs = stm.executeQuery();
            while (rs.next()) {
                EventDTO e = new EventDTO(rs.getInt("event.id"),
                        rs.getString("event.name"),
                        rs.getTimestamp("event.start_date"),
                        rs.getTimestamp("event.end_date"),
                        new Venue(rs.getInt("venue.id"), rs.getString("venue.name"), 0),
                        rs.getString("tickettype.name")
                );

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
}
