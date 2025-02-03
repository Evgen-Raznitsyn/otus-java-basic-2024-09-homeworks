package ru.otus.java.basic.homeworks.hw18.client;

import java.io.IOException;

public class ClientApplication {
    public static void main(String[] args) {
        try {
            new Client();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}