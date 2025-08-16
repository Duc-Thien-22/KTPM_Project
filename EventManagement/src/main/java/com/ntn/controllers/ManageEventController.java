/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.ntn.controllers;

import com.ntn.eventmanagement.App;
import com.ntn.eventmanagement.SessionManager;
import com.ntn.eventmanagement.ViewManager;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.text.Text;

/**
 * FXML Controller class
 *
 * @author NHAT
 */
public class ManageEventController implements Initializable {
    @FXML
    private TabPane tabPane;
    @FXML
    private Tab eventTab;
    @FXML
    private Tab notificationTab;
    
    @FXML
    private Text textCurrentUser;

    private void loadTab(Tab tab, String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(App.class.getResource(fxmlPath + ".fxml"));
            Node content = loader.load();
            tab.setContent(content);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.textCurrentUser.setText(SessionManager.getCurrentUser().getUsername());
        loadTab(eventTab, "eventTab");
        loadTab(notificationTab, "notificationTab");
    }
    
    
    public void logoutHandler(ActionEvent e) throws IOException{
        SessionManager.logout();
        ViewManager.routeView("login");
    }
}
