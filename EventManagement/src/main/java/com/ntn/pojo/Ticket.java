/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ntn.pojo;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

/**
 *
 * @author NHAT
 */
@javax.persistence.Entity
@javax.persistence.Table(name = "ticket")
@javax.persistence.NamedQueries({
    @javax.persistence.NamedQuery(name = "Ticket.findAll", query = "SELECT t FROM Ticket t"),
    @javax.persistence.NamedQuery(name = "Ticket.findById", query = "SELECT t FROM Ticket t WHERE t.id = :id"),
    @javax.persistence.NamedQuery(name = "Ticket.findByQuantity", query = "SELECT t FROM Ticket t WHERE t.quantity = :quantity"),
    @javax.persistence.NamedQuery(name = "Ticket.findByCreatedDate", query = "SELECT t FROM Ticket t WHERE t.createdDate = :createdDate"),
    @javax.persistence.NamedQuery(name = "Ticket.findByUpdatedDate", query = "SELECT t FROM Ticket t WHERE t.updatedDate = :updatedDate")})
public class Ticket implements Serializable {

    private static final long serialVersionUID = 1L;
    @javax.persistence.Id
    @javax.persistence.GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
    @javax.persistence.Basic(optional = false)
    @javax.persistence.Column(name = "id")
    private Integer id;
    @javax.persistence.Basic(optional = false)
    @javax.persistence.Column(name = "quantity")
    private int quantity;
    @javax.persistence.Column(name = "created_date")
    @javax.persistence.Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date createdDate;
    @javax.persistence.Column(name = "updated_date")
    @javax.persistence.Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date updatedDate;
    @javax.persistence.JoinColumn(name = "event_id", referencedColumnName = "id")
    @javax.persistence.ManyToOne(optional = false)
    private Event eventId;
    @javax.persistence.JoinColumn(name = "ticket_type_id", referencedColumnName = "id")
    @javax.persistence.ManyToOne(optional = false)
    private Tickettype ticketTypeId;
    @javax.persistence.OneToMany(cascade = javax.persistence.CascadeType.ALL, mappedBy = "ticketId")
    private Set<Registration> registrationSet;

    public Ticket() {
    }

    public Ticket(Integer id) {
        this.id = id;
    }

    public Ticket(Integer id, int quantity) {
        this.id = id;
        this.quantity = quantity;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
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

    public Tickettype getTicketTypeId() {
        return ticketTypeId;
    }

    public void setTicketTypeId(Tickettype ticketTypeId) {
        this.ticketTypeId = ticketTypeId;
    }

    public Set<Registration> getRegistrationSet() {
        return registrationSet;
    }

    public void setRegistrationSet(Set<Registration> registrationSet) {
        this.registrationSet = registrationSet;
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
        if (!(object instanceof Ticket)) {
            return false;
        }
        Ticket other = (Ticket) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.ntn.pojo.Ticket[ id=" + id + " ]";
    }
    
}
