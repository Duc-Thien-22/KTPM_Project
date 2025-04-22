/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.ntn.controllers;

import com.ntn.pojo.Event;
import com.ntn.pojo.Ticket;
import com.ntn.pojo.Tickettype;
import com.ntn.pojo.Venue;
import com.ntn.services.EventServices;
import com.ntn.services.NotificationServices;
import com.ntn.services.TicketServices;
import com.ntn.services.VenueServices;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * FXML Controller class
 *
 * @author NHAT
 */
public class EventTabController implements Initializable {

    @FXML
    private TextField eventNameField;
    @FXML
    private DatePicker startDatePicker;
    @FXML
    private TextField startTimeField;
    @FXML
    private DatePicker endDatePicker;
    @FXML
    private TextField endTimeField;
    @FXML
    private ComboBox<String> cbVenues;
//    private ComboBox<Venue> cbVenues;
    @FXML
    private TextField eventCapacityField;
    @FXML
    private TableView<Event> tbEvents;
    @FXML
    private Button btnUpdateEvent;
    @FXML
    private Button btnCreateEvent;
    @FXML
    private TextField txtSearch;
    @FXML
    private HBox hTicketType;

    private int eventIdSelected = 0;
    private final Map<String, Integer> venuesId = new HashMap<>();
    private final VenueServices venueServies = new VenueServices();
    private final EventServices eventServices = new EventServices();
    private final NotificationServices notificationServices = new NotificationServices();
    private final TicketServices ticketServices = new TicketServices();
    private final Map<Tickettype, HBox> ticketRows = new HashMap<>();
    private final int ONE_HOUR = 24 * 60 * 60 * 1000;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            // cap nhat lai su kien het han
            this.resetEventData();
            this.loadVenue();
            this.loadColumnEvents();
            this.loadEvent("");
            this.loadTicketType();
            this.txtSearch.textProperty().addListener(e -> {
                try {
                    this.loadEvent(this.txtSearch.getText());
                } catch (SQLException ex) {
                    Logger.getLogger(EventTabController.class.getName()).log(Level.SEVERE, null, ex);
                }
            });

