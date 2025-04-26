/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ntn.testcase;

import com.ntn.pojo.DTO.NotificationDTO;
import com.ntn.pojo.Event;
import com.ntn.pojo.JdbcUtils;
import com.ntn.pojo.Registration;
import com.ntn.pojo.Ticket;
import com.ntn.pojo.Tickettype;
import com.ntn.pojo.User;
import com.ntn.pojo.Venue;
import com.ntn.services.EventServices;
import com.ntn.services.EventTabServices;
import com.ntn.services.NotificationServices;
import com.ntn.services.NotificationTabServices;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.*;
import org.mockito.junit.jupiter.*;
import org.mockito.*;
import org.junit.jupiter.api.Assertions;

/**
 *
 * @author admin
 */
@ExtendWith(MockitoExtension.class)
public class NotificationTabServicesTest {

    @Mock
    private EventServices eventServices;

    @Mock
    private NotificationServices notificationServices;

    @InjectMocks
    private NotificationTabServices notificationTabServices;

    @InjectMocks
    private EventTabServices eventTabServices;

    @Test
    @DisplayName("Kiểm tra lấy được lịch sử thông báo hay không")
    public void testNoticesHistory() throws Exception {
        List<NotificationDTO> mockNotifications = Arrays.asList(
                new NotificationDTO(1, "Thông báo 1", new Timestamp(System.currentTimeMillis()), "user1", "Sự kiện A")
        );

        Mockito.when(notificationServices.getNotifications(null)).thenReturn(mockNotifications);

        List<NotificationDTO> result = notificationTabServices.getNotificationHistory();

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals("Thông báo 1", result.get(0).getContent());
        Assertions.assertEquals("Sự kiện A", result.get(0).getEventName());

    }

    @Test
    @DisplayName("Kiểm tra có gửi được thông báo hay không")
    public void testSendNotification() throws SQLException {
        int eventId = 1;
        String content = "Hôm nay là thứ 2";
        int insert = 3;

        Mockito.when(notificationServices.sendNotificationForUser(content, "UPDATE", eventId)).thenReturn(insert);

        boolean result = notificationTabServices.sendNotification(eventId, content);
        Assertions.assertTrue(result);

    }

    @Test
    @DisplayName("Kiểm tra gửi thông báo nhắc sự kiện sắp diễn ra trước 1 ngày")
    public void testAutoReminderNotification() throws SQLException {
        Venue mockVenue = new Venue();
        mockVenue.setId(1);

        Event mockEvent = new Event();
        mockEvent.setId(1);
        mockEvent.setIsActive(true);
        mockEvent.setStartDate(new Timestamp(System.currentTimeMillis() + 24 * 60 * 60 * 1000));
        mockEvent.setEndDate(new Timestamp(System.currentTimeMillis() + 25 * 60 * 60 * 1000));
        mockEvent.setVenue(mockVenue);
        mockEvent.setMaxAttendees(30);

        List<Event> mockEvents = Arrays.asList(mockEvent);
        List<Integer> mockRegisterIds = Arrays.asList(1, 2);

        Mockito.when(eventServices.getEvents()).thenReturn(mockEvents);
        Mockito.when(eventServices.getRegisterByEventId(1)).thenReturn(mockRegisterIds);
        Mockito.when(notificationServices.isUsersRemider(mockRegisterIds)).thenReturn(false);
        Mockito.when(notificationServices.sendNotificationForUser(
                Mockito.anyString(), Mockito.eq("REMINDER"), Mockito.anyInt())
        ).thenReturn(2);

        int result = notificationTabServices.autoRemiderNotification();

        Assertions.assertEquals(2, result);
    }

