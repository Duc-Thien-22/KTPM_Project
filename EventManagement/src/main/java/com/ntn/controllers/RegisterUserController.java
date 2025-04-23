package com.ntn.controllers;

import com.ntn.eventmanagement.SessionManager;
import com.ntn.eventmanagement.ViewManager;
import com.ntn.pojo.DTO.EventDTO;
import com.ntn.pojo.DTO.NotificationDTO;
import com.ntn.pojo.User;
import com.ntn.services.RegisterUserServices;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;


public class RegisterUserController implements Initializable {

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
    @FXML
    private TableView<EventDTO> registeredEventTable;
    @FXML
    private TableColumn<EventDTO, String> nameColumn;
    @FXML
    private TableColumn<EventDTO, String> ticketTypeColumn;
    @FXML
    private TableColumn<EventDTO, String> startDateColumn;
    @FXML
    private TableColumn<EventDTO, String> endDateColumn;
    @FXML
    private TableColumn<EventDTO, String> venueColumn;
    @FXML
    private ObservableList<String> collectNotice = FXCollections.observableArrayList();

    private final RegisterUserServices registerService = new RegisterUserServices();
    private final User currentUser = SessionManager.getCurrentUser();
    private final int userId;

    public RegisterUserController() {
        this.userId = currentUser.getId();
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            loadEvents(null);
            loadUserInfo();
            loadNotifications();
            loadRegisteredEvents();
            
            searchField.textProperty().addListener((observable, oldValue, newValue) -> {
                try {
                    loadEvents(newValue);
                } catch (SQLException ex) {
                    Logger.getLogger(RegisterUserController.class.getName()).log(Level.SEVERE, null, ex);
                }
            });
        } catch (SQLException ex) {
            Logger.getLogger(RegisterUserController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void loadEvents(String keyword) throws SQLException {
        List<EventDTO> events = registerService.getEvents(keyword);

        if (eventListContainer != null) {
            // Giữ lại ô tìm kiếm, chỉ xóa danh sách sự kiện
            eventListContainer.getChildren().removeIf(node -> !(node instanceof HBox));
        }
        
        if (events.isEmpty()) {
            Label emptyLabel = new Label("Không có sự kiện nào.");
            this.eventListContainer.getChildren().add(emptyLabel);
        } else {
            for (EventDTO event : events) {
                eventListContainer.getChildren().add(createEventBox(event));
            }
        }
    }


    private VBox createEventBox(EventDTO event) {
        VBox eventBox = new VBox(10);
        eventBox.setStyle("-fx-background-color: white; -fx-border-color: #ddd; -fx-border-radius: 5px; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 5);");
        eventBox.setPadding(new Insets(15));
        HBox.setHgrow(eventBox, Priority.ALWAYS);
        
        Label nameLabel = new Label(event.getName());
        nameLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        nameLabel.setWrapText(true); // Cho phép xuống dòng nếu tên quá dài
        HBox.setHgrow(nameLabel, Priority.ALWAYS);
        
        HBox infoBox = new HBox(15);
        HBox.setHgrow(infoBox, Priority.ALWAYS);
        infoBox.setAlignment(Pos.CENTER_LEFT);
        
        ImageView eventImage = new ImageView();
        eventImage.setFitWidth(200);
        eventImage.setPreserveRatio(true);
        
        VBox detailsBox = new VBox(5);
        HBox.setHgrow(detailsBox, Priority.ALWAYS);
        detailsBox.getChildren().addAll(
                createInfoRow("Mã sự kiện", "#" + String.valueOf(event.getId())),
                createInfoRow("Ngày bắt đầu:", Utils.formatedDate(event.getStartDate())),
                createInfoRow("Ngày kết thúc:", Utils.formatedDate(event.getEndDate())),
                createInfoRow("Địa điểm:", event.getVenue().getName()),
                createInfoRow("Số lượng người đăng ký: ",
                        String.valueOf(event.getRegisteredUser()) + "/" + String.valueOf(event.getMaxAttendees()))
        );

        infoBox.getChildren().addAll(eventImage, detailsBox);

        Button registerButton = new Button("Đăng ký tham gia");
        registerButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        if (event.getMaxAttendees() - event.getRegisteredUser() == 0) {
            registerButton.setDisable(true);
        }
        
        registerButton.setOnAction(action -> showRegistrationForm(event.getId()));
        
        eventBox.getChildren().addAll(nameLabel, infoBox, registerButton);
        return eventBox;
    }


    private HBox createInfoRow(String title, String value) {
        HBox row = new HBox(10);
        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-weight: bold;");
        Label valueLabel = new Label(value);
        HBox.setHgrow(valueLabel, Priority.ALWAYS);
        valueLabel.setWrapText(true);
        row.getChildren().addAll(titleLabel, valueLabel);
        return row;
    }


    private void showRegistrationForm(int eventId) {
        try {
            boolean hasTimeConflict = registerService.checkTimeConflict(eventId, userId);
            if (hasTimeConflict) {
                Utils.getAlert(Alert.AlertType.WARNING, "Bạn đã đăng ký một sự kiện khác trong cùng thời gian!");
                return;
            }
            
            displayTicketSelection(eventId);
        } catch (SQLException ex) {
            Utils.getAlert(Alert.AlertType.WARNING, "Đã xảy ra lỗi khi kiểm tra sự kiện.");
            Logger.getLogger(RegisterUserController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void displayTicketSelection(int eventId) {
        Stage popupStage = new Stage();
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.setTitle("Đăng ký vé");

        Map<String, Object[]> ticketData = registerService.getTicketInfo(eventId);

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
            ticketRadio.setDisable(quantity == 0);
            
            if (isFirst && quantity > 0) {
                ticketRadio.setSelected(true);
                priceLabel.setText("Giá tiền: " + price.toPlainString());
                isFirst = false;
            }

            radioBox.getChildren().add(ticketRadio);
        }

        ticketGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                priceLabel.setText("Giá tiền: " + newValue.getUserData().toString());
            }
        });

        
        Button payButton = new Button("Thanh toán");
        payButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        payButton.setOnAction(event -> {
            Toggle selectedToggle = ticketGroup.getSelectedToggle();
//            if (selectedToggle == null) {
//                Utils.getAlert(Alert.AlertType.WARNING, "Vui lòng chọn loại vé!");
//                return;
//            }
            
            RadioButton selectedButton = (RadioButton) selectedToggle;
            String ticketType = selectedButton.getText().split("\\s*\\(")[0];
            boolean paymentSuccess = registerService.processPayment(eventId, ticketType, userId);

            if (paymentSuccess) {
                Utils.getAlert(Alert.AlertType.INFORMATION, "Thanh toán thành công!");
                popupStage.close();
                try {
                    loadEvents(null);
                    loadRegisteredEvents();
                } catch (SQLException ex) {
                    Logger.getLogger(RegisterUserController.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else {
                Utils.getAlert(Alert.AlertType.ERROR, "Thanh toán không thành công. Hết vé hoặc lỗi hệ thống!");
            }
        });

        Button cancelButton = new Button("Hủy");
        cancelButton.setOnAction(event -> popupStage.close());

        HBox buttonBox = new HBox(20, cancelButton, payButton);
        buttonBox.setAlignment(Pos.CENTER);

        VBox layout = new VBox(10, radioBox, priceLabel, buttonBox);
        layout.setPadding(new Insets(15));
        layout.setAlignment(Pos.CENTER);

        Scene scene = new Scene(layout, 300, 200);
        popupStage.setScene(scene);
        popupStage.showAndWait();
    }


    private void loadUserInfo() {
        this.lbUsername.setText(currentUser.getUsername());
        this.lbEmail.setText(currentUser.getEmail());
        this.lbPhone.setText(currentUser.getPhone());
    }


    @FXML
    public void logoutHandler(ActionEvent event) throws IOException {
        SessionManager.logout();
        ViewManager.routeView("login");
    }

    private void loadNotifications() throws SQLException {
        List<NotificationDTO> notifications = registerService.getNotifications(userId);
        collectNotice.clear();
        
        if (notifications.isEmpty()) {
            collectNotice.add("Không có thông báo!");
        } else {
            for (NotificationDTO notice : notifications) {
                collectNotice.add("Sự kiện " + notice.getEventName() + ": " + notice.getContent());
            }
        }
    }

    private void loadRegisteredEvents() throws SQLException {
        List<EventDTO> events = registerService.getRegisteredEvents(userId);
        
        registeredEventTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        nameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));
        ticketTypeColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTicketName()));
        startDateColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getStartDate().toString()));
        endDateColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getEndDate().toString()));
        venueColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getVenue().getName()));

        ObservableList<EventDTO> eventList = FXCollections.observableArrayList(events);
        registeredEventTable.setItems(eventList);

        if (events.isEmpty()) {
            registeredEventTable.setPlaceholder(new Label("Không có sự kiện nào đã đăng ký."));
        }
    }
}

