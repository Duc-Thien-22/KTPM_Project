/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ntn.testcase;

import com.ntn.controllers.Utils;
import com.ntn.pojo.Event;
import com.ntn.pojo.Ticket;
import com.ntn.pojo.Tickettype;
import com.ntn.pojo.Venue;
import com.ntn.services.EventServices;
import com.ntn.services.EventTabServices;
import com.ntn.services.NotificationServices;
import com.ntn.services.TicketServices;
import com.ntn.services.VenueServices;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.*;
import org.mockito.junit.jupiter.*;
import org.mockito.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

/**
 *
 * @author NHAT
 */
@ExtendWith(MockitoExtension.class)
public class EventTabServicesTester {

    @Mock
    private VenueServices venueServies;
    @Mock
    private EventServices eventServices;
    @Mock
    private NotificationServices notificationServices;
    @Mock
    private TicketServices ticketServices;

    @Spy
    @InjectMocks
    private EventTabServices eventTabServies;

    @BeforeAll
    public static void initToolkit() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        Platform.startup(latch::countDown);
        latch.await();  // đợi JavaFX toolkit khởi động
    }

    @BeforeEach
    public void setup(TestInfo testInfo) {
        if (testInfo.getTags().contains("testEmptyFields")) {
            Map<Tickettype, HBox> ticketRows = new HashMap<>();

            HBox row = new HBox(10);
            Tickettype ticketType = new Tickettype(1, "Loại 1");

            Label nameLabel = new Label(String.format("Loại vé : %s", ticketType.getName()));
//        nameLabel.setPrefWidth(100);
            TextField quantityField = new TextField("");
//        quantityField.setPromptText("Nhập số lượng");

            TextField priceField = new TextField("");
//        priceField.setPromptText("Nhập giá vé");

            row.getChildren().addAll(nameLabel, quantityField, priceField);

            ticketRows.put(ticketType, row);

            // Re-initialize EventTabServices với Map thật
            eventTabServies = new EventTabServices(
                    venueServies,
                    eventServices,
                    notificationServices,
                    ticketServices,
                    ticketRows
            );
        }
    }

    @ParameterizedTest
    @CsvSource({
        "'', '', '', '', '', '',''",
        "'', '2025-05-01', '10:00', '2025-05-02', '12:00', 'White place - 30', '30'",
        "'Sự kiện A', '', '10:00', '2025-05-02', '12:00', 'White place - 30', '30'",
        "'Sự kiện A', '2025-05-01', '', '2025-05-02', '12:00', 'White place - 30', '30'",
        "'Sự kiện A', '2025-05-01', '10:00', '', '12:00', 'White place - 30', '30'",
        "'Sự kiện A', '2025-05-01', '10:00', '2025-05-02', '', 'White place - 30', '30'",
        "'Sự kiện A', '2025-05-01', '10:00', '2025-05-02', '12:00', '', '30'",
        "'Sự kiện A', '2025-05-01', '10:00', '2025-05-02', '12:00', 'White place - 30', ''"
    })
    @DisplayName("Test các trường rỗng")
    @Tag("testEmptyFields")
    public void testEmptyFields(
            String eventName,
            String startDate,
            String startTime,
            String endDate,
            String endTime,
            String venueName,
            String eventCapacity
    ) throws Exception {

        LocalDate parsedStartDate = startDate == null || startDate.isEmpty() ? null : LocalDate.parse(startDate);
        LocalDate parsedEndDate = endDate == null || endDate.isEmpty() ? null : LocalDate.parse(endDate);
        boolean isValid = this.eventTabServies.isInputValid(
                eventName, parsedStartDate, startTime, parsedEndDate, endTime, venueName, eventCapacity);

        Assertions.assertFalse(isValid);

    }

    @Test
    @DisplayName("Kiểm tra lấy danh sách địa điểm")
    public void testGetVenues() throws SQLException {
        List<Venue> venues = new ArrayList<>();

        Venue v1 = new Venue();
        v1.setName("Địa điểm A");
        Venue v2 = new Venue();
        v2.setName("Địa điểm B");
        venues.add(v1);
        venues.add(v2);

        Mockito.when(this.venueServies.getVenues()).thenReturn(venues);
        List<Venue> result = this.eventTabServies.getVenues();

        Assertions.assertEquals(2, result.size());
        Assertions.assertEquals("Địa điểm A", result.get(0).getName());

        Mockito.verify(this.venueServies, Mockito.times(1)).getVenues();
    }

    @Test
    @DisplayName("Kiểm tra lấy danh sự kiện")
    public void testGetEvents() throws SQLException {
        List<Event> events = new ArrayList<>();
        Event e1 = new Event();
        Event e2 = new Event();

        e1.setName("Test1");
        e2.setName("Test2");
        events.add(e1);
        events.add(e2);

        Mockito.when(this.eventServices.getEvents(0, "")).thenReturn(events);

        List<Event> result = this.eventTabServies.getEvents(0, "");
        Assertions.assertEquals(2, result.size());
        Assertions.assertEquals("Test1", result.get(0).getName());

        Mockito.verify(this.eventServices, Mockito.times(1)).getEvents(0, "");
    }

    @Test
    @DisplayName("Kiểm tra lấy danh sự kiện")
    public void testGetTicketTypes() throws SQLException {
        List<Tickettype> ticketTypes = new ArrayList<>();
        Tickettype t1 = new Tickettype();
        Tickettype t2 = new Tickettype();

        t1.setName("Loại 1");
        t2.setName("Loại 2");
        ticketTypes.add(t1);
        ticketTypes.add(t2);

        Mockito.when(this.ticketServices.getTicketTypes()).thenReturn(ticketTypes);

        List<Tickettype> result = this.eventTabServies.getTicketTypes();
        Assertions.assertEquals(2, result.size());
        Assertions.assertEquals("Loại 1", result.get(0).getName());

        Mockito.verify(this.ticketServices, Mockito.times(1)).getTicketTypes();
    }

    @Test
    @DisplayName("Kiểm tra trạng thái của sự kiện")
    public void testEventDisable() {
        Event e = new Event();
        e.setIsActive(Boolean.FALSE);

        String result = this.eventTabServies.checkValidAction(e);
        Assertions.assertEquals("Sự kiện đã hết hạn !!", result);
    }

    @Test
    @DisplayName("Kiểm tra sự kiện có được chỉnh sửa khi chuẩn bị diễn ra")
    public void testEventIsUpdateOR() {
        Event e = new Event();
        e.setStartDate(new Timestamp(System.currentTimeMillis() + 30 * 60 * 1000)); // +30 phút

        Mockito.doReturn(null).when(this.eventTabServies).checkEventStatus(e);

        String result = this.eventTabServies.checkValidAction(e);
        Assertions.assertEquals("Sự kiện không được chỉnh hoặc hủy sau 24h", result);
    }

    @Test
    @DisplayName("Được chỉnh sửa nếu còn trên 24h")
    public void testCheckValidAction_whenEventIsValid() {
        Event e = new Event();
        e.setStartDate(new Timestamp(System.currentTimeMillis() + 25 * 60 * 60 * 1000)); // +25 giờ

        Mockito.doReturn(null).when(this.eventTabServies).checkEventStatus(e);

        String result = this.eventTabServies.checkValidAction(e);
        Assertions.assertNull(result);  // Không lỗi gì
    }

    @Test
    @DisplayName("Không được chỉnh sửa nếu sự kiện đã hết hạn")
    public void testEventExpired() {
        Event e = new Event();
        e.setIsActive(false);  // Đã hết hạn
        e.setStartDate(new Timestamp(System.currentTimeMillis() + 48 * 60 * 60 * 1000)); // +48 giờ

        String result = this.eventTabServies.checkValidAction(e);
        Assertions.assertEquals("Sự kiện đã hết hạn !!", result);
    }

    @Test
    @DisplayName("Không được chỉnh nếu còn đúng 24h (biên)")
    public void testEventExactly24hLeft() {
        Event e = new Event();
        e.setIsActive(true);
        e.setStartDate(new Timestamp(System.currentTimeMillis() + 24 * 60 * 60 * 1000 - 2)); // đúng 24h

        Mockito.doReturn(null).when(this.eventTabServies).checkEventStatus(e);

        String result = this.eventTabServies.checkValidAction(e);
        Assertions.assertEquals("Sự kiện không được chỉnh hoặc hủy sau 24h", result);
    }

    @Test
    @DisplayName("Không được chỉnh nếu sự kiện đã bắt đầu")
    public void testEventInPast() {
        Event e = new Event();
        e.setIsActive(true);
        e.setStartDate(new Timestamp(System.currentTimeMillis() - 60 * 60 * 1000)); // -1 giờ

        Mockito.doReturn(null).when(this.eventTabServies).checkEventStatus(e);

        String result = this.eventTabServies.checkValidAction(e);
        Assertions.assertEquals("Sự kiện không được chỉnh hoặc hủy sau 24h", result);
    }

    @Test
    @DisplayName("Xử lý khi ngày bắt đầu bị null")
    public void testEventStartDateIsNull() {
        Event e = new Event();
        e.setIsActive(true);
        e.setStartDate(null);

        Mockito.doReturn(null).when(this.eventTabServies).checkEventStatus(e);

        Assertions.assertThrows(NullPointerException.class, () -> {
            this.eventTabServies.checkValidAction(e);
        });
    }

    @DisplayName("Kiểm tra ngày tạo sự kiện phải lớn hơn ngày hiện tại")
    @ParameterizedTest
    @CsvSource({
        "'07:00ewrw','23:00'",
        "'07:00','23:00ewrw'"
    })
    public void testEventStartTimeAndEndTimeInvalid(String startTime, String endTime) throws SQLException {
        String[] errorMsg = new String[1];
        Mockito.doReturn(Boolean.TRUE).when(this.eventTabServies).isInputValid(
                "Test",
                LocalDate.parse("2025-04-25"),
                startTime,
                LocalDate.parse("2025-04-25"),
                endTime,
                "White place - 30",
                "1");
        List<Ticket> tickets = this.eventTabServies.getTicketsValid(
                "Test",
                LocalDate.parse("2025-04-25"),
                startTime,
                LocalDate.parse("2025-04-25"),
                endTime,
                "White place - 30",
                "1",
                errorMsg);

        Assertions.assertNull(tickets);
        Assertions.assertEquals("Giờ không hợp lệ! Vui lòng nhập đúng định dạng HH:mm.",
                errorMsg[0]);
    }

    @Test
    @DisplayName("Kiểm tra ngày tạo sự kiện phải lớn hơn ngày hiện tại")
    public void testEventStartDateLessThanCurrentDay() throws SQLException {
        String[] errorMsg = new String[1];
        Mockito.doReturn(Boolean.TRUE).when(this.eventTabServies).isInputValid(
                "Test",
                LocalDate.parse("2025-04-23"),
                "07:00",
                LocalDate.parse("2025-04-23"),
                "23:00",
                "White place - 30",
                "1");
        List<Ticket> tickets = this.eventTabServies.getTicketsValid(
                "Test",
                LocalDate.parse("2025-04-23"),
                "07:00",
                LocalDate.parse("2025-04-23"),
                "23:00",
                "White place - 30",
                "1",
                errorMsg);

        Assertions.assertNull(tickets);
        Assertions.assertEquals("Ngày và giờ bắt đầu phải lớn hơn ngày hiện tại",
                errorMsg[0]);
    }

    @Test
    @DisplayName("Kiểm tra ngày kết thúc phải lớn hơn ngày bắt đầu sự kiện")
    public void testEventEndDateMoreThanStartDate() throws SQLException {
        String[] errorMsg = new String[1];
        Mockito.doReturn(Boolean.TRUE).when(this.eventTabServies).isInputValid(
                "Test",
                LocalDate.parse("2025-04-25"),
                "07:00",
                LocalDate.parse("2025-04-23"),
                "23:00",
                "White place - 30",
                "1");
        List<Ticket> tickets = this.eventTabServies.getTicketsValid(
                "Test",
                LocalDate.parse("2025-04-25"),
                "07:00",
                LocalDate.parse("2025-04-23"),
                "23:00",
                "White place - 30",
                "1",
                errorMsg);

        Assertions.assertNull(tickets);
        Assertions.assertEquals("Ngày và giờ kết thúc phải lớn hơn ngày bắt đầu",
                errorMsg[0]);
    }

    @ParameterizedTest
    @CsvSource({
        "'2025-04-26','07:00','2025-04-26','23:00'",
        "'2025-04-26','06:59','2025-04-26','23:00'",
        "'2025-04-26','06:00','2025-04-26','23:59'",
        "'2025-04-26','08:00','2025-04-26','10:00'",
        "'2025-04-25','23:00','2025-04-27','01:00'",
        "'2025-04-26','05:00','2025-04-26','06:59'",
        "'2025-04-26','23:01','2025-04-26','23:59'",
        "'2025-04-25','07:00','2025-04-26','23:00'"
    })
    @DisplayName("Kiểm tra tạo sự kiện nhưng trùng lịch với một sự kiện khác")
    public void testCreateEventEqualEventExist(String startDate, String startTime, String endDate, String endTime) throws SQLException {
        EventServices e = new EventServices();
        
        Timestamp start = Utils.convertToTimestamp(LocalDate.parse(startDate), startTime);
        Timestamp end = Utils.convertToTimestamp(LocalDate.parse(endDate), endTime);
        Event event = e.checkVenueAndDateTime(1, start, end, 0);
        
        System.out.println("Start: " + start + " - End: " + end);

        Assertions.assertNotNull(event);
    }
}
