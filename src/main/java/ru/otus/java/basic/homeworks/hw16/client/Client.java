package ru.otus.java.basic.homeworks.hw16.client;

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
                    if (message.startsWith("/")) {
                        if (message.equalsIgnoreCase("/exitok")) {
                            break;
                        }
                        if (message.equalsIgnoreCase("/logoutok")) {
                            System.out.println("Вы вышли из системы. Пожалуйста, введите свои учетные данные для повторной аутентификации.\n");
                            continue;
                        }
                        if (message.equalsIgnoreCase("/kickoff")) {
                            out.writeUTF("/exit");
                        }
                        if (message.startsWith("/authok ")) {
                            System.out.printf("Удалось успешно войти в чат с именем пользователя %s!\nЧтобы ознакомиться со всеми командами, введите команду /help.\n",
                                    message.split(" ")[1]);
                        }
                        if (message.startsWith("/regok ")) {
                            System.out.printf("Удалось успешно зарегистрироваться c именем пользователя %s!\nЧтобы ознакомиться со всеми командами, введите команду /help.\n",
                                    message.split(" ")[1]);
                        }
                    } else {
                        System.out.println(message);
                    }
                }
            } catch (IOException e) {
                System.out.printf("Не удалось установить соединение с сервером.\n" +
                        "Пожалуйста, проверьте, запущен ли сервер и доступен ли он по адресу %s:%s.", host, port);
            } finally {
                disconnect();
            }
        }).start();

        while (true) {
            String message = scanner.nextLine();
            if (socket.isClosed()) {
                System.out.println("Вы были отключены от чата администратором.");
                break;
            }
            out.writeUTF(message);
            if (message.equalsIgnoreCase("/exit")) {
                break;
            }
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
