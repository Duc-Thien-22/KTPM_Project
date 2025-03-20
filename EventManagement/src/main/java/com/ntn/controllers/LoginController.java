/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.ntn.controllers;

import com.ntn.eventmanagement.SessionManager;
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
    private TextField txtPassword;

    private final UserServices userServices = new UserServices();

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }

    public void loginHandler(ActionEvent e) throws SQLException, IOException {
        User currentUser = this.userServices.checkUserLogin(this.txtUsername.getText(), this.txtPassword.getText());
        if (currentUser == null) {
            Utils.getAlert(Alert.AlertType.ERROR, "Đăng nhập thất bại !!");
            return;
        }

        SessionManager.login(currentUser);

        if ("ROLE_ADMIN".equals(currentUser.getRole())) {
            ViewManager.routeView("manageEvent");
        } else {
            Utils.getAlert(Alert.AlertType.INFORMATION, "Trang user");
        }
    }
}
