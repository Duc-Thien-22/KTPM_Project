/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.ntn.controllers;

import com.ntn.pojo.DTO.NotificationDTO;
import com.ntn.pojo.Event;
import com.ntn.services.EventServices;
import com.ntn.services.NotificationServices;
import java.net.URL;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
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
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.PropertyValueFactory;

public class NotificationTabController implements Initializable {

    @FXML
    private TableView<NotificationDTO> tbHistoryNotifications;
    @FXML
    private ComboBox<Event> cbEvents;
    @FXML
    private TextArea txtContentNotification;

    private final EventServices eventServices = new EventServices();
    private final NotificationServices notificationServices = new NotificationServices();
    private final int ONE_HOUR = 24 * 60 * 60 * 1000;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            // TODO
            this.loadEvents();
            this.autoRemiderNotification();
            this.loadColumnHistoryNotification();
            this.loadDataHistoryNotification();
        } catch (SQLException ex) {
            Logger.getLogger(NotificationTabController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void autoRemiderNotification() throws SQLException {
        List<Event> events = this.eventServices.getEvents();
        Timestamp timestamp = new Timestamp(new Date().getTime());
        for (Event event : events) {
             List<Integer> registerIds = this.eventServices.getRegisterByEventId(event.getId());
            if (event.getIsActive() 
                    && event.getStartDate().getTime() - timestamp.getTime() <= ONE_HOUR 
                    && !this.notificationServices.isUsersRemider(registerIds)) {
                this.notificationServices.sendNotificationForUser(Utils.getContentNotification("Sắp diễn ra", event), "REMINDER", event.getId());
            }
        }
    }

    public void loadColumnHistoryNotification() {
        TableColumn colId = new TableColumn("ID");
        colId.setCellValueFactory(new PropertyValueFactory("id"));
        colId.setPrefWidth(50);

        TableColumn colContent = new TableColumn("Nội dung thông báo");
        colContent.setCellValueFactory(new PropertyValueFactory("content"));
        colContent.setMinWidth(500);

        TableColumn colUsername = new TableColumn("Tên khách");
        colUsername.setCellValueFactory(new PropertyValueFactory("username"));
        colUsername.setMinWidth(100);

        TableColumn colEventName = new TableColumn("Tên sự kiện");
        colEventName.setCellValueFactory(new PropertyValueFactory("eventName"));
        colEventName.setMinWidth(150);

        TableColumn<NotificationDTO, String> colCreateDateTime = new TableColumn("Ngày gửi");
        colCreateDateTime.setCellValueFactory(cellData -> {
            return new SimpleStringProperty(Utils.formatedDate(cellData.getValue().getCreatedDate()));
        });
        colCreateDateTime.setMinWidth(150);
        this.tbHistoryNotifications.getColumns().addAll(colId, colContent, colEventName, colUsername, colCreateDateTime);
    }

    public void loadDataHistoryNotification() throws SQLException {
        this.tbHistoryNotifications.getItems().clear();
        this.tbHistoryNotifications.setItems(FXCollections.observableList(this.notificationServices.getNotifications()));
        this.tbHistoryNotifications.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    public void loadEvents() throws SQLException {
        this.cbEvents.getItems().clear();
        this.cbEvents.setItems(FXCollections.observableList(this.eventServices.getEvents()));
    }

    public void refeshHandler(ActionEvent e) throws SQLException {
        this.loadEvents();
        this.loadColumnHistoryNotification();
        this.loadDataHistoryNotification();
    }
    
    public void sendNotificationHandler(ActionEvent e){
        if(this.cbEvents.getValue() == null || this.txtContentNotification.getText().isEmpty()){
            Utils.getAlert(Alert.AlertType.WARNING, "Vui lòng nhập dữ liệu !!");
            return;
        }
        int event_id = this.cbEvents.getValue().getId();
        String content = this.txtContentNotification.getText();
        try {
            this.notificationServices.sendNotificationForUser(content, "UPDATE", event_id);
            Utils.getAlert(Alert.AlertType.INFORMATION, "Gửi thông báo thành công");
            this.loadDataHistoryNotification();
            this.clearFields();
        } catch (SQLException ex) {
            Logger.getLogger(NotificationTabController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void clearFields(){
        this.txtContentNotification.clear();
        this.cbEvents.setValue(null);
    }
}
