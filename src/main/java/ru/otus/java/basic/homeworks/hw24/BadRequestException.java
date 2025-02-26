package ru.otus.java.basic.homeworks.hw24;

public class BadRequestException extends RuntimeException {
    private String code;
    private String description;

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public BadRequestException(String code, String description) {
        this.code = code;
        this.description = description;
    }
}
