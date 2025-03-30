/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.ntn.controllers;

import com.ntn.eventmanagement.SessionManager;
import com.ntn.eventmanagement.ViewManager;
import com.ntn.pojo.DTO.EventDTO;
import com.ntn.pojo.User;
import com.ntn.services.EventServices;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.SQLException;
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
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
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
            getEvent(null);
            loadUserRegister();
            searchField.textProperty().addListener((observable, oldValue, newValue) -> {
                try {
                    getEvent(newValue);

                } catch (SQLException ex) {
                    Logger.getLogger(RegisterUserController.class.getName()).log(Level.SEVERE, null, ex);
                }
            });
        } catch (SQLException ex) {
            Logger.getLogger(RegisterUserController.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    @FXML
    private VBox eventListContainer;
    @FXML
    private TextField searchField;
    @FXML
    private Label lbUsername;
    @FXML
    private Label lbEmail;
    @FXML
    private Label lbPhone;

    private final EventServices s = new EventServices();

    public void getEvent(String kw) throws SQLException {
        List<EventDTO> events = s.getEvent(kw);

        if (eventListContainer != null) {
            // Giữ lại ô tìm kiếm, chỉ xóa danh sách sự kiện
            eventListContainer.getChildren().removeIf(node -> !(node instanceof HBox));
        }
        if (events.isEmpty()) {
            Label emptyLabel = new Label("Không có sự kiện nào.");
            this.eventListContainer.getChildren().add(emptyLabel);
            return;
        } else {
            for (EventDTO e : events) {
                eventListContainer.getChildren().add(displayEvent(e));
            }
        }
    }

    private VBox displayEvent(EventDTO e) {
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
        // eventImage.setImage(new Image(e.getImageUrl()));

        VBox detailsBox = new VBox(5);
        detailsBox.getChildren().addAll(
                createInfoRow("Mã sự kiện", "#" + String.valueOf(e.getId())),
                createInfoRow("Ngày bắt đầu:", Utils.formatedDate(e.getStartDate())),
                createInfoRow("Ngày kết thúc:", Utils.formatedDate(e.getEndDate())),
                createInfoRow("Địa điểm:", e.getVenue().getName()),
                createInfoRow("Số lượng người đăng ký: ",
                        String.valueOf(e.getRegisteredUser()) + "/" + String.valueOf(e.getMaxAttendees()))
        );

        infoBox.getChildren().addAll(eventImage, detailsBox);

//            Label descriptionLabel = new Label("Mô tả: " + e.getDescription());
//            descriptionLabel.setWrapText(true);
        Button registerButton = new Button("Đăng ký tham gia");
        registerButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        if (e.getMaxAttendees() - e.getRegisteredUser() == 0) {
            registerButton.setDisable(true);
        }
        int eventId = e.getId();
        registerButton.setOnAction(event -> registerEvent(eventId));
        eventBox.getChildren().addAll(nameLabel, infoBox, registerButton);
        return eventBox;
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
        int userId = SessionManager.getCurrentUser().getId();
        try {
            boolean status_regis = s.checkStatusRegis(eventId, userId);
            if (status_regis) {
                Utils.getAlert(Alert.AlertType.WARNING, "Bạn đã đăng ký một sự kiện khác trong cùng thời gian!");
                return; // Ngăn chặn đăng ký
            }

        } catch (SQLException ex) {
            Utils.getAlert(Alert.AlertType.WARNING, "Đã xảy ra lỗi khi kiểm tra sự kiện.");

        }
        displayTicket(eventId, userId);
    }

    private void displayTicket(int eventId, int userId) {
        // Tạo cửa sổ mới
        Stage popupStage = new Stage();
        popupStage.initModality(Modality.APPLICATION_MODAL); // Chặn tương tác với cửa sổ chính
        popupStage.setTitle("Đăng ký vé");

        // Lấy thông tin loại vé từ database
        Map<String, Object[]> ticketData = s.getTicketInfo(eventId);

        ToggleGroup ticketGroup = new ToggleGroup();
        VBox radioBox = new VBox(10);
        Label priceLabel = new Label("Giá tiền: Chưa chọn");

        boolean isFirst = true;
        for (Map.Entry<String, Object[]> entry : ticketData.entrySet()) {
            //THONG TIN VE
            String type = entry.getKey();
            int quantity = (int) entry.getValue()[0];
            BigDecimal price = (BigDecimal) entry.getValue()[1];

            RadioButton ticketRadio = new RadioButton(type + " (" + quantity + " vé còn lại)");
            ticketRadio.setUserData(price.toString());
            ticketRadio.setToggleGroup(ticketGroup);
            ticketRadio.setDisable(quantity == 0);
            if (isFirst) {
                ticketRadio.setSelected(true);
                priceLabel.setText("Gía tiền: " + price.toPlainString());
                isFirst = false;
            }

            radioBox.getChildren().add(ticketRadio);
        }

        ticketGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                //BigDecimal selectedPrice = new BigDecimal(newValue.getUserData().toString());
                priceLabel.setText("Gía tiền: " + newValue.getUserData().toString());
            }
        });

        // Nút thanh toán
        Button payButton = new Button("Thanh toán");
        payButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        payButton.setOnAction(event -> {
            Toggle selectedToggle = ticketGroup.getSelectedToggle();
            RadioButton selectedButton = (RadioButton) selectedToggle;
            String ticketType = selectedButton.getText().split("\\s*\\(")[0];
            boolean paymentSuccess = s.processPayment(eventId, ticketType, userId);

            if (paymentSuccess) {
                Utils.getAlert(Alert.AlertType.INFORMATION, "Thanh toán thành công!");
                popupStage.close();
                try {
                    getEvent(null); // Cập nhật lại danh sách sự kiện
                } catch (SQLException ex) {
                    Logger.getLogger(RegisterUserController.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else {
                Utils.getAlert(Alert.AlertType.ERROR, "Thanh toán không thành công. Hết vé hoặc lỗi hệ thống!");
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

    public void loadUserRegister() {
        User currentUser = SessionManager.getCurrentUser();
        this.lbUsername.setText(currentUser.getUsername());
        this.lbEmail.setText(currentUser.getEmail());
        this.lbPhone.setText(currentUser.getPhone());
    }

    public void logoutHandler(ActionEvent e) throws IOException {
        SessionManager.logout();
        ViewManager.routeView("login");
    }
}
