/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ntn.pojo;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *
 * @author NHAT
 */
@Entity
@Table(name = "user_notification")
@NamedQueries({
    @NamedQuery(name = "UserNotification.findAll", query = "SELECT u FROM UserNotification u"),
    @NamedQuery(name = "UserNotification.findByUserId", query = "SELECT u FROM UserNotification u WHERE u.userNotificationPK.userId = :userId"),
    @NamedQuery(name = "UserNotification.findByNotificationId", query = "SELECT u FROM UserNotification u WHERE u.userNotificationPK.notificationId = :notificationId"),
    @NamedQuery(name = "UserNotification.findByReadStatus", query = "SELECT u FROM UserNotification u WHERE u.readStatus = :readStatus"),
    @NamedQuery(name = "UserNotification.findByReceivedAt", query = "SELECT u FROM UserNotification u WHERE u.receivedAt = :receivedAt")})
public class UserNotification implements Serializable {

    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected UserNotificationPK userNotificationPK;
    @Column(name = "read_status")
    private Boolean readStatus;
    @Column(name = "received_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date receivedAt;
    @JoinColumn(name = "notification_id", referencedColumnName = "id", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private Notification notification;
    @JoinColumn(name = "user_id", referencedColumnName = "id", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private User user;

    public UserNotification() {
    }

    public UserNotification(UserNotificationPK userNotificationPK) {
        this.userNotificationPK = userNotificationPK;
    }

    public UserNotification(int userId, int notificationId) {
        this.userNotificationPK = new UserNotificationPK(userId, notificationId);
    }

    public UserNotificationPK getUserNotificationPK() {
        return userNotificationPK;
    }

    public void setUserNotificationPK(UserNotificationPK userNotificationPK) {
        this.userNotificationPK = userNotificationPK;
    }

    public Boolean getReadStatus() {
        return readStatus;
    }

    public void setReadStatus(Boolean readStatus) {
        this.readStatus = readStatus;
    }

    public Date getReceivedAt() {
        return receivedAt;
    }

    public void setReceivedAt(Date receivedAt) {
        this.receivedAt = receivedAt;
    }

    public Notification getNotification() {
        return notification;
    }

    public void setNotification(Notification notification) {
        this.notification = notification;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (userNotificationPK != null ? userNotificationPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof UserNotification)) {
            return false;
        }
        UserNotification other = (UserNotification) object;
        if ((this.userNotificationPK == null && other.userNotificationPK != null) || (this.userNotificationPK != null && !this.userNotificationPK.equals(other.userNotificationPK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.ntn.pojo.UserNotification[ userNotificationPK=" + userNotificationPK + " ]";
    }
    
}
