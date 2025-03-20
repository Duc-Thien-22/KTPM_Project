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
    
    public User checkUserLogin(String username, String password) throws SQLException{
        try(Connection conn = JdbcUtils.getConnection()){
            String sql = "SELECT*FROM user WHERE username = ? AND password = ?";
            PreparedStatement stm = conn.prepareCall(sql);
            stm.setString(1, username);
            stm.setString(2, password);
            
            ResultSet rs = stm.executeQuery();
            
            if(rs.next()){
                User u = new User(rs.getInt("id"), rs.getString("username"),
                        rs.getString("password"), rs.getString("first_name"), 
                        rs.getString("last_name"), rs.getString("email"), rs.getString("role"));
                return u;
            }
            
        }
        return null;
    }
}
