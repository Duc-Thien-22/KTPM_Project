/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.ntn.controllers;

import com.ntn.pojo.Event;
import com.ntn.services.ListEventService;
import java.net.URL;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;



/**
 * FXML Controller class
 *
 * @author NHAT
 */
public class RegisterUserController implements Initializable {

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            getEvent();
        } catch (SQLException ex) {
            Logger.getLogger(RegisterUserController.class.getName()).log(Level.SEVERE, null, ex);
        }

        
    }

    @FXML
    private Label lbNameSubject;
    @FXML
    private Label lbStartDate;
    @FXML
    private Label lbEndDate;
    @FXML
    private Label lbAttendees;
    @FXML
    private Label lbVenue;
    @FXML
    private VBox eventListContainer;
    
    public void getEvent() throws SQLException {
        ListEventService s = new ListEventService();
        List<Event> events = s.getEvent();

         if (eventListContainer != null) {
        this.eventListContainer.getChildren().clear(); // Xóa dữ liệu cũ
    }
         if (events.isEmpty()) {
        Label emptyLabel = new Label("Không có sự kiện nào.");
        this.eventListContainer.getChildren().add(emptyLabel);
        return;
    }

        for (Event e : events) {
            VBox eventBox = new VBox(10);
            eventBox.setStyle("-fx-background-color: white; -fx-border-color: #ddd; -fx-border-radius: 5px; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 5);");
            eventBox.setPadding(new Insets(15));

            Label nameLabel = new Label(e.getName());
            nameLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

            HBox infoBox = new HBox(15);

            // Ảnh sự kiện (nếu có URL ảnh)
            ImageView eventImage = new ImageView();
            eventImage.setFitWidth(200);
            eventImage.setPreserveRatio(true);
            // eventImage.setImage(new Image(e.getImageUrl())); // Nếu có URL ảnh từ DB, dùng dòng này

            VBox detailsBox = new VBox(5);
            detailsBox.getChildren().addAll(
                createInfoRow("Ngày bắt đầu:", Utils.formatedDate(e.getStartDate())),
                createInfoRow("Ngày kết thúc:",  Utils.formatedDate(e.getEndDate())),
                createInfoRow("Địa điểm:", e.getVenue().getName()),
                createInfoRow("Số lượng người đăng ký: ",
                        String.valueOf(e.getRegisteredUsers())+"/"+String.valueOf(e.getMaxAttendees()))
            );

            infoBox.getChildren().addAll(eventImage, detailsBox);

//            Label descriptionLabel = new Label("Mô tả: " + e.getDescription());
//            descriptionLabel.setWrapText(true);

            Button registerButton = new Button("Đăng ký tham gia");
            registerButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
            
            eventBox.getChildren().addAll(nameLabel, infoBox, registerButton);
            eventListContainer.getChildren().add(eventBox);
        
    }
    }
    private HBox createInfoRow(String title, String value) {
        HBox row = new HBox(10);
        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-weight: bold;");
        Label valueLabel = new Label(value);
        row.getChildren().addAll(titleLabel, valueLabel);
        return row;
    }
    
    public void registerEvent(ActionEvent e){
        
    }


}
