/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ntn.testcase;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.*;
import org.mockito.junit.jupiter.*;
import org.mockito.*;
import com.ntn.controllers.EventTabController;
import com.ntn.services.EventTabServices;
import com.ntn.pojo.Event;
import java.util.List;

/**
 *
 * @author Duc Thien
 */
@ExtendWith(MockitoExtension.class)
public class EventTabControllerTest {

    @Mock
    private EventTabServices eventTabServices;

    @InjectMocks
    private EventTabController eventTabController;

    @Test
    @DisplayName("Kiểm tra tìm kiếm sự kiện theo từ khóa")
    public void testSearchEventByKeyword() throws Exception {
        String keyword = "abc"; // Từ khóa tìm kiếm

        // Giả lập phương thức loadEvent() được gọi trong controller khi có thay đổi từ ô tìm kiếm
        Mockito.when(eventTabServices.getEventServices().getEvents(0, keyword)).thenReturn(List.of(new Event("abc Event")));

        // Gọi phương thức để thực hiện tìm kiếm
        eventTabController.loadEvent(keyword);

        // Kiểm tra danh sách sự kiện trả về
        Assertions.assertFalse(eventTabController.getTbEvents().getItems().isEmpty());
        Assertions.assertTrue(eventTabController.getTbEvents().getItems().get(0).getName().contains(keyword));
    }

    @Test
    @DisplayName("Kiểm tra tìm kiếm sự kiện theo tên sự kiện")
    public void testSearchEventByName() throws Exception {
        String eventName = "Music Festival";
        Mockito.when(eventTabServices.searchEventByName(eventName)).thenReturn(List.of(new Event(eventName)));

        List<Event> result = eventTabController.searchEventByName(eventName);

        Assertions.assertEquals(1, result.size());
        Assertions.assertTrue(result.get(0).getName().contains(eventName));
    }

    @Test
    @DisplayName("Kiểm tra trạng thái thanh toán")
    public void testPaymentStatus() throws Exception {
        String result = eventTabController.checkPaymentStatus(1);  // Giả sử tham số '1' là ID của sự kiện
        Assertions.assertEquals("Đã thanh toán", result);
    }

    @Test
    @DisplayName("Kiểm tra email hợp lệ khi đăng nhập")
    public void testValidEmailOnLogin() throws Exception {
        String email = "test@example.com";
        Mockito.when(eventTabServices.checkEmailValidity(email)).thenReturn(true);

        boolean result = eventTabController.checkEmailValidity(email);

        Assertions.assertTrue(result);
    }

    @Test
    @DisplayName("Kiểm tra sự kiện đã được tạo thành công")
    public void testCreateEventSuccess() throws Exception {
        Event newEvent = new Event("Music Event");
        Mockito.when(eventTabServices.createEvent(newEvent)).thenReturn(true);

        boolean result = eventTabController.createEvent(newEvent);

        Assertions.assertTrue(result);
    }

    @Test
    @DisplayName("Kiểm tra sự kiện đang diễn ra có thể đăng ký")
    public void testEventActiveRegistration() throws Exception {
        Event activeEvent = new Event("Active Event", true); // Trạng thái sự kiện là "Đang diễn ra"
        Mockito.when(eventTabServices.checkEventStatus(activeEvent.getId())).thenReturn(true);

        boolean result = eventTabController.checkEventActive(activeEvent);

        Assertions.assertTrue(result);
    }
    
    @Test
    @DisplayName("Kiểm tra khi người dùng không thể đăng ký khi sự kiện không có vé")
    public void testEventCannotRegisterWhenNoTickets() throws Exception {
        Event event = new Event("Event A");
        event.setTicketsRemaining(0); // Không còn vé

        boolean isRegisterable = eventTabController.isEventRegisterable(event);

        Assertions.assertFalse(isRegisterable);
    }
    
    @Test
    @DisplayName("Kiểm tra sự kiện không thể đăng ký khi đã kết thúc")
    public void testEventCannotRegisterAfterEndDate() throws Exception {
        Event event = new Event("Event A");
        event.setStatus(EventStatus.ENDED); // Đã kết thúc

        boolean isRegisterable = eventTabController.isRegisterButtonEnabled(event);
        Assertions.assertFalse(isRegisterable);
    }
}
