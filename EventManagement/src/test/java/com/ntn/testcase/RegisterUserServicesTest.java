/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ntn.testcase;

import com.ntn.controllers.Utils;
import com.ntn.pojo.DTO.EventDTO;
import com.ntn.pojo.DTO.NotificationDTO;
import com.ntn.services.EventServices;
import com.ntn.services.NotificationServices;
import com.ntn.services.RegisterUserServices;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.junit.jupiter.*;
import org.mockito.*;
import org.junit.jupiter.api.extension.*;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 *
 * @author Duc Thien
 */
@ExtendWith(MockitoExtension.class)
public class RegisterUserServicesTest {

    @InjectMocks
    private RegisterUserServices registerUserServices;

    @Mock
    private EventServices eventServicesMock;
    @Mock
    private NotificationServices notificationServicesMock;

//    @BeforeEach
//    void setUp() {
//        // Tạo mock
//        eventServicesMock = mock(EventServices.class);
//        notificationServicesMock = mock(NotificationServices.class);
//
//        // Inject mock vào RegisterUserServices bằng cách kế thừa tạm để chèn (nếu cần)
//        registerUserServices = new RegisterUserServices() {
//            {
//                // Gán lại bằng mock
//                super.eventServices = eventServicesMock;
//                super.notificationServices = notificationServicesMock;
//            }
//        };
//    }
    @Test
    public void testGetEventsSuccess() throws SQLException {
        List<EventDTO> expectedEvents = List.of(new EventDTO(), new EventDTO());

        when(eventServicesMock.getEvents("music")).thenReturn(expectedEvents);

        List<EventDTO> result = registerUserServices.getEvents("music");

        assertEquals(2, result.size());
        verify(eventServicesMock, times(1)).getEvents("music");
    }

    @Test
    public void testGetEventsSQLException() throws SQLException {
        when(eventServicesMock.getEvents("fail")).thenThrow(new SQLException("Database error"));

        assertThrows(SQLException.class, () -> {
            registerUserServices.getEvents("fail");
        });

        verify(eventServicesMock, times(1)).getEvents("fail");
    }

    @Test
    public void testCheckTimeConflictTrue() throws SQLException {
        when(eventServicesMock.checkStatusRegis(1, 100)).thenReturn(true);

        boolean result = registerUserServices.checkTimeConflict(1, 100);

        assertTrue(result);
        verify(eventServicesMock, times(1)).checkStatusRegis(1, 100);
    }

    @Test
    public void testCheckTimeConflictFalse() throws SQLException {
        when(eventServicesMock.checkStatusRegis(2, 101)).thenReturn(false);

        boolean result = registerUserServices.checkTimeConflict(2, 101);

        assertFalse(result);
        verify(eventServicesMock, times(1)).checkStatusRegis(2, 101);
    }

    @Test
    public void testGetTicketInfo() {
        Map<String, Object[]> ticketInfo = new HashMap<>();
        ticketInfo.put("VIP", new Object[]{50, 100});
        ticketInfo.put("Normal", new Object[]{200, 50});

        when(eventServicesMock.getTicketInfo(10)).thenReturn(ticketInfo);

        Map<String, Object[]> result = registerUserServices.getTicketInfo(10);

        assertEquals(2, result.size());
        assertTrue(result.containsKey("VIP"));
        assertTrue(result.containsKey("Normal"));
        verify(eventServicesMock, times(1)).getTicketInfo(10);
    }

    @Test
    public void testProcessPaymentSuccess() {
        when(eventServicesMock.processPayment(1, "VIP", 100)).thenReturn(true);

        boolean result = registerUserServices.processPayment(1, "VIP", 100);

        assertTrue(result);
        verify(eventServicesMock, times(1)).processPayment(1, "VIP", 100);
    }

    @Test
    public void testProcessPaymentFailure() {
        when(eventServicesMock.processPayment(2, "Normal", 101)).thenReturn(false);

        boolean result = registerUserServices.processPayment(2, "Normal", 101);

        assertFalse(result);
        verify(eventServicesMock, times(1)).processPayment(2, "Normal", 101);
    }

    @Test
    public void testGetNotificationsSorted() throws SQLException {
        NotificationDTO noti1 = new NotificationDTO();
        Timestamp start = Utils.convertToTimestamp(LocalDate.parse("2025-04-26"), "07:00");
        noti1.setCreatedDate(start);

        NotificationDTO noti2 = new NotificationDTO();
        noti2.setCreatedDate(start);

        List<NotificationDTO> unsortedList = Arrays.asList(noti1, noti2);
        when(notificationServicesMock.getNotifications(100)).thenReturn(unsortedList);

        List<NotificationDTO> result = registerUserServices.getNotifications(100);

        assertEquals(2, result.size());
        assertTrue(result.get(0).getCreatedDate().toLocalDateTime()
                .isAfter(result.get(1).getCreatedDate().toLocalDateTime()));
        verify(notificationServicesMock, times(1)).getNotifications(100);
    }

    @Test
    public void testGetNotificationsEmpty() throws SQLException {
        when(notificationServicesMock.getNotifications(101)).thenReturn(Collections.emptyList());

        List<NotificationDTO> result = registerUserServices.getNotifications(101);

        assertTrue(result.isEmpty());
        verify(notificationServicesMock, times(1)).getNotifications(101);
    }

    @Test
    public void testGetRegisteredEventsSuccess() throws SQLException {
        List<EventDTO> events = List.of(new EventDTO());
        when(eventServicesMock.getEvents(100)).thenReturn(events);

        List<EventDTO> result = registerUserServices.getRegisteredEvents(100);

        assertEquals(1, result.size());
        verify(eventServicesMock, times(1)).getEvents(100);
    }

    @Test
    public void testGetRegisteredEventsSQLException() throws SQLException {
        when(eventServicesMock.getEvents(200)).thenThrow(new SQLException("Database error"));

        assertThrows(SQLException.class, () -> {
            registerUserServices.getRegisteredEvents(200);
        });

        verify(eventServicesMock, times(1)).getEvents(200);
    }
}
