/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ntn.pojo.DTO;

import com.ntn.pojo.Venue;
import java.sql.Timestamp;

/**
 *
 * @author admin
 */
public class EventDTO {

    private int id;
    private String name;
    private Timestamp startDate;
    private Timestamp endDate;
    private int maxAttendees;
    private int registeredUser;
    private Venue venue;
    private String ticketName;

    public EventDTO(int id, String name, Timestamp startDate, Timestamp endDate, int maxAttendees, int registeredUser, Venue venue) {
        this.id = id;
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
        this.maxAttendees = maxAttendees;
        this.registeredUser = registeredUser;
        this.venue = venue;
    }
    
    public EventDTO(int id, String name, Timestamp startDate, Timestamp endDate, Venue venue, String ticketName) {
        this.id = id;
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
        this.venue = venue;
        this.ticketName = ticketName;
    }

    /**
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the startDate
     */
    public Timestamp getStartDate() {
        return startDate;
    }

    /**
     * @param startDate the startDate to set
     */
    public void setStartDate(Timestamp startDate) {
        this.startDate = startDate;
    }

    /**
     * @return the endDate
     */
    public Timestamp getEndDate() {
        return endDate;
    }

    /**
     * @param endDate the endDate to set
     */
    public void setEndDate(Timestamp endDate) {
        this.endDate = endDate;
    }

    /**
     * @return the maxAttendees
     */
    public int getMaxAttendees() {
        return maxAttendees;
    }

    /**
     * @param maxAttendees the maxAttendees to set
     */
    public void setMaxAttendees(int maxAttendees) {
        this.maxAttendees = maxAttendees;
    }

    /**
     * @return the registeredUser
     */
    public int getRegisteredUser() {
        return registeredUser;
    }

    /**
     * @param registeredUser the registeredUser to set
     */
    public void setRegisteredUser(int registeredUser) {
        this.registeredUser = registeredUser;
    }

    /**
     * @return the venue
     */
    public Venue getVenue() {
        return venue;
    }

    /**
     * @param venue the venue to set
     */
    public void setVenue(Venue venue) {
        this.venue = venue;
    }

    /**
     * @return the ticketName
     */
    public String getTicketName() {
        return ticketName;
    }

    /**
     * @param ticketName the ticketName to set
     */
    public void setTicketName(String ticketName) {
        this.ticketName = ticketName;
    }

}
