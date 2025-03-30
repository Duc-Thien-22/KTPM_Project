/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ntn.eventmanagement;

import com.ntn.pojo.User;

/**
 *
 * @author NHAT
 */
public class SessionManager {
    private static User currentUser;
    
    public static boolean isLogin(){
        return currentUser != null;
    }
    
    public static void logout(){
        currentUser = null;
    }
    
    public static User getCurrentUser() {
        return currentUser;
    }

    public static void login(User user) {
        currentUser = user;
    }
    
}
