package com.ntn.pojo;

import java.time.LocalDateTime;

public class Event {
    private String name;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    public Event(String name, LocalDateTime startTime, LocalDateTime endTime) {
        this.name = name;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    // Getter và Setter nếu cần
}
