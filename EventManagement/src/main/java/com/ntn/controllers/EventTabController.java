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
            this.loadEvent();
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
//                btnDel.setOnAction(event -> {
//                    Event selectedEvent = getTableView().getItems().get(getIndex());
//                    tbEvents.getItems().remove(selectedEvent);
//                });
//
//                btnUpdate.setOnAction(event -> {
//                    Event selectedEvent = getTableView().getItems().get(getIndex());
//                    System.out.println("Cập nhật: " + selectedEvent.getName());
//                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : hbox);
            }
        });

        this.tbEvents.getColumns().addAll(colName, colStartDateTime,
                colEndDateTime, colVenue, colCapacity, colStatus, colDelAction);

    }

    public void loadEvent() throws SQLException {
        this.tbEvents.setItems(FXCollections.observableList(this.eventServices.getEvents()));
        this.tbEvents.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    public void createEventHanlder(ActionEvent e) throws SQLException {
        if (this.isInputValid()) {
            String name = this.eventNameField.getText();

            LocalDate today = LocalDate.now();

            if (!this.isValidTimeFormat(this.startTimeField.getText())) {
                Utils.getAlert(AlertType.ERROR, "Giờ không hợp lệ! Vui lòng nhập đúng định dạng HH:mm.");
                this.startTimeField.clear();
                return;
            }
            Timestamp startDate = Utils.convertToTimestamp(
                    (LocalDate) this.startDatePicker.getValue(),
                    this.startTimeField.getText());
            if (startDate.toLocalDateTime().toLocalDate().isBefore(today.plusDays(1))) {
                Utils.getAlert(AlertType.ERROR, "Ngày và giờ bắt đầu phải lớn hơn ngày hiện tại");
                this.clearDateTimeFields(this.startDatePicker, this.startTimeField);
                return;
            }

            if (!this.isValidTimeFormat(this.endTimeField.getText())) {
                Utils.getAlert(AlertType.ERROR, "Giờ không hợp lệ! Vui lòng nhập đúng định dạng HH:mm.");
                this.endTimeField.clear();
                return;
            }
            Timestamp endDate = Utils.convertToTimestamp(
                    (LocalDate) this.endDatePicker.getValue(),
                    this.endTimeField.getText());
            if (endDate.before(startDate)) {
                Utils.getAlert(AlertType.ERROR, "Ngày và giờ kết thúc phải lớn hơn ngày bắt đầu");
                this.clearDateTimeFields(this.endDatePicker, this.endTimeField);
                return;
            }

            int venueId = this.venuesId.get(this.cbVenues.getValue().split(" - ")[0].trim());

            Event eventExist = this.eventServices.checkVenueAndDateTime(venueId, startDate);
            if (eventExist != null) {
                Utils.getAlert(AlertType.ERROR, String.format("Từ %s đến %s đang diễn ra sự kiên tại %s",
                        Utils.formatedDate(eventExist.getStartDate()),
                        Utils.formatedDate(eventExist.getEndDate()), eventExist.getVenue()));
                this.cbVenues.setValue(null);
                this.clearDateTimeFields(this.startDatePicker, this.startTimeField);
                this.clearDateTimeFields(this.endDatePicker, this.endTimeField);
                return;
            }
            if (!this.isNumeric(this.eventCapacityField.getText())) {
                Utils.getAlert(AlertType.ERROR, "Số lượng khách phải là số nguyên dương");
                this.eventCapacityField.clear();
                return;
            }
            int maxAttendess = Integer.parseInt(this.eventCapacityField.getText());
            if (maxAttendess > this.venueServies.getVenueById(venueId).getCapacity()) {
                Utils.getAlert(AlertType.ERROR, "Số lượng khách lớn hơn sức chứa của địa điểm");
                this.eventCapacityField.clear();
                return;
            }
            int rs = this.eventServices.createEvent(name, startDate, endDate, maxAttendess, venueId);

            Utils.getAlert(
                    rs > 0 ? AlertType.INFORMATION : AlertType.WARNING,
                    rs > 0 ? "Thêm sự kiện thành công!" : "Thêm sự kiện thất bại!"
            );

            if (rs > 0) {
                loadEvent();
                clearFields();
            }

        } else {
            Utils.getAlert(AlertType.ERROR, "Vui lòng nhập đầy đủ dữ liệu");
        }
    }

    public void deleteEventHandler(ActionEvent e) throws SQLException {
        Event eventSelected = this.tbEvents.getSelectionModel().getSelectedItem();

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
                "Bạn có chắc muốn xóa sự kiện này?",
                ButtonType.YES, ButtonType.NO);
        alert.showAndWait();

        if (eventSelected != null) {
            if (alert.getResult() == ButtonType.YES) {
                int rs = eventServices.deleteEventById(eventSelected.getId());
                Utils.getAlert(
                        rs > 0 ? AlertType.INFORMATION : AlertType.WARNING,
                        rs > 0 ? "Xóa sự kiện thành công!" : "Xóa sự kiện thất bại!"
                );
                if (rs > 0) {
                    this.loadEvent();
                }
            }
        }
    }

    private boolean isInputValid() {
        return !this.eventNameField.getText().isEmpty()
                && this.startDatePicker.getValue() != null && !this.startTimeField.getText().isEmpty()
                && this.endDatePicker.getValue() != null && !this.endTimeField.getText().isEmpty()
                && !this.eventCapacityField.getText().isEmpty()
                && this.cbVenues.getValue() != null;
    }

    private void clearDateTimeFields(DatePicker datePicker, TextField timeField) {
        datePicker.setValue(null);
        timeField.clear();
    }

    private void clearFields() {
        this.eventNameField.clear();
        this.clearDateTimeFields(this.startDatePicker, this.startTimeField);
        this.clearDateTimeFields(this.endDatePicker, this.endTimeField);
        this.cbVenues.setValue(null);
        this.eventCapacityField.clear();
    }

    private boolean isNumeric(String str) {
        return str.matches("\\d+"); // Chỉ cho phép số nguyên dương
    }

    private boolean isValidTimeFormat(String time) {
        return time != null && time.matches("([01]\\d|2[0-3]):[0-5]\\d");
    }

}
