/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.ntn.controllers;

import com.ntn.pojo.Event;
import com.ntn.pojo.Venue;
import com.ntn.services.EventServices;
import com.ntn.services.VenueServices;
import java.net.URL;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
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
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;

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

    private int eventIdSelected = 0;
    private final Map<String, Integer> venuesId = new HashMap<>();
    private final VenueServices venueServies = new VenueServices();
    private final EventServices eventServices = new EventServices();

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            this.loadVenue();
            this.loadColumnEvents();
            this.loadEvent("");
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

        TableColumn<Event, Void> colDelAction = new TableColumn<>("Action");

        colDelAction.setCellFactory(col -> new TableCell<>() {
            private final Button btnDel = new Button("Xóa");
            private final Button btnUpdate = new Button("Cập nhật");
            private final HBox hbox = new HBox(5, btnUpdate, btnDel);

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
                    Event e = (Event) ((TableRow) ((Button) evt.getSource()).getParent().getParent().getParent()).getItem();
                    System.out.println(e.getIsActive());
                    if (e.getIsActive()) {
                        eventIdSelected = e.getId();
                        loadDataUpdateEvent(e);
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
        // cap nhat lai su kien het han
        this.resetEventData();

        this.tbEvents.setItems(FXCollections.observableList(this.eventServices.getEvents(0, kw)));
        this.tbEvents.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    public Event getEventValid() throws SQLException {

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

            Event eventExist = this.eventServices.checkVenueAndDateTime(venueId, startDate, this.eventIdSelected);
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
            return event;

        } else {
            Utils.getAlert(AlertType.ERROR, "Vui lòng nhập đầy đủ dữ liệu");
        }
        return null;
    }

    ;
    
    public void createEventHanlder(ActionEvent e) throws SQLException {
        Event event = getEventValid();
        if (event != null) {
            int rs = this.eventServices.createEvent(event);

            Utils.getAlert(
                    rs > 0 ? AlertType.INFORMATION : AlertType.WARNING,
                    rs > 0 ? "Thêm sự kiện thành công!" : "Thêm sự kiện thất bại!"
            );

            if (rs > 0) {
                this.loadEvent("");
                clearFields();
            }
        }
    }

    public void deleteEvent(int id) throws SQLException {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
                "Bạn có chắc muốn xóa sự kiện này?",
                ButtonType.YES, ButtonType.NO);
        alert.showAndWait();

        if (id != 0) {
            if (alert.getResult() == ButtonType.YES) {
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

    public void loadDataUpdateEvent(Event e) {
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
    }

    public void updateEventHandler(ActionEvent e) throws SQLException {
        Event event = getEventValid();
        if (event != null) {
            event.setId(this.eventIdSelected);
            event.setIsActive(Boolean.TRUE);
            int rs = this.eventServices.updateEventById(event);

            Utils.getAlert(
                    rs > 0 ? AlertType.INFORMATION : AlertType.WARNING,
                    rs > 0 ? "Cập nhật sự kiện thành công!" : "Cập nhật sự kiện thất bại!"
            );

            if (rs > 0) {
                this.loadEvent("");
                this.toggleButton(this.btnCreateEvent);
                this.toggleButton(this.btnUpdateEvent);
                clearFields();
            }
        }
    }

    public boolean isInputValid() {
        return !this.eventNameField.getText().isEmpty()
                && this.startDatePicker.getValue() != null && !this.startTimeField.getText().isEmpty()
                && this.endDatePicker.getValue() != null && !this.endTimeField.getText().isEmpty()
                && !this.eventCapacityField.getText().isEmpty()
                && this.cbVenues.getValue() != null;
    }

    public void clearDateTimeFields(DatePicker datePicker, TextField timeField) {
        datePicker.setValue(null);
        timeField.clear();
    }

    public void clearFields() {
        this.eventNameField.clear();
        this.clearDateTimeFields(this.startDatePicker, this.startTimeField);
        this.clearDateTimeFields(this.endDatePicker, this.endTimeField);
        this.cbVenues.setValue(null);
        this.eventCapacityField.clear();
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
//        System.out.println(timestamp);
        for (Event e : this.eventServices.getEvents(0, "")) {
            if (e.getEndDate().before(timestamp)) {
                e.setIsActive(Boolean.FALSE);
                this.eventServices.updateEventById(e);
            }
        }
    }
}
