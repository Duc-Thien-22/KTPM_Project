/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ntn.controllers;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

/**
 *
 * @author NHAT
 */
public class Utils {

    public static Alert getAlert(AlertType alertType, String content) {
        Alert a = new Alert(alertType);
        a.setTitle("Thông báo");
        a.setContentText(content);
        a.showAndWait();
        return a;
    }

    public static Timestamp convertToTimestamp(LocalDate date, String time) {
        try {
            // Định dạng giờ HH:mm (24h)
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
            LocalTime localTime = LocalTime.parse(time, timeFormatter);

            // Kết hợp ngày và giờ
            LocalDateTime dateTime = LocalDateTime.of(date, localTime);

            // Chuyển thành Timestamp để lưu vào DB
            return Timestamp.valueOf(dateTime);
        } catch (Exception e) {
            System.out.println("Lỗi chuyển đổi ngày giờ: " + e.getMessage());
            return null;
        }
    }

    public static String formatedDate(Timestamp date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
        LocalDateTime dateTime = date.toLocalDateTime();
        return dateTime.format(formatter);
    }

}
