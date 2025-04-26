/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.ntn.controllers;

import com.ntn.pojo.Event;
import com.ntn.pojo.Ticket;
import com.ntn.pojo.Tickettype;
import com.ntn.pojo.Venue;
import com.ntn.services.EventTabServices;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
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

    private final EventTabServices eventTabService = new EventTabServices();

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            // cap nhat lai su kien het han
            this.eventTabService.resetEventData();
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
        List<Venue> venues = this.eventTabService.getVenues();

        this.cbVenues.getItems().clear();
        for (Venue v : venues) {
            this.cbVenues.getItems().add(String.format("%s - %d", v.getName(), v.getCapacity()));
            this.eventTabService.getVenuesId().put(v.getName(), v.getId());
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

                    Event e = (Event) ((TableRow) ((Button) evt.getSource()).getParent().getParent().getParent()).getItem();

                    String errorMessage = eventTabService.checkValidAction(e);
                    if (errorMessage != null) {
                        Utils.getAlert(AlertType.ERROR, errorMessage);
                        return;
                    }
                    eventTabService.setEventIdSelected(e.getId());
                    loadDataUpdateTicket(e);

                    if (!btnUpdateEvent.isVisible()) {
                        btnUpdateEvent.setVisible(true);
                        btnUpdateEvent.setManaged(true);
                    }

                    if (btnCreateEvent.isVisible()) {
                        btnCreateEvent.setVisible(false);
                        btnCreateEvent.setManaged(false);
                    }
                });

                btnDestroy.setOnAction(evt -> {

                    Event e = (Event) ((TableRow) ((Button) evt.getSource()).getParent().getParent().getParent()).getItem();

                    String errorMessage = eventTabService.checkValidAction(e);
                    if (errorMessage != null) {
                        Utils.getAlert(AlertType.ERROR, errorMessage);
                        return;
                    }
                    destroyEvent(e);
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
        this.tbEvents.setItems(FXCollections.observableList(this.eventTabService.getEvents(0, kw)));
        this.tbEvents.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    public void createTicketsHanlder(ActionEvent e) throws SQLException {
        String[] errorMsg = new String[1];
        List<Ticket> tickets = this.eventTabService.getTicketsValid(
                this.eventNameField.getText(),
                this.startDatePicker.getValue(),
                this.startTimeField.getText(),
                this.endDatePicker.getValue(),
                this.endTimeField.getText(),
                this.cbVenues.getValue(),
                this.eventCapacityField.getText(),
                errorMsg
        );
        if (tickets == null) {
            Utils.getAlert(AlertType.ERROR, errorMsg[0]);
            return;
        }
        boolean result = this.eventTabService.getTicketServices().addTickets(tickets);

        Utils.getAlert(
                result ? AlertType.INFORMATION : AlertType.WARNING,
                result ? "Thêm sự kiện thành công!" : "Thêm sự kiện thất bại!"
        );

        if (result) {
            this.loadEvent("");
            clearFields();
        }
    }

    public void loadTicketType() throws SQLException {
        VBox container = new VBox(10);
        List<Tickettype> ticketTypes = this.eventTabService.getTicketTypes();

        for (Tickettype type : ticketTypes) {
            HBox row = new HBox(10);

            Label nameLabel = new Label(String.format("Loại vé : %s", type.getName()));
            nameLabel.setPrefWidth(100);

            TextField quantityField = new TextField();
            quantityField.setPromptText("Nhập số lượng");

            TextField priceField = new TextField();
            priceField.setPromptText("Nhập giá vé");

           

            row.getChildren().addAll(nameLabel, quantityField, priceField);
            container.getChildren().add(row);
            
             this.eventTabService.getTicketRows().put(type, row);
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
                boolean rs = this.eventTabService.deleteEvent(id);
                Utils.getAlert(
                        rs ? AlertType.INFORMATION : AlertType.WARNING,
                        rs ? "Xóa sự kiện thành công!" : "Xóa sự kiện thất bại!"
                );
                if (rs) {
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
            List<Ticket> tickets = this.eventTabService.getTicketServices().getTicketByEventId(e.getId());
            int index = 0;
            for (HBox row : this.eventTabService.getTicketRows().values()) {
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
        String[] errorMsg = new String[1];
        List<Ticket> tickets = this.eventTabService.getTicketsValid(
                this.eventNameField.getText(),
                this.startDatePicker.getValue(),
                this.startTimeField.getText(),
                this.endDatePicker.getValue(),
                this.endTimeField.getText(),
                this.cbVenues.getValue().split(" - ")[0].trim(),
                this.eventCapacityField.getText(),
                errorMsg
        );
        if (tickets == null) {
            Utils.getAlert(AlertType.ERROR, errorMsg[0]);
            return;
        }
        tickets.get(0).getEventId().setId(this.eventTabService.getEventIdSelected());
        tickets.get(0).getEventId().setIsActive(Boolean.TRUE);
        List<Integer> ticketIds = this.eventTabService.getTicketServices().getTicketById(
                tickets.get(0).getEventId().getId());

        for (int i = 0; i < tickets.size(); i++) {
            tickets.get(i).setId(ticketIds.get(i));
        }

        boolean rs = this.eventTabService.getTicketServices().updateTickets(tickets);

        Utils.getAlert(
                rs ? AlertType.INFORMATION : AlertType.WARNING,
                rs ? "Cập nhật sự kiện thành công!" : "Cập nhật sự kiện thất bại!"
        );

        if (rs) {
            this.loadEvent("");
            this.eventTabService.sendNotification(tickets.get(0).getEventId().getId(),
                    Utils.getContentNotification("Cập nhật", tickets.get(0).getEventId()), "UPDATE");
            this.toggleButton(this.btnCreateEvent);
            this.toggleButton(this.btnUpdateEvent);
            clearFields();
        }
    }

    public void destroyEvent(Event e) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
                "Bạn có chắc muốn hủy sự kiện này?",
                ButtonType.YES, ButtonType.NO);
        alert.showAndWait();

        if (alert.getResult() == ButtonType.YES) {
            try {
                // cập nhật trạng thái
                boolean rs = this.eventTabService.destroyEvent(e);
                if (rs) {
                    // gửi thông báo
                    this.eventTabService.sendNotification(e.getId(),
                            Utils.getContentNotification("Hủy", e), "DELETE");
                    //hoàn tiền 
                    this.eventTabService.refundeMoney(
                            this.eventTabService.getEventServices().getRegisterByEventId(e.getId()));

                    Utils.getAlert(AlertType.INFORMATION, "Đã hủy sự kiện thành công!!");
                    loadEvent("");
                } else {
                    Utils.getAlert(AlertType.ERROR, "Hủy sự kiện thất bại!!");
                }
            } catch (SQLException ex) {
                Logger.getLogger(EventTabController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void clearDateTimeFields(DatePicker datePicker, TextField timeField) {
        datePicker.setValue(null);
        timeField.clear();
    }

    public void toggleButton(Button b) {
        b.setVisible(!b.isVisible());
        b.setManaged(b.isVisible());
    }

    public void clearFields() {
        this.eventNameField.clear();
        this.clearDateTimeFields(this.startDatePicker, this.startTimeField);
        this.clearDateTimeFields(this.endDatePicker, this.endTimeField);
        this.cbVenues.setValue(null);
        this.eventCapacityField.clear();
        this.eventTabService.clearTicketType();
    }
}
