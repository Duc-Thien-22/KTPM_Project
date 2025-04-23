/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.ntn.controllers;

import com.ntn.eventmanagement.ValidationUtils;
import com.ntn.eventmanagement.ViewManager;
import com.ntn.pojo.User;
import com.ntn.services.AddUserServices;
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
public class AddUserController implements Initializable {

    @FXML
    private TextField txtUsername;
    @FXML
    private PasswordField txtPassword;
    @FXML
    private TextField txtFirstName;
    @FXML
    private TextField txtLastName;
    @FXML
    private TextField txtEmail;
    @FXML
    private TextField txtNumberPhone;

    private final AddUserServices addUserService = new AddUserServices(new UserServices());

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }

    public void addUserHandler(ActionEvent e) throws SQLException, IOException, Exception {
        User u = new User(
                this.txtUsername.getText(),
                this.txtPassword.getText(),
                this.txtFirstName.getText(),
                this.txtLastName.getText(),
                this.txtEmail.getText(),
                this.txtNumberPhone.getText(),
                "ROLE_USER"
        );

        String result = addUserService.addUser(u);

        if ("SUCCESS".equals(result)) {
            Utils.getAlert(Alert.AlertType.INFORMATION, "Đăng kí thành công");
            ViewManager.routeView("login");
        } else if ("Username hoặc email đã tồn tại".equals(result)) {
            Utils.getAlert(Alert.AlertType.ERROR, result);
            ViewManager.routeView("login");
        } else {
            Utils.getAlert(Alert.AlertType.ERROR, result);
        }
    }

    public void loginHandler(ActionEvent e) throws IOException {
        ViewManager.routeView("login");
    }
}
