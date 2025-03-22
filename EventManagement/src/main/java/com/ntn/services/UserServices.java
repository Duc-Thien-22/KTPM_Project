/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ntn.services;

import com.ntn.pojo.JdbcUtils;
import com.ntn.pojo.User;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author NHAT
 */
public class UserServices {

    public User getUserByUsername(String username) throws SQLException {
        try (Connection conn = JdbcUtils.getConnection()) {
            String sql = "SELECT*FROM user WHERE username = ?";

            PreparedStatement stm = conn.prepareCall(sql);
            stm.setString(1, username);

            ResultSet rs = stm.executeQuery();

            if (rs.next()) {
                User u = new User(rs.getInt("id"), rs.getString("username"),
                        rs.getString("password"), rs.getString("first_name"),
                        rs.getString("last_name"), rs.getString("email"), rs.getString("role"));
                return u;
            }
        }
        return null;
    }

    public User getUserByUsername(String username, String email) throws SQLException {
        try (Connection conn = JdbcUtils.getConnection()) {
            String sql = "SELECT*FROM user WHERE username = ? OR email = ?";

            PreparedStatement stm = conn.prepareCall(sql);
            stm.setString(1, username);
             stm.setString(2, email);

            ResultSet rs = stm.executeQuery();

            if (rs.next()) {
                User u = new User(rs.getInt("id"), rs.getString("username"),
                        rs.getString("password"), rs.getString("first_name"),
                        rs.getString("last_name"), rs.getString("email"), rs.getString("role"));
                return u;
            }
        }
        return null;
    }

    public int addUser(User u) throws SQLException {
        try (Connection conn = JdbcUtils.getConnection()) {
            String sql = "INSERT INTO user (username, password,first_name, last_name,email, phone, role) VALUES (?, ?, ?, ?, ?,?,?)";
            PreparedStatement stm = conn.prepareCall(sql);
            stm.setString(1, u.getUsername());
            stm.setString(2, u.getPassword());
            stm.setString(3, u.getFirstName());
            stm.setString(4, u.getLastName());
            stm.setString(5, u.getEmail());
            stm.setString(6, u.getPhone());
            stm.setString(7, u.getRole());

            return stm.executeUpdate();

        }
    }
}
