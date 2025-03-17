/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ntn.pojo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Set;

/**
 *
 * @author NHAT
 */
@javax.persistence.Entity
@javax.persistence.Table(name = "tickettype")
@javax.persistence.NamedQueries({
    @javax.persistence.NamedQuery(name = "Tickettype.findAll", query = "SELECT t FROM Tickettype t"),
    @javax.persistence.NamedQuery(name = "Tickettype.findById", query = "SELECT t FROM Tickettype t WHERE t.id = :id"),
    @javax.persistence.NamedQuery(name = "Tickettype.findByName", query = "SELECT t FROM Tickettype t WHERE t.name = :name"),
    @javax.persistence.NamedQuery(name = "Tickettype.findByPrice", query = "SELECT t FROM Tickettype t WHERE t.price = :price"),
    @javax.persistence.NamedQuery(name = "Tickettype.findByCreatedDate", query = "SELECT t FROM Tickettype t WHERE t.createdDate = :createdDate"),
    @javax.persistence.NamedQuery(name = "Tickettype.findByUpdatedDate", query = "SELECT t FROM Tickettype t WHERE t.updatedDate = :updatedDate")})
public class Tickettype implements Serializable {

    private static final long serialVersionUID = 1L;
    @javax.persistence.Id
    @javax.persistence.GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
    @javax.persistence.Basic(optional = false)
    @javax.persistence.Column(name = "id")
    private Integer id;
    @javax.persistence.Basic(optional = false)
    @javax.persistence.Column(name = "name")
    private String name;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @javax.persistence.Basic(optional = false)
    @javax.persistence.Column(name = "price")
    private BigDecimal price;
    @javax.persistence.Column(name = "created_date")
    @javax.persistence.Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date createdDate;
    @javax.persistence.Column(name = "updated_date")
    @javax.persistence.Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date updatedDate;
    @javax.persistence.OneToMany(cascade = javax.persistence.CascadeType.ALL, mappedBy = "ticketTypeId")
    private Set<Ticket> ticketSet;

    public Tickettype() {
    }

    public Tickettype(Integer id) {
        this.id = id;
    }

    public Tickettype(Integer id, String name, BigDecimal price) {
        this.id = id;
        this.name = name;
        this.price = price;
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

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
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

    public Set<Ticket> getTicketSet() {
        return ticketSet;
    }

    public void setTicketSet(Set<Ticket> ticketSet) {
        this.ticketSet = ticketSet;
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
        if (!(object instanceof Tickettype)) {
            return false;
        }
        Tickettype other = (Tickettype) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.ntn.pojo.Tickettype[ id=" + id + " ]";
    }
    
}
