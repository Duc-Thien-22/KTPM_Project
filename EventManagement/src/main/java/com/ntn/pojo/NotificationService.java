package com.ntn.pojo;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import javafx.application.Platform;
import javafx.scene.control.Alert;

public class NotificationService {

    private List<Event> events; // Danh sách các sự kiện
    private Timer timer;

    public NotificationService(List<Event> events) {
    this.events = events;
    this.timer = new Timer(true);
}


    // Bắt đầu kiểm tra và gửi thông báo
    public void start() {
        if (events == null || events.isEmpty()) {
            System.out.println("Không có sự kiện nào để nhắc nhở.");
            return;
        }

        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                checkEvents();
            }
        }, 0, 60000); // Kiểm tra mỗi phút
    }

    // Kiểm tra sự kiện sắp diễn ra
    private void checkEvents() {
        LocalDateTime now = LocalDateTime.now();
        for (Event event : events) {
            if (event.getStartTime() == null) {
                continue; // Tránh lỗi NullPointerException
            }
            if (event.getStartTime().minusMinutes(10).isBefore(now) && event.getStartTime().isAfter(now)) {
                showNotification(event);
            }
        }
    }

    // Hiển thị thông báo bằng JavaFX
    private void showNotification(Event event) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Nhắc nhở sự kiện");
            alert.setHeaderText(null);
            alert.setContentText("Sự kiện '" + event.getTitle() + "' sẽ diễn ra trong 10 phút nữa.");
            alert.showAndWait();
        });
    }

    // Phương thức dừng Timer
    public void stop() {
        if (timer != null) {
            timer.cancel();
            timer.purge();
            System.out.println("Đã dừng NotificationService.");
        }
    }

    public class Event {

        private Integer id;
        private String title;
        private LocalDateTime startTime;

        public Event() {
        }

        public Event(Integer id, String title, LocalDateTime startTime) {
            this.id = id;
            this.title = title;
            this.startTime = startTime;
        }

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public LocalDateTime getStartTime() {
            return startTime;
        }

        public void setStartTime(LocalDateTime startTime) {
            this.startTime = startTime;
        }
    }
}
