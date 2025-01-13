package ru.otus.java.basic.homeworks.hw13;

import java.io.*;
import java.net.*;
import java.util.*;

public class CalculatorClient {
    private static final String SERVER_ADDRESS = "localhost";
    private static final int PORT = 8080;

    public static void main(String[] args) {
        try (Socket socket = new Socket(SERVER_ADDRESS, PORT);
             DataInputStream inputStream = new DataInputStream(socket.getInputStream());
             DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
             Scanner scanner = new Scanner(System.in)) {

            System.out.println("Подключено к серверу: " + SERVER_ADDRESS + ":" + PORT);
            String operations = inputStream.readUTF();
            System.out.println(operations);

            while (true) {
                System.out.println("Введите выражение (число1 оператор число2) или (sqrt число) для извлечения корня, 'exit' для выхода:");
                String userMessage = scanner.nextLine();

                if (userMessage.equalsIgnoreCase("exit")) {
                    break;
                }

                outputStream.writeUTF(userMessage);
                outputStream.flush();

                String result = inputStream.readUTF();
                System.out.println("Результат: " + result);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}