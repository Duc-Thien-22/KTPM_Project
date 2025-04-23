/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ntn.services;

import com.ntn.controllers.Utils;
import com.ntn.pojo.Event;
import com.ntn.pojo.Ticket;
import com.ntn.pojo.Tickettype;
import com.ntn.pojo.Venue;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;

/**
 *
 * @author NHAT
 */
public class EventTabServices {

    private int eventIdSelected = 0;
    private Map<String, Integer> venuesId = new HashMap<>();
    private final VenueServices venueServies = new VenueServices();
    private final EventServices eventServices = new EventServices();
    private final NotificationServices notificationServices = new NotificationServices();
    private final TicketServices ticketServices = new TicketServices();
    private final Map<Tickettype, HBox> ticketRows = new HashMap<>();
    private final int ONE_HOUR = 24 * 60 * 60 * 1000;

    public List<Venue> getVenues() throws SQLException {
        return this.getVenueServies().getVenues();
    }

    public String checkEventStatus(Event e) {
        if (!e.getIsActive()) {
            return "Sự kiện đã hết hạn !!";
        }
        return null;
    }

    public String checkValidAction(Event e) {
        Timestamp timestamp = new Timestamp(new Date().getTime());

        String statusMessage = this.checkEventStatus(e);
        if (statusMessage != null) {
            return statusMessage;
        }

        if (e.getStartDate().getTime() - timestamp.getTime() < getONE_HOUR()) {
            return "Sự kiện không được chỉnh hoặc hủy sau 24h";
        }
        return null;
    }

    public List<Ticket> getTicketsValid(String eventName, LocalDate startDateF, String startTimeF,
            LocalDate endDateF, String endTimeF, String venueName, String eventCapacity, String[] errorMsg) throws SQLException {
        List<Ticket> tickets = new ArrayList<>();

        if (this.isInputValid(eventName, startDateF, startTimeF, endDateF, endTimeF, venueName, eventCapacity)) {
            String name = eventName;

            LocalDate today = LocalDate.now();

            if (!this.isValidTimeFormat(startTimeF)) {
                errorMsg[0] = "Giờ không hợp lệ! Vui lòng nhập đúng định dạng HH:mm.";
                return null;
            }
            Timestamp startDate = Utils.convertToTimestamp(startDateF, startTimeF);
            if (startDate.toLocalDateTime().toLocalDate().isBefore(today.plusDays(1))) {
                errorMsg[0] = "Ngày và giờ bắt đầu phải lớn hơn ngày hiện tại";
                return null;
            }

            if (!this.isValidTimeFormat(endTimeF)) {
                errorMsg[0] = "Giờ không hợp lệ! Vui lòng nhập đúng định dạng HH:mm.";
                return null;
            }
            Timestamp endDate = Utils.convertToTimestamp(endDateF, endTimeF);
            if (endDate.before(startDate)) {
                errorMsg[0] = "Ngày và giờ kết thúc phải lớn hơn ngày bắt đầu";
                return null;
            }
            int venueId = this.getVenuesId().get(venueName.split(" - ")[0].trim());
            Event eventExist = this.getEventServices().checkVenueAndDateTime(venueId, startDate, endDate, this.getEventIdSelected());
            if (eventExist != null) {
                errorMsg[0] = String.format("Từ %s đến %s đang diễn ra sự kiên tại %s",
                        Utils.formatedDate(eventExist.getStartDate()),
                        Utils.formatedDate(eventExist.getEndDate()), eventExist.getVenue());
                return null;
            }

            if (!this.isNumeric(eventCapacity)) {
                errorMsg[0] = "Số lượng khách phải là số nguyên dương";
                return null;
            }
            int maxAttendess = Integer.parseInt(eventCapacity);
            if (maxAttendess > this.getVenueServies().getVenueById(venueId).getCapacity()) {
                errorMsg[0] = "Số lượng khách lớn hơn sức chứa của địa điểm";
                return null;
            }

            Event event = new Event(name, startDate, endDate, maxAttendess);
            event.setVenue(this.getVenueServies().getVenueById(venueId));

            int totalNumberTickets = 0;
            for (Map.Entry<Tickettype, HBox> entry : this.getTicketRows().entrySet()) {
                Tickettype type = entry.getKey();
                HBox row = entry.getValue();

                TextField txtNumberTicket = (TextField) row.getChildren().get(1);
                TextField txtPriceTicket = (TextField) row.getChildren().get(2);

                if (!this.isNumeric(txtNumberTicket.getText()) || !this.isNumeric(txtPriceTicket.getText())) {
                    errorMsg[0] = "Số lượng và giá vé phải là chữ số";
                    return null;
                }

                totalNumberTickets += Integer.parseInt(txtNumberTicket.getText());
                Ticket t = new Ticket(Integer.parseInt(txtNumberTicket.getText()),
                        new BigDecimal(txtPriceTicket.getText()));
                t.setEventId(event);
                t.setTicketTypeId(type);

                tickets.add(t);
            }

            if (totalNumberTickets > maxAttendess) {
                errorMsg[0] = "Tổng số lượng vé khong được lớp hơn số khách mời";
                this.clearTicketType();
                return null;
            }

            return tickets;

        } else {
            errorMsg[0] = "Vui lòng nhập đầy đủ dữ liệu";
        }
        return null;
    }

