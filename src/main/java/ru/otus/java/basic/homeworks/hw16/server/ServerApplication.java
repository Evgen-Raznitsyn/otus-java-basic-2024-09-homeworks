package ru.otus.java.basic.homeworks.hw16.server;

public class ServerApplication {
    public static void main(String[] args) {
        new Server(8189).start();
    }
}