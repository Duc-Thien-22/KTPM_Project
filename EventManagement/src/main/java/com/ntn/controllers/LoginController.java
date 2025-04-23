/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.ntn.controllers;

import com.ntn.eventmanagement.ViewManager;
import com.ntn.services.LoginServices;
import com.ntn.services.UserServices;
import java.io.IOException;
import java.net.URL;
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

    private final LoginServices loginService = new LoginServices();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }

    public void loginHandler(ActionEvent e) throws Exception {
        String username = txtUsername.getText();
        String password = txtPassword.getText();

        String result = loginService.login(username, password);

        if (result.startsWith("SUCCESS")) {
            String role = result.split(":")[1];
            if ("ROLE_ADMIN".equals(role)) {
                ViewManager.routeView("manageEvent");
            } else {
                ViewManager.routeView("registerUser");
            }
        } else {
            Utils.getAlert(Alert.AlertType.ERROR, result);
            this.clearField();
        }
    }

    public void addUserHandler(ActionEvent e) throws IOException {
        ViewManager.routeView("addUser");
    }

    public void clearField() {
        this.txtUsername.clear();
        this.txtPassword.clear();
    }
}