            //toggleButton
            this.toggleButton(this.btnUpdateEvent);
        } catch (SQLException ex) {
            Logger.getLogger(EventTabController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void loadVenue() throws SQLException {
//        this.cbVenues.setItems(FXCollections.observableList(this.venueServies.getVenues()));
        List<Venue> venues = this.venueServies.getVenues();

        this.cbVenues.getItems().clear();
        for (Venue v : venues) {
            this.cbVenues.getItems().add(String.format("%s - %d", v.getName(), v.getCapacity()));
            this.venuesId.put(v.getName(), v.getId());
        }

    }

    public void loadColumnEvents() {
        TableColumn colId = new TableColumn("ID");
        colId.setCellValueFactory(new PropertyValueFactory("id"));
        colId.setPrefWidth(100);

        TableColumn colName = new TableColumn("Tên sự kiện");
        colName.setCellValueFactory(new PropertyValueFactory("name"));

        TableColumn<Event, String> colStartDateTime = new TableColumn("Ngày bắt đầu");
        colStartDateTime.setCellValueFactory(cellData -> {
            return new SimpleStringProperty(Utils.formatedDate(cellData.getValue().getStartDate()));
        });

        TableColumn<Event, String> colEndDateTime = new TableColumn("Ngày kết thúc");
        colEndDateTime.setCellValueFactory(cellData -> {
            return new SimpleStringProperty(Utils.formatedDate(cellData.getValue().getEndDate()));
        });

        TableColumn colVenue = new TableColumn("Địa điểm");
        colVenue.setCellValueFactory(new PropertyValueFactory("venue"));

        TableColumn colCapacity = new TableColumn("Số lượng người tham gia");
        colCapacity.setCellValueFactory(new PropertyValueFactory("maxAttendees"));

        TableColumn<Event, String> colStatus = new TableColumn("Trạng thái");
        colStatus.setCellValueFactory(cellData -> {
            Boolean isActive = cellData.getValue().getIsActive();
            String status = (isActive != null && isActive) ? "Hoạt động" : "Kết thúc";
            return new SimpleStringProperty(status);
        });

        TableColumn<Event, Void> colDelAction = new TableColumn("Action");

        colDelAction.setCellFactory(col -> new TableCell<>() {
            private Button btnDel = new Button("Xóa");
            private Button btnUpdate = new Button("Cập nhật");
            private Button btnDestroy = new Button("Hủy");
            private HBox hbox = new HBox(5, btnUpdate, btnDel, btnDestroy);

            {
                btnDel.setOnAction(evt -> {
                    Event e = (Event) ((TableRow) ((Button) evt.getSource()).getParent().getParent().getParent()).getItem();
                    try {
                        deleteEvent(e.getId());
                    } catch (SQLException ex) {
                        Logger.getLogger(EventTabController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                });

                btnUpdate.setOnAction(evt -> {

                    Timestamp timestamp = new Timestamp(new Date().getTime());
                    Event e = (Event) ((TableRow) ((Button) evt.getSource()).getParent().getParent().getParent()).getItem();
                    if (e.getIsActive()) {
                        if (e.getStartDate().getTime() - timestamp.getTime() < ONE_HOUR) {
                            Utils.getAlert(AlertType.ERROR, "Sự kiện không được chỉnh sau 24h");
                            return;
                        }

                        eventIdSelected = e.getId();
                        loadDataUpdateTicket(e);
                        if (!btnUpdateEvent.isVisible()) {
                            btnUpdateEvent.setVisible(true);
                            btnUpdateEvent.setManaged(true);
                        }

                        if (btnCreateEvent.isVisible()) {
                            btnCreateEvent.setVisible(false);
                            btnCreateEvent.setManaged(false);
                        }
                    } else {
                        Utils.getAlert(AlertType.WARNING, "Sự kiện đã hết hạn !!");
                    }
                });

                btnDestroy.setOnAction(evt -> {

                    Event e = (Event) ((TableRow) ((Button) evt.getSource()).getParent().getParent().getParent()).getItem();
                    if (e.getIsActive()) {
                        destroyEvent(e);
                    } else {
                        Utils.getAlert(AlertType.WARNING, "Sự kiện đã hết hạn !!");
                    }
                });
            }

            @Override

            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : hbox);
            }
        });

        this.tbEvents.getColumns().addAll(colId, colName, colStartDateTime,
                colEndDateTime, colVenue, colCapacity, colStatus, colDelAction);

    }

    public void loadEvent(String kw) throws SQLException {
        this.tbEvents.getItems().clear();
        this.tbEvents.setItems(FXCollections.observableList(this.eventServices.getEvents(0, kw)));
        this.tbEvents.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    public List<Ticket> getTicketsValid() throws SQLException {
        List<Ticket> tickets = new ArrayList<>();

        if (this.isInputValid()) {
            String name = this.eventNameField.getText();

            LocalDate today = LocalDate.now();

            if (!this.isValidTimeFormat(this.startTimeField.getText())) {
                Utils.getAlert(AlertType.ERROR, "Giờ không hợp lệ! Vui lòng nhập đúng định dạng HH:mm.");
                this.startTimeField.clear();
                return null;
            }
            Timestamp startDate = Utils.convertToTimestamp(
                    (LocalDate) this.startDatePicker.getValue(),
                    this.startTimeField.getText());
            if (startDate.toLocalDateTime().toLocalDate().isBefore(today.plusDays(1))) {
                Utils.getAlert(AlertType.ERROR, "Ngày và giờ bắt đầu phải lớn hơn ngày hiện tại");
                this.clearDateTimeFields(this.startDatePicker, this.startTimeField);
                return null;
            }

            if (!this.isValidTimeFormat(this.endTimeField.getText())) {
                Utils.getAlert(AlertType.ERROR, "Giờ không hợp lệ! Vui lòng nhập đúng định dạng HH:mm.");
                this.endTimeField.clear();
                return null;
            }
            Timestamp endDate = Utils.convertToTimestamp(
                    (LocalDate) this.endDatePicker.getValue(),
                    this.endTimeField.getText());
            if (endDate.before(startDate)) {
                Utils.getAlert(AlertType.ERROR, "Ngày và giờ kết thúc phải lớn hơn ngày bắt đầu");
                this.clearDateTimeFields(this.endDatePicker, this.endTimeField);
                return null;
            }

            int venueId = this.venuesId.get(this.cbVenues.getValue().split(" - ")[0].trim());

            Event eventExist = this.eventServices.checkVenueAndDateTime(venueId, startDate,endDate, this.eventIdSelected);
            if (eventExist != null) {
                Utils.getAlert(AlertType.ERROR, String.format("Từ %s đến %s đang diễn ra sự kiên tại %s",
                        Utils.formatedDate(eventExist.getStartDate()),
                        Utils.formatedDate(eventExist.getEndDate()), eventExist.getVenue()));
                this.cbVenues.setValue(null);
                this.clearDateTimeFields(this.startDatePicker, this.startTimeField);
                this.clearDateTimeFields(this.endDatePicker, this.endTimeField);
                return null;
            }

            if (!this.isNumeric(this.eventCapacityField.getText())) {
                Utils.getAlert(AlertType.ERROR, "Số lượng khách phải là số nguyên dương");
                this.eventCapacityField.clear();
                return null;
            }
            int maxAttendess = Integer.parseInt(this.eventCapacityField.getText());
            if (maxAttendess > this.venueServies.getVenueById(venueId).getCapacity()) {
                Utils.getAlert(AlertType.ERROR, "Số lượng khách lớn hơn sức chứa của địa điểm");
                this.eventCapacityField.clear();
                return null;
            }

            Event event = new Event(name, startDate, endDate, maxAttendess);
            event.setVenue(this.venueServies.getVenueById(venueId));

            int totalNumberTickets = 0;
            for (Map.Entry<Tickettype, HBox> entry : this.ticketRows.entrySet()) {
                Tickettype type = entry.getKey();
                HBox row = entry.getValue();

                TextField txtNumberTicket = (TextField) row.getChildren().get(1);
                TextField txtPriceTicket = (TextField) row.getChildren().get(2);

                if (!this.isNumeric(txtNumberTicket.getText()) || !this.isNumeric(txtPriceTicket.getText())) {
                    Utils.getAlert(AlertType.ERROR, "Số lượng và giá vé phải là chữ số");
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
                Utils.getAlert(AlertType.ERROR, "Tổng số lượng vé khong được lớp hơn số khách mời");
                this.clearTicketType();
                return null;
            }

            return tickets;

        } else {
            Utils.getAlert(AlertType.ERROR, "Vui lòng nhập đầy đủ dữ liệu");
        }
        return null;
    }

    public void createTicketsHanlder(ActionEvent e) throws SQLException {

        List<Ticket> tickets = getTicketsValid();
        if (tickets != null) {
            boolean result = this.ticketServices.addTickets(tickets);

            Utils.getAlert(
                    result ? AlertType.INFORMATION : AlertType.WARNING,
                    result ? "Thêm sự kiện thành công!" : "Thêm sự kiện thất bại!"
            );

            if (result) {
                this.loadEvent("");
                clearFields();
            }
        }
    }

    public void loadTicketType() throws SQLException {
        VBox container = new VBox(10);
        List<Tickettype> ticketTypes = this.ticketServices.getTicketTypes();

        for (Tickettype type : ticketTypes) {
            HBox row = new HBox(10);

            Label nameLabel = new Label(String.format("Loại vé : %s", type.getName()));
            nameLabel.setPrefWidth(100);

            TextField quantityField = new TextField();
            quantityField.setPromptText("Nhập số lượng");

            TextField priceField = new TextField();
            priceField.setPromptText("Nhập giá vé");

            this.ticketRows.put(type, row);

            row.getChildren().addAll(nameLabel, quantityField, priceField);
            container.getChildren().add(row);
        }

        this.hTicketType.getChildren().add(container);

    }

    public void deleteEvent(int id) throws SQLException {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
                "Bạn có chắc muốn xóa sự kiện này?",
                ButtonType.YES, ButtonType.NO);
        alert.showAndWait();

        if (alert.getResult() == ButtonType.YES) {
            if (id != 0) {
                int rs = this.eventServices.deleteEventById(id);
                Utils.getAlert(
                        rs > 0 ? AlertType.INFORMATION : AlertType.WARNING,
                        rs > 0 ? "Xóa sự kiện thành công!" : "Xóa sự kiện thất bại!"
                );
                if (rs > 0) {
                    this.loadEvent("");
                }
            }
        }
    }

    public void loadDataUpdateTicket(Event e) {
        // load data
        this.eventNameField.setText(e.getName());

        this.startDatePicker.setValue(e.getStartDate().toLocalDateTime().toLocalDate());
        this.startTimeField.setText(String.format("%02d:%02d", e.getStartDate().toLocalDateTime().getHour(),
                e.getStartDate().toLocalDateTime().getMinute()));

        this.endDatePicker.setValue(e.getEndDate().toLocalDateTime().toLocalDate());
        this.endTimeField.setText(String.format("%02d:%02d", e.getEndDate().toLocalDateTime().getHour(),
                e.getEndDate().toLocalDateTime().getMinute()));

        this.cbVenues.setValue(String.format("%s - %d", e.getVenue().getName(), e.getVenue().getCapacity()));
        this.eventCapacityField.setText(String.valueOf(e.getMaxAttendees()));

        try {
            List<Ticket> tickets = this.ticketServices.getTicketByEventId(e.getId());
            int index = 0;
            for (HBox row : this.ticketRows.values()) {
                TextField quantityField = (TextField) row.getChildren().get(1);
                TextField priceField = (TextField) row.getChildren().get(2);

                quantityField.setText(String.format("%d", tickets.get(index).getQuantity()));
                priceField.setText(String.format("%d", tickets.get(index).getPrice().intValue()));
                index++;
            }
        } catch (SQLException ex) {
            Logger.getLogger(EventTabController.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void updateTicketsHandler(ActionEvent e) throws SQLException {
        List<Ticket> tickets = getTicketsValid();
        if (tickets != null) {
            tickets.get(0).getEventId().setId(this.eventIdSelected);
            tickets.get(0).getEventId().setIsActive(Boolean.TRUE);
            List<Integer> ticketIds = this.ticketServices.getTicketById(tickets.get(0).getEventId().getId());
            
            for(int i = 0; i < tickets.size() ; i++)
                tickets.get(i).setId(ticketIds.get(i));
            
            boolean rs = this.ticketServices.updateTickets(tickets);

            Utils.getAlert(
                    rs ? AlertType.INFORMATION : AlertType.WARNING,
                    rs ? "Cập nhật sự kiện thành công!" : "Cập nhật sự kiện thất bại!"
            );

            if (rs) {
                this.loadEvent("");
                this.notificationServices.sendNotificationForUser(
                        Utils.getContentNotification("Cập nhật", tickets.get(0).getEventId()), "UPDATE",
                        tickets.get(0).getEventId().getId());
                this.toggleButton(this.btnCreateEvent);
                this.toggleButton(this.btnUpdateEvent);
                clearFields();
            }
        }
    }

    public boolean isInputValid() {
        boolean isValid = !this.eventNameField.getText().isEmpty()
                && this.startDatePicker.getValue() != null && !this.startTimeField.getText().isEmpty()
                && this.endDatePicker.getValue() != null && !this.endTimeField.getText().isEmpty()
                && !this.eventCapacityField.getText().isEmpty()
                && this.cbVenues.getValue() != null;
        if (isValid) {
            for (HBox row : this.ticketRows.values()) {
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

    public void resetEventData() throws SQLException {
        Timestamp timestamp = new Timestamp(new Date().getTime());
        for (Event e : this.eventServices.getEvents(0, "")) {
            if (e.getEndDate().before(timestamp)) {
                e.setIsActive(Boolean.FALSE);
                this.eventServices.updateEventById(e);
            }
        }
    }

    public void destroyEvent(Event e) {
        Timestamp timestamp = new Timestamp(new Date().getTime());
        if (e.getStartDate().getTime() - timestamp.getTime() < ONE_HOUR) {
            Utils.getAlert(AlertType.ERROR, "Sự kiện không được hủy sau 24h");
            return;
        }
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
                "Bạn có chắc muốn hủy sự kiện này?",
                ButtonType.YES, ButtonType.NO);
        alert.showAndWait();

        if (alert.getResult() == ButtonType.YES) {
            try {
                // cập nhật trạng thái
                e.setIsActive(Boolean.FALSE);
                eventServices.updateEventById(e);

                // gửi thông báo
                notificationServices.sendNotificationForUser(
                        Utils.getContentNotification("Hủy", e), "DELETE", e.getId());
                loadEvent("");

                //hoàn tiền 
                this.eventServices.refundeMoneyToUsers(this.eventServices.getRegisterByEventId(e.getId()));

                Utils.getAlert(AlertType.INFORMATION, "Đã hủy sự kiện thành công!!");
            } catch (SQLException ex) {
                Logger.getLogger(EventTabController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void clearFields() {
        this.eventNameField.clear();
        this.clearDateTimeFields(this.startDatePicker, this.startTimeField);
        this.clearDateTimeFields(this.endDatePicker, this.endTimeField);
        this.cbVenues.setValue(null);
        this.eventCapacityField.clear();
        this.clearTicketType();
    }

    public void clearTicketType() {
        for (HBox row : this.ticketRows.values()) {
            TextField quantityField = (TextField) row.getChildren().get(1);
            TextField priceField = (TextField) row.getChildren().get(2);

            quantityField.clear();
            priceField.clear();

        }
    }
}
