# KTPM_Project – Event Management System

**Công nghệ**: Java, JavaFX, Maven, MySQL, JUnit 5, Mockito 

## Giới thiệu
Dự án **Quản lý sự kiện** được xây dựng nhằm hỗ trợ quản trị và người dùng trong việc:
- Đăng ký sự kiện, quản lý vé tham gia
- Quản trị viên quản lý sự kiện, lịch, địa điểm
- Hệ thống thông báo trước khi sự kiện diễn ra
- Lưu trữ và hiển thị lịch sử đăng ký, thông báo

##  Tính năng chính
###  Người dùng
- Đăng nhập / Đăng ký tài khoản
- Tìm kiếm & đăng ký tham gia sự kiện
- Xem lịch sử sự kiện đã đăng ký
- Nhận thông báo khi sự kiện sắp diễn ra

###  Quản trị viên
- Quản lý danh sách sự kiện (CRUD)
- Quản lý địa điểm, số lượng người tham gia
- Theo dõi báo cáo đăng ký

###  Hệ thống thông báo
- Tự động gửi thông báo trước khi sự kiện bắt đầu
- Lưu lại lịch sử thông báo

##  Kiến trúc dự án
- **JavaFX (UI)**: giao diện người dùng (FXML + Controller)
- **Service layer**: `EventServices`, `NotificationServices`, `UserServices`… xử lý logic
- **DAO/JDBC**: kết nối MySQL qua `JdbcUtils`
- **DTO/POJO**: ánh xạ dữ liệu (EventDTO, User, Notification…)
- **JUnit + Mockito**: viết test cho service & validate input
