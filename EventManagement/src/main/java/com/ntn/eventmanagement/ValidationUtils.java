/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ntn.eventmanagement;

import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;
import java.util.regex.Pattern;

/**
 *
 * @author NHAT
 */
public class ValidationUtils {

    private static final Argon2 argon = Argon2Factory.create();

    public static String hashPassword(String password) {
        return argon.hash(2, 65536, 1, password.toCharArray());
    }

    public static boolean isCheckPassword(String passwordNew, String passwordOld) {
        return argon.verify(passwordOld, passwordNew.toCharArray());
    }
    
    public static boolean isValidationPassword(String password){
        String pattern  = "^[A-Z][\\w_\\.!@#$%^&*()]{5,31}$";
        return Pattern.matches(pattern , password);
    }
    
    public static boolean isValidationEmail(String email){
        String pattern  = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,12}$";
        return Pattern.matches(pattern , email);
    }
    
    public static boolean isValidationPhone(String phone){
        String pattern  = "^\\+?[0-9]{9,15}$";
        return Pattern.matches(pattern , phone);
    }
}
