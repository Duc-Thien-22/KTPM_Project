/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ntn.testcase;

import com.ntn.eventmanagement.ValidationUtils;
import com.ntn.pojo.User;
import org.junit.jupiter.api.Assertions;
import com.ntn.services.LoginServices;
import com.ntn.services.UserServices;
import java.sql.SQLException;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.*;
import org.mockito.junit.jupiter.*;
import org.mockito.*;

/**
 *
 * @author NHAT
 */
@ExtendWith(MockitoExtension.class)
public class LoginServicesTester {

    @Mock
    private UserServices userServices;

    @InjectMocks
    private LoginServices loginServices;

    @Test
    @DisplayName("Kiểm tra username rỗng và password rỗng")
    public void testUserNameNullAndPasswordNull() throws Exception {
        String result = loginServices.login("", "");
        Assertions.assertEquals("Không được để trống dữ liệu !!", result);
    }

    @Test
    @DisplayName("Kiểm tra username rỗng và password hợp lệ")
    public void testUserNameNullAndPassWordValid() throws Exception {
        String result = loginServices.login("", "Admin123@");

        Assertions.assertEquals("Không được để trống dữ liệu !!", result);
    }

    @Test
    @DisplayName("Kiểm tra Username hợp lệ passoword  trống")
    public void testUsernameValidPasswordNull() throws Exception {
        String result = loginServices.login("abc", "");

        Assertions.assertEquals("Không được để trống dữ liệu !!", result);
    }

    @Test
    @DisplayName("Kiểm tra username không tồn tại trong csdl và password hợp lệ")
    public void testUserNameNotInDataBase() throws Exception {

        //dùng mock để không cần truy cập vào database thực tế
        Mockito.when(userServices.getUserByUsername("abc")).thenReturn(null);

        String result = loginServices.login("abc", "Admin123@");

        Assertions.assertEquals("Bạn chưa có tài khoản !!", result);
    }

    @Test
    @DisplayName("Kiểm tra username bị vô hiệu hóa")
    public void testUserDisable() throws SQLException, Exception {
        User userTest = new User();
        userTest.setUsername("test");
        userTest.setPassword(ValidationUtils.hashPassword("Admin123@"));
        userTest.setIsActive(Boolean.FALSE);

        Mockito.when(userServices.getUserByUsername("test")).thenReturn(userTest);

        String result = loginServices.login("test", "Admin123@");
        Assertions.assertEquals("tài khoản đã bị khóa", result);
    }

    @Test
    @DisplayName("Kiểm tra username hợp lệ, Password không đủ độ dài")
    public void testUserNameValidPasswordSizeInvalid() throws Exception {
        String result = loginServices.login("test", "admin2");
        Assertions.assertEquals(
                "Password ít nhất là 6 kí tự, bắt đầu bằng 1 kí tự In hoa, thường, chữ số và kí tự đặc biệt",
                result);
    }

    @Test
    @DisplayName("Kiểm tra username hợp lệ, Password đủ độ dài và bắt dầu bằng chữa thường")
    public void testUserNameValidPasswordStartNomalInvalid() throws Exception {
        String result = loginServices.login("test", "admin123@");
        Assertions.assertEquals(
                "Password ít nhất là 6 kí tự, bắt đầu bằng 1 kí tự In hoa, thường, chữ số và kí tự đặc biệt",
                result);
    }

    @Test
    @DisplayName("Kiểm tra username hợp lệ, Password đủ độ dài và bắt dầu bằng chữa hoa, không có kí tự đăng biệt")
    public void testUserNameValidPasswordInvalid() throws Exception {
        String result = loginServices.login("test", "Admin123");
        Assertions.assertEquals(
                "Password ít nhất là 6 kí tự, bắt đầu bằng 1 kí tự In hoa, thường, chữ số và kí tự đặc biệt",
                result);
    }

    @Test
    @DisplayName("Kiểm tra username hợp lệ, Password đủ độ dài và bắt dầu bằng chữa hoa, có kí tự đặc biệt nhưng dài hơn 32 kít tự")
    public void testUserNameValidPasswordMaxSize() throws Exception {
        String result = loginServices.login("test", "Admin@12312578t6872468127681763183681736");
        Assertions.assertEquals(
                "Password ít nhất là 6 kí tự, bắt đầu bằng 1 kí tự In hoa, thường, chữ số và kí tự đặc biệt",
                result);
    }

    @Test
    @DisplayName("Kiểm tra username hợp lệ, Password hợp lệ")
    public void testUserNameValidPasswordValid() throws Exception {
        User userTest = new User();
        userTest.setUsername("Admin");
        userTest.setPassword(ValidationUtils.hashPassword("Admin123@"));
        userTest.setIsActive(Boolean.TRUE);
        userTest.setRole("ADMIN");

        Mockito.when(userServices.getUserByUsername("Admin")).thenReturn(userTest);

        String result = loginServices.login("Admin", "Admin123@");
        Assertions.assertEquals("SUCCESS:ADMIN", result);
    }
}
