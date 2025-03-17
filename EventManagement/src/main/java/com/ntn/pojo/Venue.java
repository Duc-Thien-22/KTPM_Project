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
@javax.persistence.Table(name = "venue")
@javax.persistence.NamedQueries({
    @javax.persistence.NamedQuery(name = "Venue.findAll", query = "SELECT v FROM Venue v"),
    @javax.persistence.NamedQuery(name = "Venue.findById", query = "SELECT v FROM Venue v WHERE v.id = :id"),
    @javax.persistence.NamedQuery(name = "Venue.findByName", query = "SELECT v FROM Venue v WHERE v.name = :name"),
    @javax.persistence.NamedQuery(name = "Venue.findByCapacity", query = "SELECT v FROM Venue v WHERE v.capacity = :capacity"),
    @javax.persistence.NamedQuery(name = "Venue.findByCreatedDate", query = "SELECT v FROM Venue v WHERE v.createdDate = :createdDate"),
    @javax.persistence.NamedQuery(name = "Venue.findByUpdatedDate", query = "SELECT v FROM Venue v WHERE v.updatedDate = :updatedDate")})
public class Venue implements Serializable {

    private static final long serialVersionUID = 1L;
    @javax.persistence.Id
    @javax.persistence.GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
    @javax.persistence.Basic(optional = false)
    @javax.persistence.Column(name = "id")
    private Integer id;
    @javax.persistence.Basic(optional = false)
    @javax.persistence.Column(name = "name")
    private String name;
    @javax.persistence.Basic(optional = false)
    @javax.persistence.Column(name = "capacity")
    private int capacity;
    @javax.persistence.Column(name = "created_date")
    @javax.persistence.Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date createdDate;
    @javax.persistence.Column(name = "updated_date")
    @javax.persistence.Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date updatedDate;
    @javax.persistence.OneToMany(mappedBy = "venueId")
    private Set<Event> eventSet;

    public Venue() {
    }

    public Venue(Integer id) {
        this.id = id;
    }

    public Venue(Integer id, String name, int capacity) {
        this.id = id;
        this.name = name;
        this.capacity = capacity;
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

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
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

    public Set<Event> getEventSet() {
        return eventSet;
    }

    public void setEventSet(Set<Event> eventSet) {
        this.eventSet = eventSet;
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
        if (!(object instanceof Venue)) {
            return false;
        }
        Venue other = (Venue) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.ntn.pojo.Venue[ id=" + id + " ]";
    }
    
}
