/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.ntn.controllers;

import com.ntn.pojo.Event;
import com.ntn.services.ListEventService;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

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
                    createInfoRow("Mã sự kiện", "#" + String.valueOf(e.getId())),
                    createInfoRow("Ngày bắt đầu:", Utils.formatedDate(e.getStartDate())),
                    createInfoRow("Ngày kết thúc:", Utils.formatedDate(e.getEndDate())),
                    createInfoRow("Địa điểm:", e.getVenue().getName()),
                    createInfoRow("Số lượng người đăng ký: ",
                            String.valueOf(e.getRegisterUser()) + "/" + String.valueOf(e.getMaxAttendees()))
            );

            infoBox.getChildren().addAll(eventImage, detailsBox);

//            Label descriptionLabel = new Label("Mô tả: " + e.getDescription());
//            descriptionLabel.setWrapText(true);
            Button registerButton = new Button("Đăng ký tham gia");
            registerButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
            if (e.getMaxAttendees() - e.getRegisterUser() == 0) {
                registerButton.setDisable(true);
            }
            int e_id = e.getId();
            registerButton.setOnAction(event -> registerEvent(e_id));
            int eventId = e.getId();
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

    private void registerEvent(int eventId) {
        //kiem tra nguoi dung da dang ky su kien co cung thoi gian khong

        // Tạo cửa sổ mới
        Stage popupStage = new Stage();
        popupStage.initModality(Modality.APPLICATION_MODAL); // Chặn tương tác với cửa sổ chính
        popupStage.setTitle("Đăng ký vé");

        // Lấy thông tin loại vé từ database
        ListEventService s = new ListEventService();
        Map<String, Object[]> ticketData = s.getTicketInfo(eventId);

        ToggleGroup ticketGroup = new ToggleGroup();
        VBox radioBox = new VBox(10);
        Label priceLabel = new Label("Giá tiền: Chưa chọn");

        boolean isFirst = true;
        for (Map.Entry<String, Object[]> entry : ticketData.entrySet()) {
            String type = entry.getKey();
            int quantity = (int) entry.getValue()[0];
            BigDecimal price = (BigDecimal) entry.getValue()[1];

            RadioButton ticketRadio = new RadioButton(type + " (" + quantity + " vé còn lại)");
            ticketRadio.setUserData(price.toString());
            ticketRadio.setToggleGroup(ticketGroup);

            if (quantity == 0) {
                ticketRadio.setDisable(true);
            } else if (isFirst) {
                ticketRadio.setSelected(true);
                priceLabel.setText("Gía tiền: " + price.toPlainString());
                isFirst = false;
            }

            radioBox.getChildren().add(ticketRadio);
        }

        ticketGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                BigDecimal selectedPrice = new BigDecimal(newValue.getUserData().toString());
                priceLabel.setText("Gía tiền: " + selectedPrice.toPlainString());
            }
        });

        // Nút thanh toán
        Button payButton = new Button("Thanh toán");
        payButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
//        payButton.setOnAction(event -> {
//            Toggle selectedToggle = ticketGroup.getSelectedToggle();
//            RadioButton selectedButton = (RadioButton) selectedToggle;
//            String ticketType = selectedButton.getText().split("\\s*\\(")[0];
//            String ticketPrice = selectedButton.getUserData().toString();
//
//            boolean update = s.updateTicketQuantity(eventId, ticketType);
//            if (update) {
//                Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
//                successAlert.setTitle("Thanh toán");
//                successAlert.setHeaderText("Thanh toán thành công"); // Xóa tiêu đề mặc định
//                successAlert.setContentText("Loại vé: " + ticketType + "\n" + "Giá tiền: " + ticketPrice);
//                successAlert.showAndWait();
//                popupStage.close();
//                try {
//                    getEvent(); // Cập nhật lại danh sách sự kiện để hiển thị số lượng vé mới
//                } catch (SQLException ex) {
//                    Logger.getLogger(RegisterUserController.class.getName()).log(Level.SEVERE, null, ex);
//                }
//            } else {
//                Alert failAlert = new Alert(Alert.AlertType.ERROR);
//                failAlert.setTitle("Thanh toán");
//                failAlert.setHeaderText("Thanh toán không thành công, có thể đã hết vé!"); // Xóa tiêu đề mặc định
//                failAlert.showAndWait();
//                popupStage.close();
//            }
//        });

        payButton.setOnAction(event -> {
            Toggle selectedToggle = ticketGroup.getSelectedToggle();

            RadioButton selectedButton = (RadioButton) selectedToggle;
            String ticketType = selectedButton.getText().split("\\s*\\(")[0];

            int userId = 6; // Giả sử lấy userId từ session hoặc thông tin đăng nhập

            boolean paymentSuccess = s.processPayment(eventId, ticketType, userId);

            if (paymentSuccess) {
                Alert successAlert = new Alert(Alert.AlertType.INFORMATION, "Thanh toán thành công!", ButtonType.OK);
                successAlert.showAndWait();
                popupStage.close();
                try {
                    getEvent(); // Cập nhật lại danh sách sự kiện
                } catch (SQLException ex) {
                    Logger.getLogger(RegisterUserController.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else {
                Alert failAlert = new Alert(Alert.AlertType.ERROR, "Thanh toán không thành công. Hết vé hoặc lỗi hệ thống!", ButtonType.OK);
                failAlert.showAndWait();
            }
        });

        Button cancelButton = new Button("Hủy");
        cancelButton.setOnAction(event -> popupStage.close());

        // Bố cục nút bấm
        HBox buttonBox = new HBox(20, cancelButton, payButton);
        buttonBox.setAlignment(Pos.CENTER);

        // Layout
        VBox layout = new VBox(10, radioBox, priceLabel, buttonBox);
        layout.setPadding(new Insets(15));
        layout.setAlignment(Pos.CENTER);

        // Hiển thị cửa sổ
        Scene scene = new Scene(layout, 300, 200);
        popupStage.setScene(scene);
        popupStage.showAndWait();
    }

}