    public boolean deleteEvent(int id) throws SQLException {
        int rs = this.getEventServices().deleteEventById(id);
        return rs > 0;
    }

    public boolean destroyEvent(Event e) throws SQLException {
        e.setIsActive(Boolean.FALSE);
        int rs = this.getEventServices().updateEventById(e);
        return rs > 0;
    }

    public boolean isInputValid(String eventName, LocalDate startDate, String startTime,
            LocalDate endDate, String endTime, String venueName, String eventCapacity) {
        boolean isValid = eventName != null && !eventName.trim().isEmpty()
                && startDate != null && startTime != null && !startTime.trim().isEmpty()
                && endDate != null && endTime != null && !endTime.trim().isEmpty()
                && venueName != null
                && eventCapacity != null;

        if (isValid) {
            for (HBox row : this.getTicketRows().values()) {
                TextField quantityField = (TextField) row.getChildren().get(1);
                TextField priceField = (TextField) row.getChildren().get(2);

                if (quantityField.getText().trim().isEmpty() || priceField.getText().trim().isEmpty()) {
                    isValid = false;
                    break;
                }
            }
        }

        return isValid;
    }

    public void resetEventData() throws SQLException {
        Timestamp timestamp = new Timestamp(new Date().getTime());
        for (Event e : this.getEventServices().getEvents(0, "")) {
            if (e.getEndDate().before(timestamp)) {
                e.setIsActive(Boolean.FALSE);
                this.getEventServices().updateEventById(e);
            }
        }
    }

    public void clearTicketType() {
        for (HBox row : this.getTicketRows().values()) {
            TextField quantityField = (TextField) row.getChildren().get(1);
            TextField priceField = (TextField) row.getChildren().get(2);

            quantityField.clear();
            priceField.clear();

        }
    }

    public Boolean sendNotification(int eventId, String content, String type) throws SQLException {
        int count = notificationServices.sendNotificationForUser(content, type, eventId);
        return count > 0;
    }

    public Boolean refundeMoney(List<Integer> registerIds) throws SQLException {
        int count = this.eventServices.refundeMoneyToUsers(registerIds);
        return count > 0;
    }

    public void clearDateTimeFields(DatePicker datePicker, TextField timeField) {
        datePicker.setValue(null);
        timeField.clear();
    }

    public boolean isNumeric(String str) {
        return str.matches("\\d+"); // Chỉ cho phép số nguyên dương
    }

    public boolean isValidTimeFormat(String time) {
        return time != null && time.matches("([01]\\d|2[0-3]):[0-5]\\d");
    }

    public void toggleButton(Button b) {
        b.setVisible(!b.isVisible());
        b.setManaged(b.isVisible());
    }

    /**
     * @return the eventIdSelected
     */
    public int getEventIdSelected() {
        return eventIdSelected;
    }

    /**
     * @param eventIdSelected the eventIdSelected to set
     */
    public void setEventIdSelected(int eventIdSelected) {
        this.eventIdSelected = eventIdSelected;
    }

    /**
     * @return the venuesId
     */
    public Map<String, Integer> getVenuesId() {
        return venuesId;
    }

    /**
     * @param venuesId the venuesId to set
     */
    public void setVenuesId(Map<String, Integer> venuesId) {
        this.venuesId = venuesId;
    }

    /**
     * @return the venueServies
     */
    public VenueServices getVenueServies() {
        return venueServies;
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
     * @return the ticketServices
     */
    public TicketServices getTicketServices() {
        return ticketServices;
    }

    /**
     * @return the ticketRows
     */
    public Map<Tickettype, HBox> getTicketRows() {
        return ticketRows;
    }

    /**
     * @return the ONE_HOUR
     */
    public int getONE_HOUR() {
        return ONE_HOUR;
    }
}
