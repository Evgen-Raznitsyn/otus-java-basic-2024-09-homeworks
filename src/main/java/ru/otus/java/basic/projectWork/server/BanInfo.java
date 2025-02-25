package ru.otus.java.basic.projectWork.server;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class BanInfo {
    private String username;
    private LocalDateTime banStart;
    private LocalDateTime banEnd;
    private String reason;
    private static final DateTimeFormatter datetimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");

    public BanInfo(String username, LocalDateTime banStart, LocalDateTime banEnd, String reason) {
        this.username = username;
        this.banStart = banStart;
        this.banEnd = banEnd;
        this.reason = reason;
    }

    public String getUsername() {
        return username;
    }

    public LocalDateTime getBanStart() {
        return banStart;
    }

    public LocalDateTime getBanEnd() {
        return banEnd;
    }

    public String getReason() {
        return reason;
    }

    public boolean isPermanent() {
        return banEnd == null;
    }

    public boolean isActive() {
        if (isPermanent()) {
            return true;
        }
        return LocalDateTime.now().isAfter(banStart) && LocalDateTime.now().isBefore(banEnd);
    }
    public String getBanMessage() {
        if (isPermanent()) {
            return "Вы забанены. Причина: " + reason;
        } else {
            return "Вы забанены до " + banEnd.format(datetimeFormatter) + ". Причина: " + reason;
        }
    }
}