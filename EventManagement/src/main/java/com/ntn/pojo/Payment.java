/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ntn.pojo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 *
 * @author NHAT
 */
@javax.persistence.Entity
@javax.persistence.Table(name = "payment")
@javax.persistence.NamedQueries({
    @javax.persistence.NamedQuery(name = "Payment.findAll", query = "SELECT p FROM Payment p"),
    @javax.persistence.NamedQuery(name = "Payment.findById", query = "SELECT p FROM Payment p WHERE p.id = :id"),
    @javax.persistence.NamedQuery(name = "Payment.findByTicketQuantity", query = "SELECT p FROM Payment p WHERE p.ticketQuantity = :ticketQuantity"),
    @javax.persistence.NamedQuery(name = "Payment.findByTotalAmount", query = "SELECT p FROM Payment p WHERE p.totalAmount = :totalAmount"),
    @javax.persistence.NamedQuery(name = "Payment.findByIsPayment", query = "SELECT p FROM Payment p WHERE p.isPayment = :isPayment"),
    @javax.persistence.NamedQuery(name = "Payment.findByCreatedDate", query = "SELECT p FROM Payment p WHERE p.createdDate = :createdDate"),
    @javax.persistence.NamedQuery(name = "Payment.findByUpdatedDate", query = "SELECT p FROM Payment p WHERE p.updatedDate = :updatedDate")})
public class Payment implements Serializable {

    private static final long serialVersionUID = 1L;
    @javax.persistence.Id
    @javax.persistence.GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
    @javax.persistence.Basic(optional = false)
    @javax.persistence.Column(name = "id")
    private Integer id;
    @javax.persistence.Basic(optional = false)
    @javax.persistence.Column(name = "ticket_quantity")
    private int ticketQuantity;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @javax.persistence.Basic(optional = false)
    @javax.persistence.Column(name = "total_amount")
    private BigDecimal totalAmount;
    @javax.persistence.Column(name = "is_payment")
    private Boolean isPayment;
    @javax.persistence.Column(name = "created_date")
    @javax.persistence.Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date createdDate;
    @javax.persistence.Column(name = "updated_date")
    @javax.persistence.Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date updatedDate;
    @javax.persistence.JoinColumn(name = "event_id", referencedColumnName = "id")
    @javax.persistence.ManyToOne(optional = false)
    private Event eventId;
    @javax.persistence.JoinColumn(name = "user_id", referencedColumnName = "id")
    @javax.persistence.ManyToOne(optional = false)
    private User userId;

    public Payment() {
    }

    public Payment(Integer id) {
        this.id = id;
    }

    public Payment(Integer id, int ticketQuantity, BigDecimal totalAmount) {
        this.id = id;
        this.ticketQuantity = ticketQuantity;
        this.totalAmount = totalAmount;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public int getTicketQuantity() {
        return ticketQuantity;
    }

    public void setTicketQuantity(int ticketQuantity) {
        this.ticketQuantity = ticketQuantity;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public Boolean getIsPayment() {
        return isPayment;
    }

    public void setIsPayment(Boolean isPayment) {
        this.isPayment = isPayment;
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
        if (!(object instanceof Payment)) {
            return false;
        }
        Payment other = (Payment) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.ntn.pojo.Payment[ id=" + id + " ]";
    }
    
}
