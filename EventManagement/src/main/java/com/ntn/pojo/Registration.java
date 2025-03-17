/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ntn.pojo;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author NHAT
 */
@javax.persistence.Entity
@javax.persistence.Table(name = "registration")
@javax.persistence.NamedQueries({
    @javax.persistence.NamedQuery(name = "Registration.findAll", query = "SELECT r FROM Registration r"),
    @javax.persistence.NamedQuery(name = "Registration.findById", query = "SELECT r FROM Registration r WHERE r.id = :id"),
    @javax.persistence.NamedQuery(name = "Registration.findByCreatedDate", query = "SELECT r FROM Registration r WHERE r.createdDate = :createdDate"),
    @javax.persistence.NamedQuery(name = "Registration.findByUpdatedDate", query = "SELECT r FROM Registration r WHERE r.updatedDate = :updatedDate")})
public class Registration implements Serializable {

    private static final long serialVersionUID = 1L;
    @javax.persistence.Id
    @javax.persistence.GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
    @javax.persistence.Basic(optional = false)
    @javax.persistence.Column(name = "id")
    private Integer id;
    @javax.persistence.Column(name = "created_date")
    @javax.persistence.Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date createdDate;
    @javax.persistence.Column(name = "updated_date")
    @javax.persistence.Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date updatedDate;
    @javax.persistence.JoinColumn(name = "event_id", referencedColumnName = "id")
    @javax.persistence.ManyToOne(optional = false)
    private Event eventId;
    @javax.persistence.JoinColumn(name = "ticket_id", referencedColumnName = "id")
    @javax.persistence.ManyToOne(optional = false)
    private Ticket ticketId;
    @javax.persistence.JoinColumn(name = "user_id", referencedColumnName = "id")
    @javax.persistence.ManyToOne(optional = false)
    private User userId;

    public Registration() {
    }

    public Registration(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public Event getEventId() {
        return eventId;
    }

    public void setEventId(Event eventId) {
        this.eventId = eventId;
    }

    public Ticket getTicketId() {
        return ticketId;
    }

    public void setTicketId(Ticket ticketId) {
        this.ticketId = ticketId;
    }

    public User getUserId() {
        return userId;
    }

    public void setUserId(User userId) {
        this.userId = userId;
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
        if (!(object instanceof Registration)) {
            return false;
        }
        Registration other = (Registration) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.ntn.pojo.Registration[ id=" + id + " ]";
    }
    
}
