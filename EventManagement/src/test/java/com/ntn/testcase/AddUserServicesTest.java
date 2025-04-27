///*
// * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
// * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
// */
//package com.ntn.testcase;
//
//import com.ntn.pojo.User;
//import com.ntn.services.AddUserServices;
//import org.junit.jupiter.api.Assertions;
//import com.ntn.services.UserServices;
//import org.junit.jupiter.api.*;
//import org.junit.jupiter.api.extension.*;
//import org.mockito.junit.jupiter.*;
//import org.mockito.*;
//import org.junit.jupiter.params.ParameterizedTest;
//import org.junit.jupiter.params.provider.CsvSource;
//
///**
// *
// * @author NHAT
// */
//@ExtendWith(MockitoExtension.class)
//public class AddUserServicesTest {
//
//    @Mock
//    private UserServices userServices;
//
//    @InjectMocks
//    private AddUserServices addUserServices;
//
//    @Test
//    @DisplayName("Kiểm tra các trường dữ liệu trống")
//    public void testDataNull() throws Exception {
//        User u = new User(
//                "",
//                "",
//                "",
//                "",
//                "",
//                "",
//                "ROLE_USER"
//        );
//        String result = this.addUserServices.addUser(u);
//        Assertions.assertEquals("Phải nhập đầu đủ dữ liệu", result);
//    }
//
//    @ParameterizedTest
//    @CsvSource({
//        "'', 'abc', 'abc@gmail.com', '123', 'HCM', '0123456789'",
//        "'abc', '', 'abc@gmail.com', '123', 'HCM', '0123456789'",
//        "'abc', 'abc', '', '123', 'HCM', '0123456789'",
//        "'abc', 'abc', 'abc@gmail.com', '', 'HCM', '0123456789'",
//        "'abc', 'abc', 'abc@gmail.com', '123', '', '0123456789'",
//        "'abc', 'abc', 'abc@gmail.com', '123', 'HCM', ''"
//    })
//    @DisplayName("Test các trường rỗng")
//    public void testEmptyFields(
//            String username,
//            String fullname,
//            String email,
//            String password,
//            String address,
//            String phone
//    ) throws Exception {
//        User u = new User(username, fullname, email, password, address, phone, "ROLE_USER");
//        String result = addUserServices.addUser(u);
//        Assertions.assertEquals("Phải nhập đầu đủ dữ liệu", result);
//    }
//
//    @ParameterizedTest
//    @CsvSource({
//        "admin1",
//        "Admin",
//        "admin123@",
//        "Admin@123456789012345678901234567890123",
//        "123Admin@",
//        "Admin 123@",
//        "!Admin123",})
//    @DisplayName("Kiểm tra các trường hợp password sai định dạng")
//    public void testInvalidPasswordsOnly(String password) throws Exception {
//        User u = new User(
//                "abc",
//                password,
//                "a",
//                "c",
//                "c@example.com",
//                "0981234567",
//                "ROLE_USER");
//        String result = addUserServices.addUser(u);
//
//        Assertions.assertEquals(
//                "Password ít nhất là 6 kí tự, bắt đầu bằng 1 kí tự In hoa, thường, chữ số và kí tự đặc biệt",
//                result);
//    }
//
//    @ParameterizedTest
//    @CsvSource({
//        "abcexample.com",
//        "abc@.com",
//        "abc@com",
//        "abc@com.",
//        "abc@.example.com",
//        "abc@exa_mple.com",
//        "abc@ex@ample.com",
//        "abc@ex_ample.com",
//        "@example.com",
//        "abc@domain..com"
//    })
//    @DisplayName("Kiểm tra các trường hợp email sai định dạng")
//    public void testInvalidEmails(String email) throws Exception {
//        User u = new User(
//                "abc",
//                "ValidPassword123!",
//                "ab",
//                "c",
//                email,
//                "0981234567",
//                "ROLE_USER");
//        String result = addUserServices.addUser(u);
//
//        Assertions.assertEquals(
//                "Sai định dạng email.. EX: example@gmail.com",
//                result);
//    }
//
//    @ParameterizedTest
//    @CsvSource({
//        "123456789",
//        "+12345",
//        "1234567890123456",
//        "+1234567890123456",
//        "12345a67890",
//        "12345@67890",
//        "abcdef12345",
//        "12345 67890",
//        "123-456-7890",
//        "+123 456 7890"
//    })
//    @DisplayName("Kiểm tra các trường hợp số điện thoại sai định dạng")
//    public void testInvalidPhoneNumbers(String phone) throws Exception {
//        User u = new User(
//                "abc",
//                "ValidPassword123!",
//                "ab",
//                "c",
//                "abc@example.com",
//                phone,
//                "ROLE_USER");
//        String result = addUserServices.addUser(u);
//
//        Assertions.assertEquals(
//                "Sai định dạng sdt ... EX: +8401010, 098.... gồm 9-15 kí tự",
//                result);
//    }
//
//    @Test
//    @DisplayName("Kiểm tra lỗi đăng kí một user đã tồn tại trong hệ thống")
//    public void testRegisterUserExist() throws Exception {
//        User u = new User(
//                "abc11",
//                "Admin123@",
//                "ab",
//                "c",
//                "abc@gmail.com",
//                "0123456789",
//                "ROLE_USER"
//        );
//
//        Mockito.when(this.userServices.getUserByUsername(u.getUsername(), u.getEmail())).thenReturn(u);
//
//        String result = this.addUserServices.addUser(u);
//        Assertions.assertEquals("Username hoặc email đã tồn tại", result);
//    }
//
//    @Test
//    @DisplayName("Kiểm tra đăng kí thành công")
//    public void testRegisterUserSuccess() throws Exception {
//        // Tạo một người dùng hợp lệ
//        User u = new User(
//                "abc",
//                "Admin123@1",
//                "ab",
//                "c",
//                "abc@example.com",
//                "0981234567",
//                "ROLE_USER"
//        );
//
//        Mockito.when(this.userServices.getUserByUsername(u.getUsername(), u.getEmail())).thenReturn(null);
//
//        Mockito.when(this.userServices.addUser(u)).thenReturn(1);
//
//        String result = this.addUserServices.addUser(u);
//
//        Assertions.assertEquals("SUCCESS", result);
//    }
//
//    @Test
//    @DisplayName("Kiểm tra hệ thống KHÔNG bị SQL Injection từ username")
//    public void testSQLInjectionFromUsername() throws Exception {
//        User u = new User(
//                "test'); DROP TABLE users; --",
//                "Admin123@1",
//                "John",
//                "Doe",
//                "injection_test@example.com",
//                "0981234567",
//                "ROLE_USER"
//        );
//
//        Mockito.when(this.userServices.getUserByUsername(u.getUsername(), u.getEmail())).thenReturn(null);
//        Mockito.when(this.userServices.addUser(u)).thenReturn(1);
//
//        String result = this.addUserServices.addUser(u);
//
//        Assertions.assertEquals("Đăng kí thất bại", result);
//    }
//
//    @Test
//    @DisplayName("Test đăng ký 10 tài khoản từ cùng một IP (giả lập)")
//    public void testMultipleRegistrationsSameIP() throws Exception {
//        // Giả lập đăng ký nhiều tài khoản từ cùng 1 IP
//        for (int i = 0; i < 10; i++) {
//            String username = "user" + i;
//            String email = "user" + i + "@example.com";
//
//            User u = new User(
//                    username,
//                    "Admin123@",
//                    "John",
//                    "Doe",
//                    email,
//                    "0981234567",
//                    "ROLE_USER"
//            );
//
//            Mockito.when(this.userServices.addUser(Mockito.any(User.class))).thenReturn(1);
//
//            String result = this.addUserServices.addUser(u);
//
//            if (i >= 5) {
//                Assertions.assertEquals("Bạn đã đăng ký quá số lần cho phép từ IP này", result);
//            }
//        }
//    }
//
//    @Test
//    @DisplayName("Test đăng ký với username quá dài")
//    public void testLongUsername() throws Exception {
//        String longUsername = "a".repeat(300);
//        User u = new User(
//                longUsername,
//                "Admin123@1",
//                "Test",
//                "User",
//                "longuser@example.com",
//                "0987654321",
//                "ROLE_USER"
//        );
//
//        Mockito.when(this.userServices.getUserByUsername(u.getUsername(), u.getEmail())).thenReturn(null);
//        Mockito.when(this.userServices.addUser(u)).thenReturn(1);
//
//        String result = this.addUserServices.addUser(u);
//        Assertions.assertEquals("Username quá dài", result);
//    }
//
//    @Test
//    @DisplayName("Test lỗi hệ thống khi thêm user thất bại")
//    public void testAddUserFailureFromDB() throws Exception {
//        User u = new User("abc", "Admin123@", "A", "B", "abc@example.com", "0981234567", "ROLE_USER");
//
//        Mockito.when(this.userServices.getUserByUsername(u.getUsername(), u.getEmail())).thenReturn(null);
//        Mockito.when(this.userServices.addUser(u)).thenReturn(0);
//
//        String result = this.addUserServices.addUser(u);
//        Assertions.assertEquals("Đăng kí thất bại", result);
//    }
//
//    @Test
//    @DisplayName("Test đăng ký với tên chứa dấu nháy đơn")
//    public void testApostropheInUsername() throws Exception {
//        User u = new User(
//                "o'reilly",
//                "Admin123@1",
//                "O'",
//                "Reilly",
//                "oreilly@example.com",
//                "0981234567",
//                "ROLE_USER"
//        );
//
//        Mockito.when(this.userServices.getUserByUsername(u.getUsername(), u.getEmail())).thenReturn(null);
//        Mockito.when(this.userServices.addUser(u)).thenReturn(1);
//
//        String result = this.addUserServices.addUser(u);
//        Assertions.assertEquals("Tên đăng kí không được chứa nháy đơn", result);
//    }
//
//}
