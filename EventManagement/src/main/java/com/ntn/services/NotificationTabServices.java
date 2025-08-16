/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ntn.services;

import com.ntn.controllers.Utils;
import com.ntn.pojo.DTO.NotificationDTO;
import com.ntn.pojo.Event;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

/**
 *
 * @author NHAT
 */
public class NotificationTabServices {

    private final EventServices eventServices;
    private final NotificationServices notificationServices;
    private final int ONE_DAY = 24 * 60 * 60 * 1000;

    public NotificationTabServices() {
        this.eventServices = new EventServices();
        this.notificationServices = new NotificationServices();
    }
    
    

    public NotificationTabServices(EventServices eventServices, NotificationServices notificationServices) {
        this.eventServices = eventServices;
        this.notificationServices = notificationServices;
    }
    
    

    public List<Event> getEvents() throws SQLException {
        return getEventServices().getEvents();
    }
    
//
    public List<NotificationDTO> getNotificationHistory() throws SQLException {
        return getNotificationServices().getNotifications(null);
    }

    //
    public boolean sendNotification(int eventId, String content) throws SQLException {
        int count = notificationServices.sendNotificationForUser(content, "UPDATE", eventId);
        return count > 0;
    }

    //
    public String checkInputValid(Event e, String content) {
        if (e == null || content == null) {
            return "Vui lòng nhập dữ liệu !!";
        }
        return null;
    }

    //
    public int autoRemiderNotification() throws SQLException {
        int totalSent = 0;
        List<Event> events = this.eventServices.getEvents();
        Timestamp timestamp = new Timestamp(new Date().getTime());
        for (Event event : events) {
            List<Integer> registerIds = this.eventServices.getRegisterByEventId(event.getId());
            
            if (event.getIsActive()
                    && event.getStartDate().getTime() - timestamp.getTime() <= ONE_DAY
                    && !this.notificationServices.isUsersRemider(registerIds)) {
                
                totalSent += this.notificationServices.sendNotificationForUser(
                        Utils.getContentNotification("Sắp diễn ra", event), "REMINDER", event.getId());
            }
        }
        return totalSent;
    }
    
    

    /**
     * @return the eventServices
     */
    public EventServices getEventServices() {
        return eventServices;
    }

    /**
     * @return the notificationServices
     */
    public NotificationServices getNotificationServices() {
        return notificationServices;
    }

    /**
     * @return the ONE_HOUR
     */
    public int getONE_DAY() {
        return ONE_DAY;
    }
}
