/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ntn.testcase;

import com.ntn.controllers.RegisterUserController;
import org.junit.jupiter.api.*;
import org.mockito.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.extension.*;
import org.mockito.junit.jupiter.*;
import com.ntn.pojo.Event;
import com.ntn.pojo.User;
import com.ntn.services.RegisterUserServices;
import java.time.LocalDateTime;
/**
 *
 * @author Duc Thien
 */
@ExtendWith(MockitoExtension.class)
public class RegisterUserControllerTest {

    @Mock
    private RegisterUserServices registerUserServices;

    @InjectMocks
    private RegisterUserController registerUserController;

    @Test
    @DisplayName("Kiểm tra tài khoản không thể đăng ký sự kiện khi hết thời gian đăng ký")
    public void testEventRegistrationAfterDeadline() throws Exception {
        Event event = new Event("Event A");
        event.setRegistrationEndDate(LocalDateTime.now().minusDays(1)); // Đặt thời gian kết thúc đăng ký

        boolean registrationSuccess = registerUserController.registerUser(event, new User("user1", "email@example.com"));

        Assertions.assertFalse(registrationSuccess);
        System.out.println("Không thể đăng ký vì sự kiện đã hết thời gian đăng ký.");
    }
    
    @Test
    @DisplayName("Kiểm tra khi sự kiện đã có người đăng ký, không thể đăng ký lại cho cùng người")
    public void testUserAlreadyRegistered() throws Exception {
        Event event = new Event("Event A");
        User user = new User("user1", "email@example.com");
        
        Mockito.when(registerUserServices.registerUser(event, user)).thenReturn(true);

        boolean firstRegistration = registerUserController.registerUser(event, user);
        boolean secondRegistration = registerUserController.registerUser(event, user);

        Assertions.assertTrue(firstRegistration);
        Assertions.assertFalse(secondRegistration);
    }
    
    @Test
    @DisplayName("Kiểm tra sự kiện không thể đăng ký nếu người dùng không điền đầy đủ thông tin")
    public void testUserRegistrationIncompleteInformation() throws Exception {
        User user = new User("user1", "");  // Thiếu email
        Event event = new Event("Event A");

        boolean registrationSuccess = registerUserController.registerUser(event, user);

        Assertions.assertFalse(registrationSuccess);
        System.out.println("Không thể đăng ký vì thiếu thông tin.");
    }
    
    @Test
    @DisplayName("Kiểm tra khi sự kiện hết vé, không thể đăng ký thêm")
    public void testEventSoldOut() throws Exception {
        Event event = new Event("Event A");
        event.setTicketsRemaining(0); // Không còn vé

        boolean result = registerUserController.registerUser(event, new User("user1", "email@example.com"));

        Assertions.assertFalse(result);
        System.out.println("Sự kiện đã hết vé.");
    }
    
    
    @Test
    @DisplayName("Kiểm tra người dùng không thể đăng ký nhiều sự kiện cùng lúc")
    public void testMultipleEventRegistration() throws Exception {
        Event eventA = new Event("Event A");
        Event eventB = new Event("Event B");
        User user = new User("user1", "email@example.com");

        Mockito.when(registerUserServices.registerUser(eventA, user)).thenReturn(true);
        Mockito.when(registerUserServices.registerUser(eventB, user)).thenReturn(false);

        boolean registrationA = registerUserController.registerUser(eventA, user);
        boolean registrationB = registerUserController.registerUser(eventB, user);

        Assertions.assertTrue(registrationA);
        Assertions.assertFalse(registrationB);
    }
    
    @Test
    @DisplayName("Kiểm tra khi người dùng không thể đăng ký sự kiện sau khi hết thời gian đăng ký")
    public void testEventRegistrationAfterEndDate() throws Exception {
        Event event = new Event("Event A");
        event.setRegistrationEndDate(LocalDateTime.now().minusDays(1)); // Đặt thời gian kết thúc đăng ký

        boolean registrationSuccess = registerUserController.registerUser(event, new User("user1", "email@example.com"));

        Assertions.assertFalse(registrationSuccess);
        System.out.println("Không thể đăng ký vì đã hết thời gian đăng ký.");
    }
    
    @Test
    @DisplayName("Kiểm tra đăng ký sự kiện thành công")
    public void testRegisterEventSuccess() throws Exception {
        Event event = new Event("Event A");
        User user = new User("user1", "email@example.com");

        Mockito.when(registerUserServices.registerUser(event, user)).thenReturn(true);

        boolean result = registerUserController.registerUser(event, user);

        Assertions.assertTrue(result);
    }
}


