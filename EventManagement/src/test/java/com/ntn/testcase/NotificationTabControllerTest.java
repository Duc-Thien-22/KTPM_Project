///*
// * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
// * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
// */
//package com.ntn.testcase;
//
//
//import com.ntn.controllers.NotificationTabController;
//import com.ntn.pojo.Notification;
//import com.ntn.services.NotificationTabServices;
//import org.junit.jupiter.api.*;
//import org.mockito.*;
//import org.junit.jupiter.api.Assertions;
//import org.junit.jupiter.api.extension.*;
//
///**
// *
// * @author Duc Thien
// */
//@ExtendWith(MockitoExtension.class)
//public class NotificationTabControllerTest {
//
//    @Mock
//    private NotificationTabServices notificationTabServices;
//
//    @InjectMocks
//    private NotificationTabController notificationTabController;
//
//    @Test
//    @DisplayName("Kiểm tra gửi thông báo")
//    public void testSendNotification() throws Exception {
//        String content = "Sự kiện đã được cập nhật!";
//        String type = "UPDATE";
//        int eventId = 123;
//
//        Mockito.when(notificationServices.sendNotificationForUser(content, type, eventId)).thenReturn(true);
//
//        boolean result = notificationServices.sendNotificationForUser(content, type, eventId);
//
//        Assertions.assertTrue(result);
//    }
//}
//}
