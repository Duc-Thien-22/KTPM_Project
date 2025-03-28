/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.ntn.controllers;

import com.ntn.eventmanagement.SessionManager;
import com.ntn.eventmanagement.ValidationUtils;
import com.ntn.eventmanagement.ViewManager;
import com.ntn.pojo.User;
import com.ntn.services.UserServices;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

/**
 * FXML Controller class
 *
 * @author NHAT
 */
public class LoginController implements Initializable {

    @FXML
    private TextField txtUsername;
    @FXML
    private PasswordField txtPassword;

    private final UserServices userServices = new UserServices();

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }

    public void loginHandler(ActionEvent e) throws SQLException, IOException {

        if (!this.txtUsername.getText().isEmpty() && !this.txtPassword.getText().isEmpty()) {
            if (!ValidationUtils.isValidationPassword(this.txtPassword.getText())) {
                Utils.getAlert(Alert.AlertType.ERROR, "Password ít nhất là 8 kí tự, bắt đầu bằng 1 kí tự In hoa, thường, chữ số và kí tự đặc biệt");
                this.txtPassword.clear();
                return;
            }

            User currentUser = this.userServices.getUserByUsername(this.txtUsername.getText());
            if (currentUser == null) {
                Utils.getAlert(Alert.AlertType.ERROR, "Bạn chưa có tài khoản !!");
                this.clearField();
                return;
            }

            if (!ValidationUtils.isCheckPassword(this.txtPassword.getText(), currentUser.getPassword())) {
                Utils.getAlert(Alert.AlertType.ERROR, "Nhập sai mật khẩu !!");
                this.txtPassword.clear();
                return;
            }

            SessionManager.login(currentUser);

            if ("ROLE_ADMIN".equals(currentUser.getRole())) {
                ViewManager.routeView("manageEvent");
            } else {
                Utils.getAlert(Alert.AlertType.INFORMATION, "Trang user");
            }
        }else
            Utils.getAlert(Alert.AlertType.ERROR, "Không được để trống dữ liệu !!");
    }

    public void addUserHandler(ActionEvent e) throws IOException {
        ViewManager.routeView("addUser");
    }

    public void clearField() {
        this.txtUsername.clear();
        this.txtPassword.clear();
    }
}
