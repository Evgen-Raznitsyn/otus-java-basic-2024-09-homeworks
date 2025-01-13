package ru.otus.java.basic.homeworks.hw13;

import java.io.*;
import java.net.*;

public class CalculatorServer {
    private static final int PORT = 8080;

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Сервер запущен на порту " + PORT);
            while (true) {
                Socket clientSocket = serverSocket.accept();
                new Thread(new ClientHandler(clientSocket)).start();
                System.out.println("Клиент подключен: " + clientSocket.getPort());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String processRequest(String request) {
        String[] parts = request.split(" ");
        double result;

        if (parts.length == 2 && parts[0].equalsIgnoreCase("sqrt")) {
            double num = Double.parseDouble(parts[1]);
            result = Math.sqrt(num);
            return String.valueOf(result);
        }

        if (parts.length != 3) {
            return "Ошибка: Неверный формат. Используйте: число1 оператор число2 или sqrt число";
        }

        double num1 = Double.parseDouble(parts[0]);
        String operator = parts[1];
        double num2 = Double.parseDouble(parts[2]);

        switch (operator) {
            case "+":
                result = num1 + num2;
                break;
            case "-":
                result = num1 - num2;
                break;
            case "*":
                result = num1 * num2;
                break;
            case "/":
                if (num2 == 0) return "Ошибка: Деление на ноль";
                result = num1 / num2;
                break;
            case "^":
                result = Math.pow(num1, num2);
                break;
            default:
                return "Ошибка: Неверный оператор";
        }
        return String.valueOf(result);
    }
}
