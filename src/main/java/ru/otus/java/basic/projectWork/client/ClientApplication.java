package ru.otus.java.basic.projectWork.client;

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