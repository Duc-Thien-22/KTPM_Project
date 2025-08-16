/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ntn.services;

import com.ntn.eventmanagement.ValidationUtils;
import com.ntn.pojo.User;

/**
 *
 * @author NHAT
 */
public class AddUserServices {

    private final UserServices userServices;

    public AddUserServices() {
        this.userServices = new UserServices();
    }
    
    public AddUserServices(UserServices userServices) {
        this.userServices = userServices;
    }

    public String addUser(User u) throws Exception {
        if (u == null
                || u.getUsername().isEmpty() || u.getPassword().isEmpty()
                || u.getFirstName().isEmpty() || u.getLastName().isEmpty()
                || u.getEmail().isEmpty() || u.getPhone().isEmpty()) {
            return "Phải nhập đầu đủ dữ liệu";
        }

        if (!ValidationUtils.isValidationPassword(u.getPassword())) {
            return "Password ít nhất là 6 kí tự, bắt đầu bằng 1 kí tự In hoa, thường, chữ số và kí tự đặc biệt";
        }

        if (!ValidationUtils.isValidationEmail(u.getEmail())) {
            return "Sai định dạng email.. EX: example@gmail.com";
        }

        if (!ValidationUtils.isValidationPhone(u.getPhone())) {
            return "Sai định dạng sdt ... EX: +8401010, 098.... gồm 9-15 kí tự";
        }

        if (userServices.getUserByUsername(u.getUsername(), u.getEmail()) != null) {
            return "Username hoặc email đã tồn tại";
        }

        // Hash password
        u.setPassword(ValidationUtils.hashPassword(u.getPassword()));

        int result = userServices.addUser(u);
        if (result > 0) {
            return "SUCCESS";
        } else {
            return "Đăng kí thất bại";
        }
    }
}
