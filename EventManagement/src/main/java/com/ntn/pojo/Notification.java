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
@javax.persistence.Table(name = "notification")
@javax.persistence.NamedQueries({
    @javax.persistence.NamedQuery(name = "Notification.findAll", query = "SELECT n FROM Notification n"),
    @javax.persistence.NamedQuery(name = "Notification.findById", query = "SELECT n FROM Notification n WHERE n.id = :id"),
    @javax.persistence.NamedQuery(name = "Notification.findByCreatedDate", query = "SELECT n FROM Notification n WHERE n.createdDate = :createdDate"),
    @javax.persistence.NamedQuery(name = "Notification.findByUpdatedDate", query = "SELECT n FROM Notification n WHERE n.updatedDate = :updatedDate")})
public class Notification implements Serializable {

    private static final long serialVersionUID = 1L;
    @javax.persistence.Id
    @javax.persistence.GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
    @javax.persistence.Basic(optional = false)
    @javax.persistence.Column(name = "id")
    private Integer id;
    @javax.persistence.Basic(optional = false)
    @javax.persistence.Lob
    @javax.persistence.Column(name = "content")
    private String content;
    @javax.persistence.Column(name = "created_date")
    @javax.persistence.Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date createdDate;
    @javax.persistence.Column(name = "updated_date")
    @javax.persistence.Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date updatedDate;
    @javax.persistence.JoinColumn(name = "event_id", referencedColumnName = "id")
    @javax.persistence.ManyToOne(optional = false)
    private Event eventId;

    public Notification() {
    }

    public Notification(Integer id) {
        this.id = id;
    }

    public Notification(Integer id, String content) {
        this.id = id;
        this.content = content;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
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

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Notification)) {
            return false;
        }
        Notification other = (Notification) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.ntn.pojo.Notification[ id=" + id + " ]";
    }
    
}
