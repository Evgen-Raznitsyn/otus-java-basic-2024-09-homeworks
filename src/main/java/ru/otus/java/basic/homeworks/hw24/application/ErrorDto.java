package ru.otus.java.basic.homeworks.hw24.application;

import java.time.LocalDateTime;

public class ErrorDto {
    private String code;
    private String description;
    private LocalDateTime timestamp;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public ErrorDto(String code, String description) {
        this.code = code;
        this.description = description;
        this.timestamp = LocalDateTime.now();
    }
}
