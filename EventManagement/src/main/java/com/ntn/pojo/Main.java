import com.ntn.pojo.NotificationService;
import com.ntn.pojo.Event;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) {
        // Tạo danh sách các sự kiện mẫu
        Event event1 = new Event("Họp nhóm", LocalDateTime.now().plusMinutes(15), LocalDateTime.now().plusHours(1));
        Event event2 = new Event("Thuyết trình", LocalDateTime.now().plusMinutes(30), LocalDateTime.now().plusHours(2));
        List<Event> events = Arrays.asList(event1, event2);

        // Khởi tạo dịch vụ thông báo
        NotificationService notificationService = new NotificationService(events);

        // Khởi tạo giao diện JavaFX
        primaryStage.setTitle("Quản lý sự kiện");
        primaryStage.show();

        // Bắt đầu NotificationService sau khi giao diện đã khởi tạo
        Platform.runLater(() -> notificationService.start());

        // Dừng dịch vụ khi ứng dụng đóng
        primaryStage.setOnCloseRequest(event -> notificationService.stop());
    }

    public static void main(String[] args) {
        launch(args);
    }
}
