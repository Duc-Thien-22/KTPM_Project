/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ntn.eventmanagement;

import javafx.scene.Scene;
import javafx.stage.Stage;
import com.ntn.eventmanagement.App;
import java.io.IOException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

/**
 *
 * @author NHAT
 */
public class ViewManager {

    private static Stage privateScene; // cửa sổ ứng dụng đang làm việc

    public static void setPrivateScene(Stage currentStage) {
        privateScene = currentStage;
    }

    public static void routeView(String fxml) throws IOException {
        Scene scene = new Scene(loadFXML(fxml));
        privateScene.setScene(scene);
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }
}
