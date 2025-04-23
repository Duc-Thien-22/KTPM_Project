package com.ntn.services;

import com.ntn.pojo.DTO.EventDTO;
import com.ntn.pojo.DTO.NotificationDTO;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;


public class RegisterUserServices {
    private final EventServices eventServices;
    private final NotificationServices notificationServices;
    
    public RegisterUserServices() {
        this.eventServices = new EventServices();
        this.notificationServices = new NotificationServices();
    }
    

    public List<EventDTO> getEvents(String keyword) throws SQLException {
        return eventServices.getEvents(keyword);
    }
    

    public boolean checkTimeConflict(int eventId, int userId) throws SQLException {
        return eventServices.checkStatusRegis(eventId, userId);
    }
    

    public Map<String, Object[]> getTicketInfo(int eventId) {
        return eventServices.getTicketInfo(eventId);
    }
    

    public boolean processPayment(int eventId, String ticketType, int userId) {
        return eventServices.processPayment(eventId, ticketType, userId);
    }

    public List<NotificationDTO> getNotifications(int userId) throws SQLException {
        List<NotificationDTO> notifications = notificationServices.getNotifications(userId);
        Collections.sort(notifications, (n1, n2) -> n2.getCreatedDate().compareTo(n1.getCreatedDate()));
        return notifications;
    }
    

    public List<EventDTO> getRegisteredEvents(int userId) throws SQLException {
        try {
            return eventServices.getEvents(userId);
        } catch (SQLException ex) {
            Logger.getLogger(RegisterUserServices.class.getName()).log(Level.SEVERE, 
                    "Lỗi khi lấy danh sách sự kiện đã đăng ký", ex);
            throw ex;
        }
    }
}

