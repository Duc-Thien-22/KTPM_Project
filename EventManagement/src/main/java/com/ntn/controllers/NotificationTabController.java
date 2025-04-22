/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.ntn.controllers;

import com.ntn.pojo.DTO.NotificationDTO;
import com.ntn.pojo.Event;
import com.ntn.services.NotificationTabServices;
import java.net.URL;
import java.sql.SQLException;
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

    private final NotificationTabServices notificationTabServices = new NotificationTabServices();

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
        this.notificationTabServices.autoRemiderNotification();
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
        this.tbHistoryNotifications.setItems(FXCollections.observableList(
                this.notificationTabServices.getNotificationHistory()));
        this.tbHistoryNotifications.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    public void loadEvents() throws SQLException {
        this.cbEvents.getItems().clear();
        this.cbEvents.setItems(FXCollections.observableList(this.notificationTabServices.getEvents()));
    }

    public void refeshHandler(ActionEvent e) throws SQLException {
        this.loadEvents();
        this.loadColumnHistoryNotification();
        this.loadDataHistoryNotification();
    }

    public void sendNotificationHandler(ActionEvent e) {
        String error = this.notificationTabServices.checkInputValid(this.cbEvents.getValue(),
                this.txtContentNotification.getText());
        if (error != null) {
            Utils.getAlert(Alert.AlertType.ERROR, error);
            return;
        }

        int event_id = this.cbEvents.getValue().getId();
        String content = this.txtContentNotification.getText();
        try {
            boolean rs = this.notificationTabServices.sendNotification(event_id, content);
            if (rs) {
                Utils.getAlert(Alert.AlertType.INFORMATION, "Gửi thông báo thành công");
                this.loadDataHistoryNotification();
                this.clearFields();
            } else {
                Utils.getAlert(Alert.AlertType.ERROR, "Gửi thông báo thất bại");
            }

        } catch (SQLException ex) {
            Logger.getLogger(NotificationTabController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void clearFields() {
        this.txtContentNotification.clear();
        this.cbEvents.setValue(null);
    }
}
