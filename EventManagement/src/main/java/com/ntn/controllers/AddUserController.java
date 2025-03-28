/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.ntn.controllers;

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

    private final UserServices userServices = new UserServices();

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }

    public void addUserHandler(ActionEvent e) throws SQLException, IOException {
        // check invalid
        if (!this.isInputValid()) {
            Utils.getAlert(Alert.AlertType.ERROR, "Phải nhập đầu đủ dữ liệu");
            return;
        }

        if (!ValidationUtils.isValidationPassword(this.txtPassword.getText())) {
            Utils.getAlert(Alert.AlertType.ERROR, "Password ít nhất là 8 kí tự, bắt đầu bằng 1 kí tự In hoa, thường, chữ số và kí tự đặc biệt");
            this.txtPassword.clear();
            return;
        }
        
        if(!ValidationUtils.isValidationEmail(this.txtEmail.getText())){
            Utils.getAlert(Alert.AlertType.ERROR, "Sai định dạng email.. EX: example@gmail.com");
            this.txtEmail.clear();
            return;
        }
        
        if(!ValidationUtils.isValidationPhone(this.txtNumberPhone.getText())){
            Utils.getAlert(Alert.AlertType.ERROR, "Sai định dạng sdt ... EX: +8401010, 098.... gồm 9-15 kí tự");
            this.txtNumberPhone.clear();
            return;
        }

        // handler add user
        User userExits = this.userServices.getUserByUsername(this.txtUsername.getText(), this.txtEmail.getText());

        if (userExits != null) {
            Utils.getAlert(Alert.AlertType.ERROR, "Username hoặc email đã tồn tại");
            ViewManager.routeView("login");
            return;
        }
        User u = new User(this.txtUsername.getText(), ValidationUtils.hashPassword(this.txtPassword.getText()),
                this.txtFirstName.getText(), this.txtLastName.getText(),
                this.txtEmail.getText(), this.txtNumberPhone.getText(), "ROLE_USER");

        int rs = this.userServices.addUser(u);
        if (rs > 0) {
            Utils.getAlert(Alert.AlertType.INFORMATION, "Đăng kí thành công");
            ViewManager.routeView("login");
        } else {
            Utils.getAlert(Alert.AlertType.WARNING, "Đăng kí thất bại");
        }
    }

    public void loginHandler(ActionEvent e) throws IOException {
        ViewManager.routeView("login");
    }

    public boolean isInputValid() {
        return !this.txtUsername.getText().isEmpty() && !this.txtPassword.getText().isEmpty()
                && !this.txtFirstName.getText().isEmpty() && !this.txtLastName.getText().isEmpty()
                && !this.txtEmail.getText().isEmpty() && !this.txtNumberPhone.getText().isEmpty();
    }
}
