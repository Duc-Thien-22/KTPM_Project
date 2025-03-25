/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ntn.pojo;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Set;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *
 * @author NHAT
 */
@Entity
@Table(name = "event")
@NamedQueries({
    @NamedQuery(name = "Event.findAll", query = "SELECT e FROM Event e"),
    @NamedQuery(name = "Event.findById", query = "SELECT e FROM Event e WHERE e.id = :id"),
    @NamedQuery(name = "Event.findByName", query = "SELECT e FROM Event e WHERE e.name = :name"),
    @NamedQuery(name = "Event.findByStartDate", query = "SELECT e FROM Event e WHERE e.startDate = :startDate"),
    @NamedQuery(name = "Event.findByEndDate", query = "SELECT e FROM Event e WHERE e.endDate = :endDate"),
    @NamedQuery(name = "Event.findByMaxAttendees", query = "SELECT e FROM Event e WHERE e.maxAttendees = :maxAttendees"),
    @NamedQuery(name = "Event.findByIsActive", query = "SELECT e FROM Event e WHERE e.isActive = :isActive"),
    @NamedQuery(name = "Event.findByCreatedDate", query = "SELECT e FROM Event e WHERE e.createdDate = :createdDate"),
    @NamedQuery(name = "Event.findByUpdatedDate", query = "SELECT e FROM Event e WHERE e.updatedDate = :updatedDate")})
public class Event implements Serializable {
    
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Basic(optional = false)
    @Column(name = "name")
    private String name;
    @Basic(optional = false)
    @Column(name = "start_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Timestamp startDate;
    @Basic(optional = false)
    @Column(name = "end_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Timestamp endDate;
    @Basic(optional = false)
    @Column(name = "max_attendees")
    private int maxAttendees;
    @Column(name = "is_active")
    private Boolean isActive;
    @Column(name = "created_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdDate;
    @Column(name = "updated_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedDate;
    @JoinTable(name = "event_notification", joinColumns = {
        @JoinColumn(name = "event_id", referencedColumnName = "id")}, inverseJoinColumns = {
        @JoinColumn(name = "notification_id", referencedColumnName = "id")})
    @ManyToMany
    private Set<Notification> notificationSet;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "eventId")
    private Set<Ticket> ticketSet;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "eventId")
    private Set<Payment> paymentSet;
    @JoinColumn(name = "venue_id", referencedColumnName = "id")
    @ManyToOne
    private Venue venue;

    public Event() {
    }

    public Event(Integer id) {
        this.id = id;
    }

    public Event(Integer id, String name, Timestamp startDate, Timestamp endDate, int maxAttendees) {
        this.id = id;
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
        this.maxAttendees = maxAttendees;
    }
    public Event(String name, Timestamp startDate, Timestamp endDate, int maxAttendees) {
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
        this.maxAttendees = maxAttendees;
    }

    
    public Event(Integer id, String name, Timestamp startDate, Timestamp endDate, int maxAttendees, boolean is_active) {
        this.id = id;
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
        this.maxAttendees = maxAttendees;
        this.isActive = is_active;
    }
    
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Timestamp getStartDate() {
        return startDate;
    }

    public void setStartDate(Timestamp startDate) {
        this.startDate = startDate;
    }

    public Timestamp getEndDate() {
        return endDate;
    }

    public void setEndDate(Timestamp endDate) {
        this.endDate = endDate;
    }

    public int getMaxAttendees() {
        return maxAttendees;
    }

    public void setMaxAttendees(int maxAttendees) {
        this.maxAttendees = maxAttendees;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Date getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(Date updatedDate) {
        this.updatedDate = updatedDate;
    }

    public Set<Notification> getNotificationSet() {
        return notificationSet;
    }

    public void setNotificationSet(Set<Notification> notificationSet) {
        this.notificationSet = notificationSet;
    }

    public Set<Ticket> getTicketSet() {
        return ticketSet;
    }

    public void setTicketSet(Set<Ticket> ticketSet) {
        this.ticketSet = ticketSet;
    }

    public Set<Payment> getPaymentSet() {
        return paymentSet;
    }

    public void setPaymentSet(Set<Payment> paymentSet) {
        this.paymentSet = paymentSet;
    }

    public Venue getVenue() {
        return venue;
    }

    public void setVenue(Venue venueId) {
        this.venue = venueId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Event)) {
            return false;
        }
        Event other = (Event) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.ntn.pojo.Event[ id=" + id + " ]";
    }
    
}
