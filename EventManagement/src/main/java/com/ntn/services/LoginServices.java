/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ntn.services;

import com.ntn.eventmanagement.SessionManager;
import com.ntn.eventmanagement.ValidationUtils;
import com.ntn.pojo.User;

/**
 *
 * @author NHAT
 */
public class LoginServices {

    private final UserServices userServices;

    public LoginServices() {
        this.userServices = new UserServices();
    }


    public LoginServices(UserServices userServices) {
        this.userServices = userServices;
    }

    public String login(String username, String password) throws Exception {
        if (username == null || username.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            return "Không được để trống dữ liệu !!";
        }

        if (!ValidationUtils.isValidationPassword(password)) {
            return "Password ít nhất là 6 kí tự, bắt đầu bằng 1 kí tự In hoa, thường, chữ số và kí tự đặc biệt";
        }

        User user = userServices.getUserByUsername(username);
        if (user == null) {
            return "Bạn chưa có tài khoản !!";
        }

        if (!ValidationUtils.isCheckPassword(password, user.getPassword())) {
            return "Nhập sai mật khẩu !!";
        }

        SessionManager.login(user);

        return "SUCCESS:" + user.getRole();
    }
}
