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
public class EventTabServicesTest {

    @Mock
    private VenueServices venueServices;
    @Mock
    private EventServices eventServices;
    @Mock
    private NotificationServices notificationServices;
    @Mock
    private TicketServices ticketServices;

    @Spy
    @InjectMocks
    private EventTabServices eventTabServices;

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
            eventTabServices = new EventTabServices(
                    venueServices,
                    eventServices,
                    notificationServices,
                    ticketServices,
                    ticketRows
            );
        }

        if (testInfo.getTags().contains("testNotEmptyFields")) {
            Map<Tickettype, HBox> ticketRows = new HashMap<>();

            HBox row1 = new HBox(10);
            Tickettype ticketType1 = new Tickettype(1, "Loại 1");

            Label nameLabel1 = new Label(String.format("Loại vé : %s", ticketType1.getName()));
//        nameLabel.setPrefWidth(100);
            TextField quantityField1 = new TextField("20");
//        quantityField.setPromptText("Nhập số lượng");

            TextField priceField1 = new TextField("1000");
//        priceField.setPromptText("Nhập giá vé");

            row1.getChildren().addAll(nameLabel1, quantityField1, priceField1);

            HBox row2 = new HBox(10);
            Tickettype ticketType2 = new Tickettype(1, "Loại 2");

            Label nameLabel2 = new Label(String.format("Loại vé : %s", ticketType2.getName()));
//        nameLabel.setPrefWidth(100);
            TextField quantityField2 = new TextField("10");
//        quantityField.setPromptText("Nhập số lượng");

            TextField priceField2 = new TextField("2000");
//        priceField.setPromptText("Nhập giá vé");

            row2.getChildren().addAll(nameLabel2, quantityField2, priceField2);

            ticketRows.put(ticketType1, row1);
            ticketRows.put(ticketType2, row2);

            // Re-initialize EventTabServices với Map thật
            eventTabServices = new EventTabServices(
                    venueServices,
                    eventServices,
                    notificationServices,
                    ticketServices,
                    ticketRows
            );
        }

        if (testInfo.getTags().contains("testTotalTicketsExceedCapacity")) {
            Map<Tickettype, HBox> ticketRows = new HashMap<>();

            HBox row1 = new HBox(10);
            Tickettype ticketType1 = new Tickettype(1, "Loại 1");
            row1.getChildren().addAll(new Label(String.format("Loại vé : %s", ticketType1.getName())),
                    new TextField("20"), new TextField("1000"));

            HBox row2 = new HBox(10);
            Tickettype ticketType2 = new Tickettype(1, "Loại 2");
            row2.getChildren().addAll(new Label(String.format("Loại vé : %s", ticketType2.getName())),
                    new TextField("50"), new TextField("2000"));
            ticketRows.put(ticketType1, row1);
            ticketRows.put(ticketType2, row2);

            eventTabServices = new EventTabServices(
                    venueServices,
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
        boolean isValid = this.eventTabServices.isInputValid(
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

        Mockito.when(this.venueServices.getVenues()).thenReturn(venues);
        List<Venue> result = this.eventTabServices.getVenues();

        Assertions.assertEquals(2, result.size());
        Assertions.assertEquals("Địa điểm A", result.get(0).getName());

        Mockito.verify(this.venueServices, Mockito.times(1)).getVenues();
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

        List<Event> result = this.eventTabServices.getEvents(0, "");
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

        List<Tickettype> result = this.eventTabServices.getTicketTypes();
        Assertions.assertEquals(2, result.size());
        Assertions.assertEquals("Loại 1", result.get(0).getName());

        Mockito.verify(this.ticketServices, Mockito.times(1)).getTicketTypes();
    }

    @Test
    @DisplayName("Kiểm tra trạng thái của sự kiện")
    public void testEventDisable() {
        Event e = new Event();
        e.setIsActive(Boolean.FALSE);

        String result = this.eventTabServices.checkValidAction(e);
        Assertions.assertEquals("Sự kiện đã hết hạn !!", result);
    }

    @Test
    @DisplayName("Kiểm tra sự kiện có được chỉnh sửa khi chuẩn bị diễn ra")
    public void testEventIsUpdateOR() {
        Event e = new Event();
        e.setStartDate(new Timestamp(System.currentTimeMillis() + 30 * 60 * 1000)); // +30 phút

        Mockito.doReturn(null).when(this.eventTabServices).checkEventStatus(e);

        String result = this.eventTabServices.checkValidAction(e);
        Assertions.assertEquals("Sự kiện không được chỉnh hoặc hủy sau 24h", result);
    }

    @Test
    @DisplayName("Được chỉnh sửa nếu còn trên 24h")
    public void testCheckValidAction_whenEventIsValid() {
        Event e = new Event();
        e.setStartDate(new Timestamp(System.currentTimeMillis() + 25 * 60 * 60 * 1000)); // +25 giờ

        Mockito.doReturn(null).when(this.eventTabServices).checkEventStatus(e);

        String result = this.eventTabServices.checkValidAction(e);
        Assertions.assertNull(result);  // Không lỗi gì
    }

    @Test
    @DisplayName("Không được chỉnh sửa nếu sự kiện đã hết hạn")
    public void testEventExpired() {
        Event e = new Event();
        e.setIsActive(false);  // Đã hết hạn
        e.setStartDate(new Timestamp(System.currentTimeMillis() + 48 * 60 * 60 * 1000)); // +48 giờ

        String result = this.eventTabServices.checkValidAction(e);
        Assertions.assertEquals("Sự kiện đã hết hạn !!", result);
    }

    @Test
    @DisplayName("Không được chỉnh nếu còn đúng 24h (biên)")
    public void testEventExactly24hLeft() {
        Event e = new Event();
        e.setIsActive(true);
        e.setStartDate(new Timestamp(System.currentTimeMillis() + 24 * 60 * 60 * 1000 - 2)); // đúng 24h

        Mockito.doReturn(null).when(this.eventTabServices).checkEventStatus(e);

        String result = this.eventTabServices.checkValidAction(e);
        Assertions.assertEquals("Sự kiện không được chỉnh hoặc hủy sau 24h", result);
    }

    @Test
    @DisplayName("Không được chỉnh nếu sự kiện đã bắt đầu")
    public void testEventInPast() {
        Event e = new Event();
        e.setIsActive(true);
        e.setStartDate(new Timestamp(System.currentTimeMillis() - 60 * 60 * 1000)); // -1 giờ

        Mockito.doReturn(null).when(this.eventTabServices).checkEventStatus(e);

        String result = this.eventTabServices.checkValidAction(e);
        Assertions.assertEquals("Sự kiện không được chỉnh hoặc hủy sau 24h", result);
    }

    @Test
    @DisplayName("Xử lý khi ngày bắt đầu bị null")
    public void testEventStartDateIsNull() {
        Event e = new Event();
        e.setIsActive(true);
        e.setStartDate(null);

        Mockito.doReturn(null).when(this.eventTabServices).checkEventStatus(e);

        Assertions.assertThrows(NullPointerException.class, () -> {
            this.eventTabServices.checkValidAction(e);
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
        Mockito.doReturn(Boolean.TRUE).when(this.eventTabServices).isInputValid(
                "Test",
                LocalDate.parse("2025-05-01"),
                startTime,
                LocalDate.parse("2025-05-02"),
                endTime,
                "White place - 30",
                "1");
        List<Ticket> tickets = this.eventTabServices.getTicketsValid(
                "Test",
                LocalDate.parse("2025-05-01"),
                startTime,
                LocalDate.parse("2025-05-02"),
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
        Mockito.doReturn(Boolean.TRUE).when(this.eventTabServices).isInputValid(
                "Test",
                LocalDate.parse("2025-04-23"),
                "07:00",
                LocalDate.parse("2025-04-23"),
                "23:00",
                "White place - 30",
                "1");
        List<Ticket> tickets = this.eventTabServices.getTicketsValid(
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
        Mockito.doReturn(Boolean.TRUE).when(this.eventTabServices).isInputValid(
                "Test",
                LocalDate.parse("2025-05-01"),
                "07:00",
                LocalDate.parse("2025-04-23"),
                "23:00",
                "White place - 30",
                "1");
        List<Ticket> tickets = this.eventTabServices.getTicketsValid(
                "Test",
                LocalDate.parse("2025-05-01"),
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

    @Test
    @DisplayName("Kiểm tra số lượng người tham gia phải là số nguyên")
    public void testEventCapacityIsMustInt() throws SQLException {
        String[] errorMsg = new String[1];
        Mockito.doReturn(Boolean.TRUE).when(this.eventTabServices).isInputValid(
                "Test",
                LocalDate.parse("2025-04-26"),
                "07:00",
                LocalDate.parse("2025-04-26"),
                "23:00",
                "White place - 30",
                "sdfsf");

        Map<String, Integer> mockVenueMap = new HashMap<>();
        mockVenueMap.put("White place", 1);
        Mockito.doReturn(mockVenueMap).when(this.eventTabServices).getVenuesId();

        int venueId = this.eventTabServices.getVenuesId().get("White place");
        Timestamp start = Utils.convertToTimestamp(LocalDate.parse("2025-04-26"), "07:00");
        Timestamp end = Utils.convertToTimestamp(LocalDate.parse("2025-04-26"), "23:00");
        Mockito.when(this.eventServices.checkVenueAndDateTime(venueId, start, end, 0))
                .thenReturn(null);
        List<Ticket> tickets = this.eventTabServices.getTicketsValid(
                "Test",
                LocalDate.parse("2025-04-26"),
                "07:00",
                LocalDate.parse("2025-04-26"),
                "23:00",
                "White place - 30",
                "sdfsf",
                errorMsg);

        Assertions.assertNull(tickets);
        Assertions.assertEquals("Số lượng khách phải là số nguyên dương",
                errorMsg[0]);
    }

    @Test
    @DisplayName("Kiểm tra số lượng người tham gia không được lớn hơn số lượng ghế của nơi tổ chức")
    public void testEventCapaticyMoreThanMaxattend() throws SQLException {
        String[] errorMsg = new String[1];
        Mockito.doReturn(Boolean.TRUE).when(this.eventTabServices).isInputValid(
                "Test",
                LocalDate.parse("2025-04-26"),
                "07:00",
                LocalDate.parse("2025-04-26"),
                "23:00",
                "White place - 30",
                "40");

        Map<String, Integer> mockVenueMap = new HashMap<>();
        mockVenueMap.put("White place", 1);
        Mockito.doReturn(mockVenueMap).when(this.eventTabServices).getVenuesId();

        int venueId = this.eventTabServices.getVenuesId().get("White place");
        Timestamp start = Utils.convertToTimestamp(LocalDate.parse("2025-04-26"), "07:00");
        Timestamp end = Utils.convertToTimestamp(LocalDate.parse("2025-04-26"), "23:00");
        Mockito.when(this.eventServices.checkVenueAndDateTime(venueId, start, end, 0))
                .thenReturn(null);

        Venue mockVenue = Mockito.mock(Venue.class);
        Mockito.when(mockVenue.getCapacity()).thenReturn(30);
        Mockito.when(this.venueServices.getVenueById(venueId)).thenReturn(mockVenue);

        List<Ticket> tickets = this.eventTabServices.getTicketsValid(
                "Test",
                LocalDate.parse("2025-04-26"),
                "07:00",
                LocalDate.parse("2025-04-26"),
                "23:00",
                "White place - 30",
                "40",
                errorMsg);

        Assertions.assertNull(tickets);
        Assertions.assertEquals("Số lượng khách lớn hơn sức chứa của địa điểm",
                errorMsg[0]);
    }

    @Test
    @DisplayName("Kiểm tra số lượng người tham gia không được lớn hơn số lượng ghế của nơi tổ chức")
    public void testEventCapaticyMoreThanZero() throws SQLException {
        String[] errorMsg = new String[1];
        Mockito.doReturn(Boolean.TRUE).when(this.eventTabServices).isInputValid(
                "Test",
                LocalDate.parse("2025-04-26"),
                "07:00",
                LocalDate.parse("2025-04-26"),
                "23:00",
                "White place - 30",
                "-40");

        Map<String, Integer> mockVenueMap = new HashMap<>();
        mockVenueMap.put("White place", 1);
        Mockito.doReturn(mockVenueMap).when(this.eventTabServices).getVenuesId();

        int venueId = this.eventTabServices.getVenuesId().get("White place");
        Timestamp start = Utils.convertToTimestamp(LocalDate.parse("2025-04-26"), "07:00");
        Timestamp end = Utils.convertToTimestamp(LocalDate.parse("2025-04-26"), "23:00");
        Mockito.when(this.eventServices.checkVenueAndDateTime(venueId, start, end, 0))
                .thenReturn(null);

        List<Ticket> tickets = this.eventTabServices.getTicketsValid(
                "Test",
                LocalDate.parse("2025-04-26"),
                "07:00",
                LocalDate.parse("2025-04-26"),
                "23:00",
                "White place - 30",
                "-40",
                errorMsg);

        Assertions.assertNull(tickets);
        Assertions.assertEquals("Số lượng khách phải là số nguyên dương",
                errorMsg[0]);
    }

    @Test
    @DisplayName("Kiểm tra nhập tên sự kiện dài")
    @Tag("testNotEmptyFields")
    public void testEventNameMaxLenght() throws SQLException {
        String longName = "T".repeat(300);
        String[] errorMsg = new String[1];

        String venueName = "White Place - 50";

        // Mock dữ liệu địa điểm
        Map<String, Integer> venueMap = new HashMap<>();
        venueMap.put("White Place", 1);
        this.eventTabServices.getVenuesId().putAll(venueMap);

        Venue mockVenue = Mockito.mock(Venue.class);
        Mockito.when(mockVenue.getCapacity()).thenReturn(50);
        Mockito.when(this.venueServices.getVenueById(1)).thenReturn(mockVenue);

        Mockito.when(this.eventServices.checkVenueAndDateTime(Mockito.anyInt(), Mockito.any(), Mockito.any(), Mockito.anyInt()))
                .thenReturn(null);

        List<Ticket> tickets = this.eventTabServices.getTicketsValid(
                longName,
                LocalDate.now().plusDays(2),
                "10:00",
                LocalDate.now().plusDays(2),
                "12:00",
                venueName,
                "10",
                errorMsg
        );

        Assertions.assertNull(tickets);
    }

    @Test
    @DisplayName("Kiểm tra số lượng hoặc giá vé không phải là chữ số")
    public void testTicketQuantityOrPriceNotNumeric() throws SQLException {
        String[] errorMsg = new String[1];
        Map<Tickettype, HBox> ticketRows = new HashMap<>();

        HBox row1 = new HBox(10);
        Tickettype ticketType1 = new Tickettype(1, "Loại 1");
        row1.getChildren().addAll(new Label(String.format("Loại vé : %s", ticketType1.getName())),
                new TextField("20a"), new TextField("1000"));
        ticketRows.put(ticketType1, row1);

        this.eventTabServices = new EventTabServices(
                venueServices,
                eventServices,
                notificationServices,
                ticketServices,
                ticketRows
        );

        String venueName = "White Place - 50";
        Map<String, Integer> venueMap = new HashMap<>();
        venueMap.put("White Place", 1);
        this.eventTabServices.getVenuesId().putAll(venueMap);

        Venue mockVenue = Mockito.mock(Venue.class);
        Mockito.when(mockVenue.getCapacity()).thenReturn(50);
        Mockito.when(this.venueServices.getVenueById(1)).thenReturn(mockVenue);

        Mockito.when(this.eventServices.checkVenueAndDateTime(Mockito.anyInt(), Mockito.any(), Mockito.any(), Mockito.anyInt()))
                .thenReturn(null);

        List<Ticket> tickets = this.eventTabServices.getTicketsValid(
                "Sự kiện thử",
                LocalDate.now().plusDays(2),
                "10:00",
                LocalDate.now().plusDays(2),
                "12:00",
                venueName,
                "50",
                errorMsg
        );

        Assertions.assertNull(tickets);
        Assertions.assertEquals("Số lượng và giá vé phải là chữ số", errorMsg[0]);
    }

    @Test
    @DisplayName("Kiểm tra tổng số lượng vé lớn hơn số khách mời")
    public void testTotalTicketsExceedCapacity() throws SQLException {
        String[] errorMsg = new String[1];
        Map<Tickettype, HBox> ticketRows = new HashMap<>();

        HBox row1 = new HBox(10);
        Tickettype ticketType1 = new Tickettype(1, "Loại 1");
        Label nameLabel1 = new Label(String.format("Loại vé : %s", ticketType1.getName()));
        TextField quantityField1 = new TextField("20");
        TextField priceField1 = new TextField("1000");

        row1.getChildren().addAll(nameLabel1,
                quantityField1, priceField1);

        HBox row2 = new HBox(10);
        Tickettype ticketType2 = new Tickettype(2, "Loại 2");
        Label nameLabel2 = new Label(String.format("Loại vé : %s", ticketType1.getName()));
        TextField quantityField2 = new TextField("50");
        TextField priceField2 = new TextField("1000");

        row2.getChildren().addAll(nameLabel2,
                quantityField2, priceField2);

        ticketRows.put(ticketType1, row1);
        ticketRows.put(ticketType2, row2);

        this.eventTabServices = new EventTabServices(
                venueServices,
                eventServices,
                notificationServices,
                ticketServices,
                ticketRows
        );

        String venueName = "White Place - 50";
        Map<String, Integer> venueMap = new HashMap<>();
        venueMap.put("White Place", 1);
        this.eventTabServices.getVenuesId().putAll(venueMap);

        Venue mockVenue = Mockito.mock(Venue.class);
        Mockito.when(mockVenue.getCapacity()).thenReturn(50);
        Mockito.when(this.venueServices.getVenueById(1)).thenReturn(mockVenue);

        Mockito.when(this.eventServices.checkVenueAndDateTime(Mockito.anyInt(), Mockito.any(), Mockito.any(), Mockito.anyInt()))
                .thenReturn(null);

        List<Ticket> tickets = this.eventTabServices.getTicketsValid(
                "Sự kiện VIP",
                LocalDate.now().plusDays(2),
                "09:00",
                LocalDate.now().plusDays(2),
                "11:00",
                venueName,
                "50", // khách mời chỉ 100
                errorMsg
        );

        Assertions.assertNull(tickets);
        Assertions.assertEquals("Tổng số lượng vé khong được lớp hơn số khách mời", errorMsg[0]);
    }

    @Test
    @DisplayName("Kiểm tra tạo vé sự kiện thành công")
    public void testTicketsSucsses() throws SQLException {
        String[] errorMsg = new String[1];
        Map<Tickettype, HBox> ticketRows = new HashMap<>();

        HBox row1 = new HBox(10);
        Tickettype ticketType1 = new Tickettype(1, "Loại 1");
        Label nameLabel1 = new Label(String.format("Loại vé : %s", ticketType1.getName()));
        TextField quantityField1 = new TextField("20");
        TextField priceField1 = new TextField("1000");

        row1.getChildren().addAll(nameLabel1,
                quantityField1, priceField1);

        HBox row2 = new HBox(10);
        Tickettype ticketType2 = new Tickettype(2, "Loại 2");
        Label nameLabel2 = new Label(String.format("Loại vé : %s", ticketType1.getName()));
        TextField quantityField2 = new TextField("30");
        TextField priceField2 = new TextField("1000");

        row2.getChildren().addAll(nameLabel2,
                quantityField2, priceField2);

        ticketRows.put(ticketType1, row1);
        ticketRows.put(ticketType2, row2);

        this.eventTabServices = new EventTabServices(
                venueServices,
                eventServices,
                notificationServices,
                ticketServices,
                ticketRows
        );

        String venueName = "White Place - 50";
        Map<String, Integer> venueMap = new HashMap<>();
        venueMap.put("White Place", 1);
        this.eventTabServices.getVenuesId().putAll(venueMap);

        Venue mockVenue = Mockito.mock(Venue.class);
        Mockito.when(mockVenue.getCapacity()).thenReturn(50);
        Mockito.when(this.venueServices.getVenueById(1)).thenReturn(mockVenue);

        Mockito.when(this.eventServices.checkVenueAndDateTime(Mockito.anyInt(), Mockito.any(), Mockito.any(), Mockito.anyInt()))
                .thenReturn(null);

        List<Ticket> tickets = this.eventTabServices.getTicketsValid(
                "Sự kiện VIP",
                LocalDate.now().plusDays(2),
                "09:00",
                LocalDate.now().plusDays(2),
                "11:00",
                venueName,
                "50", // khách mời chỉ 100
                errorMsg
        );

        Assertions.assertNotNull(tickets);
    }

    @Test
    @DisplayName("kiểm tra lỗi XSS (Cross-Site Scripting) - script độc hại vào tên sự kiện")
    public void testTicketCrossSiteScripting() throws SQLException {
        String[] errorMsg = new String[1];
        Map<Tickettype, HBox> ticketRows = new HashMap<>();

        HBox row1 = new HBox(10);
        Tickettype ticketType1 = new Tickettype(1, "Loại 1");
        Label nameLabel1 = new Label(String.format("Loại vé : %s", ticketType1.getName()));
        TextField quantityField1 = new TextField("20");
        TextField priceField1 = new TextField("1000");

        row1.getChildren().addAll(nameLabel1,
                quantityField1, priceField1);

        HBox row2 = new HBox(10);
        Tickettype ticketType2 = new Tickettype(2, "Loại 2");
        Label nameLabel2 = new Label(String.format("Loại vé : %s", ticketType1.getName()));
        TextField quantityField2 = new TextField("30");
        TextField priceField2 = new TextField("1000");

        row2.getChildren().addAll(nameLabel2,
                quantityField2, priceField2);

        ticketRows.put(ticketType1, row1);
        ticketRows.put(ticketType2, row2);

        this.eventTabServices = new EventTabServices(
                venueServices,
                eventServices,
                notificationServices,
                ticketServices,
                ticketRows
        );

        String venueName = "White Place - 50";
        Map<String, Integer> venueMap = new HashMap<>();
        venueMap.put("White Place", 1);
        this.eventTabServices.getVenuesId().putAll(venueMap);

        Venue mockVenue = Mockito.mock(Venue.class);
        Mockito.when(mockVenue.getCapacity()).thenReturn(50);
        Mockito.when(this.venueServices.getVenueById(1)).thenReturn(mockVenue);

        Mockito.when(this.eventServices.checkVenueAndDateTime(Mockito.anyInt(), Mockito.any(), Mockito.any(), Mockito.anyInt()))
                .thenReturn(null);

        List<Ticket> tickets = this.eventTabServices.getTicketsValid(
                "<script>alert('XSS')</script>",
                LocalDate.now().plusDays(2),
                "09:00",
                LocalDate.now().plusDays(2),
                "11:00",
                venueName,
                "50", // khách mời chỉ 100
                errorMsg
        );

        Assertions.assertNull(tickets);
    }

    @Test
    @DisplayName("Kiểm tra tìm kiếm với từ khóa không tồn tại")
    public void testSearchWithNonExistingKeyword() throws SQLException {
        EventServices e = new EventServices();
        List<Event> events = e.getEvents(0, "abcd");

        Assertions.assertNotNull(events);
        Assertions.assertTrue(events.isEmpty(), "Danh sách phải rỗng nếu không tìm thấy");
    }

    @Test
    @DisplayName("Tìm kiếm với từ khóa đúng tên sự kiện")
    public void testSearchWithExactEventName() throws SQLException {
        EventServices e = new EventServices();
        List<Event> events = e.getEvents(0, "testCreateEventEqualEventExist");

        Assertions.assertNotNull(events);
        Assertions.assertEquals("testCreateEventEqualEventExist", events.get(0).getName());
    }

    @Test
    @DisplayName("Kiểm tra tìm kiếm với chữa cái hoa thường")
    public void testSearchWithUppercaseOrLowercase() throws SQLException {
        EventServices e = new EventServices();
        List<Event> events = e.getEvents(0, "Tes");

        Assertions.assertNotNull(events);
        Assertions.assertTrue(events.get(0).getName().contains("Tes"));
    }
    
    // lưu ý ch ổn
    @Test
    @DisplayName("Kiểm tra số lần truy vấn CSDL khi tìm kiếm với debounce")
    public void testSearchDatabaseCallCountWithDebounce() throws SQLException, InterruptedException {
        // Tạo danh sách sự kiện giả
        List<Event> events = new ArrayList<>();
        Event e1 = new Event();
        Event e2 = new Event();

        e1.setName("Test1");
        e2.setName("Test2");
        events.add(e1);
        events.add(e2);

        // Mô phỏng việc trả về danh sách sự kiện khi gọi API với từ khóa "tes"
        Mockito.when(this.eventServices.getEvents(Mockito.eq(0), Mockito.eq("tes"))).thenReturn(events);

        // Tạo ArgumentCaptor để theo dõi đối số truyền vào
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);

        // Giả lập người dùng nhập từ khóa tìm kiếm
        eventServices.getEvents(0, "t");
        eventServices.getEvents(0, "te");
        eventServices.getEvents(0, "tes");

        // Mô phỏng việc người dùng dừng nhập sau khi nhập "tes"
        Thread.sleep(500);  // Giả lập thời gian debounce

        // Xác minh chỉ có một lần gọi và đối số là "tes"
        Mockito.verify(this.eventServices, Mockito.times(1)).getEvents(Mockito.eq(0), captor.capture());
        Assertions.assertEquals("tes", captor.getValue());  // Kiểm tra đối số cuối cùng là "tes"

        // Kiểm tra kết quả trả về
        List<Event> result = eventServices.getEvents(0, "tes");
        Assertions.assertNotNull(result);
        Assertions.assertEquals(2, result.size());
        Assertions.assertTrue(result.get(0).getName().contains("tes"));
    }
}