    @Test
    @DisplayName("Gửi thông báo được cho người đăng ký trong khoảng thời gian 24h đổ lại trước khi sự kiện diễn ra")
    public void testCheckUserReceiveNoti() throws SQLException {
        Venue mockVenue = new Venue();
        mockVenue.setId(1);

        Event mockEvent = new Event();
        mockEvent.setId(1);
        mockEvent.setIsActive(true);
        mockEvent.setStartDate(new Timestamp(System.currentTimeMillis() + 24 * 60 * 60 * 1000));
        mockEvent.setEndDate(new Timestamp(System.currentTimeMillis() + 33 * 60 * 60 * 1000));
        mockEvent.setVenue(mockVenue);
        mockEvent.setMaxAttendees(50);

//        Tickettype mockTicketType = new Tickettype();
//        mockTicketType.setId(1);
//
//        Ticket mockTicket = new Ticket();
//        mockTicket.setEventId(mockEvent);
//        mockTicket.setId(1);
//        mockTicket.setTicketTypeId(mockTicketType);
        User mockUser1 = new User();
        mockUser1.setId(1);

        User mockUser2 = new User();
        mockUser2.setId(2);

        Registration mockRegis1 = new Registration();
        mockRegis1.setId(1);
        mockRegis1.setUserId(mockUser1);
        mockRegis1.setCreatedDate(new Timestamp(System.currentTimeMillis() - 2 * 60 * 60 * 1000));
        mockRegis1.setUpdatedDate(new Timestamp(System.currentTimeMillis() - 2 * 60 * 60 * 1000));
        //mockRegis1.setTicketId(mockTicket);

        Registration mockRegis2 = new Registration();
        mockRegis2.setId(2);
        mockRegis2.setUserId(mockUser2);
        mockRegis2.setCreatedDate(new Timestamp(System.currentTimeMillis() + 4 * 60 * 60 * 1000));
        mockRegis2.setUpdatedDate(new Timestamp(System.currentTimeMillis() + 4 * 60 * 60 * 1000));
        //mockRegis2.setTicketId(mockTicket);

        List<Event> mockEvents = Arrays.asList(mockEvent);
        List<Integer> mockRegisterIds = Arrays.asList(mockUser1.getId(), mockUser2.getId());

        Mockito.when(eventServices.getEvents()).thenReturn(mockEvents);
        Mockito.when(eventServices.getRegisterByEventId(1)).thenReturn(mockRegisterIds);
        Mockito.when(notificationServices.isUsersRemider(mockRegisterIds)).thenReturn(false);
        Mockito.when(notificationServices.sendNotificationForUser(
                Mockito.anyString(), Mockito.eq("REMINDER"), Mockito.anyInt())
        ).thenReturn(1);

        int result = notificationTabServices.autoRemiderNotification();

        Assertions.assertEquals(2, result);
    }

    @Test
    @DisplayName("Không gửi lại thông báo nếu người dùng đã được nhắc")
    public void testReminderAlreadySent() throws SQLException {
        Venue mockVenue = new Venue();
        mockVenue.setId(1);

        Event mockEvent = new Event();
        mockEvent.setId(1);
        mockEvent.setIsActive(true);
        mockEvent.setStartDate(new Timestamp(System.currentTimeMillis() + 24 * 60 * 60 * 1000));
        mockEvent.setEndDate(new Timestamp(System.currentTimeMillis() + 26 * 60 * 60 * 1000));
        mockEvent.setVenue(mockVenue);
        mockEvent.setMaxAttendees(50);

        User mockUser1 = new User();
        mockUser1.setId(1);

        Registration reg = new Registration();
        reg.setId(1);
        reg.setCreatedDate(new Timestamp(System.currentTimeMillis() - 2 * 60 * 60 * 1000));
        reg.setUserId(mockUser1);

        List<Event> mockEvents = Arrays.asList(mockEvent);
        List<Integer> mockRegis = Arrays.asList(mockUser1.getId());

        Mockito.when(eventServices.getEvents()).thenReturn(mockEvents);
        Mockito.when(eventServices.getRegisterByEventId(1)).thenReturn(mockRegis);
        Mockito.when(notificationServices.isUsersRemider(mockRegis)).thenReturn(true);

        int result = notificationTabServices.autoRemiderNotification();
        Assertions.assertEquals(0, result);
    }

