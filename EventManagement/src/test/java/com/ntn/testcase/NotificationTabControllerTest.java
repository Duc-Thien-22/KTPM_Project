/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ntn.testcase;


import com.ntn.controllers.NotificationTabController;
import com.ntn.pojo.Notification;
import com.ntn.services.NotificationTabServices;
import org.junit.jupiter.api.*;
import org.mockito.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.extension.*;
import org.mockito.junit.jupiter.*;
import java.util.List;
/**
 *
 * @author Duc Thien
 */
@ExtendWith(MockitoExtension.class)
public class NotificationTabControllerTest {

    @Mock
    private NotificationTabServices notificationTabServices;

    @InjectMocks
    private NotificationTabController notificationTabController;

    @Test
    @DisplayName("Kiểm tra load thông báo khi sự kiện cập nhật")
    public void testLoadNotificationWhenEventUpdated() throws Exception {
        Mockito.when(notificationTabServices.loadNotifications()).thenReturn(List.of(new Notification("Sự kiện đã cập nhật")));

        List<Notification> result = notificationTabController.loadNotifications();

        Assertions.assertFalse(result.isEmpty());
        Assertions.assertEquals("Sự kiện đã cập nhật", result.get(0).getMessage());
    }
}
