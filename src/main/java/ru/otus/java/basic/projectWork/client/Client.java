package ru.otus.java.basic.projectWork.client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    private Socket socket;
    private DataOutputStream out;
    private DataInputStream in;
    private Scanner scanner;

    public Client() throws IOException {
        String host = "localhost";
        int port = 8189;
        scanner = new Scanner(System.in);
        try {
            socket = new Socket(host, port);
            out = new DataOutputStream(socket.getOutputStream());
            in = new DataInputStream(socket.getInputStream());
        } catch (ConnectException e) {
            System.out.println("Сервер не запущен");
            return;
        }

        new Thread(() -> {
            try {
                while (true) {
                    String message = in.readUTF();
                    processServerMessage(message);
                }
            } catch (IOException e) {
                System.out.printf("Не удалось установить соединение с сервером.\n" +
                        "Пожалуйста, проверьте, запущен ли сервер и доступен ли он по адресу %s:%s.\n", host, port);
            } finally {
                disconnect();
            }
        }).start();
        while (true) {
            String message = scanner.nextLine();
            if (message.trim().isEmpty()) {
                System.out.println("Сообщение не может быть пустым.");
                continue;
            }
            if (socket.isClosed()) {
                System.out.println("Вы были отключены от сервера.");
                break;
            }
            try {
                out.writeUTF(message);
            } catch (IOException e) {
                System.err.println("Ошибка при отправке сообщения: " + e.getMessage());
                break;
            }
        }
    }

    private void processServerMessage(String message) throws IOException {
        if (message.startsWith("/")) {
            switch (message) {
                case "/exitok":
                    System.out.println("Вы вышли из чата.");
                    disconnect();
                    System.exit(0);
                    break;
                case "/logoutok":
                    System.out.println("Вы вышли из системы. Пожалуйста, введите свои учетные данные для повторной аутентификации.\n");
                    break;
                case "/kickoff":
                    System.out.println("Вас отключил администратор.");
                    out.writeUTF("/exit");
                    break;
                case "/banned":
                    System.out.println("Вы были отключены от сервера.");
                    disconnect();
                    System.exit(0);
                default:
                    if (message.startsWith("/authok ")) {
                        System.out.printf("Добро пожаловать в чат, %s!\n",
                                message.split(" ")[1]);
                    } else if (message.startsWith("/regok ")) {
                        System.out.printf("Добро пожаловать, %s!\n",
                                message.split(" ")[1]);
                    }
                    break;
            }
        } else {
            System.out.println(message);
        }
    }

    public void disconnect() {
        try {
            if (in != null) in.close();
            if (out != null) out.close();
            if (socket != null) socket.close();
        } catch (IOException e) {
            System.err.println("Ошибка при отключении соединения: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