    @Test
    @DisplayName("Không gửi thông báo cho sự kiện không hoạt động")
    public void testEventNoNotification() throws SQLException {
        Venue mockVenue = new Venue();
        mockVenue.setId(1);

        Event mockEvent = new Event();
        mockEvent.setId(1);
        mockEvent.setIsActive(false);
        mockEvent.setStartDate(new Timestamp(System.currentTimeMillis() + 24 * 60 * 60 * 1000));
        mockEvent.setEndDate(new Timestamp(System.currentTimeMillis() + 25 * 60 * 60 * 1000));
        mockEvent.setVenue(mockVenue);
        mockEvent.setMaxAttendees(30);

        List<Event> mockEvents = Arrays.asList(mockEvent);
        List<Integer> mockRegisterIds = Arrays.asList(1);

        Mockito.when(eventServices.getEvents()).thenReturn(mockEvents);
        Mockito.lenient().when(eventServices.getRegisterByEventId(1)).thenReturn(mockRegisterIds);
        Mockito.lenient().when(notificationServices.isUsersRemider(mockRegisterIds)).thenReturn(false);
        Mockito.lenient().when(notificationServices.sendNotificationForUser(
                Mockito.anyString(), Mockito.eq("REMINDER"), Mockito.anyInt())
        ).thenReturn(1);

        int result = notificationTabServices.autoRemiderNotification();

        Assertions.assertEquals(0, result);
    }

    @Test
    @DisplayName("Kiểm tra khi hủy sự kiện thì thông báo được gửi thành công đến các người dùng đã đăng ký")
    public void testSendNotification_Success() throws SQLException {
        Event event = new Event();
        event.setId(36);
        event.setIsActive(Boolean.FALSE);
        String content = "Sự kiện bạn đã đăng ký đã bị hủy";
        String type = "DELETE";

        try (Connection conn = JdbcUtils.getConnection()) {
            EventTabServices e = new EventTabServices();

            boolean result = e.sendNotification(event.getId(), content, type);
            Assertions.assertTrue(result, "Gửi thông báo không thành công");

            String sql = "SELECT registration.id FROM event "
                    + "JOIN ticket ON event.id = ticket.event_id "
                    + "JOIN registration ON registration.ticket_id = ticket.id "
                    + "WHERE event.id = ?";
            PreparedStatement stm = conn.prepareCall(sql);
            stm.setInt(1, event.getId());
            ResultSet rs = stm.executeQuery();

            List<Integer> registerIds = new ArrayList<>();
            while (rs.next()) {
                registerIds.add(rs.getInt("id"));
            }

            for (Integer regId : registerIds) {
                String notifySql = "SELECT * FROM notification WHERE register_id = ? AND content = ? AND notification_type = ?";
                PreparedStatement notifyStm = conn.prepareCall(notifySql);
                notifyStm.setInt(1, regId);
                notifyStm.setString(2, content);
                notifyStm.setString(3, type);
                ResultSet notifyRs = notifyStm.executeQuery();

                Assertions.assertTrue(notifyRs.next(), "Không tìm thấy thông báo cho register_id = " + regId);
            }
        }
    }
    
    @Test
    @DisplayName("Kiểm tra xem sự k")
    public void testUpdate() throws SQLException {
//        Event event = new Event();
//        event.setId(36);
//        event.setIsActive(Boolean.TRUE);
//        
//        Mockito.when(eventServices.updateEventById(event)).thenReturn(event);
//        
//        Assertions.assertTrue(eventTabServices.sendNotification(0, content, type));
//        
//        
//
//        EventTabServices e = new EventTabServices();
//
//        // Gọi hàm gửi thông báo
//        boolean result = e.sendNotification(event.getId(), content, type);
//        Assertions.assertFalse(result, "Gửi thông báo thành công");
    }


}
