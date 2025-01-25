package ru.otus.java.basic.homeworks.hw15.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.List;


public class ClientHandler {
    private Socket socket;
    private Server server;
    private DataInputStream in;
    private DataOutputStream out;
    private String username;

    public ClientHandler(Socket socket, Server server) throws IOException {
        this.socket = socket;
        this.server = server;
        this.in = new DataInputStream(socket.getInputStream());
        this.out = new DataOutputStream(socket.getOutputStream());
        new Thread(() -> {
            try {
                System.out.println("Клиент подключился " + socket.getPort());
                requestUsername();
                handleMessages();
            } catch (IOException e) {
                //    e.printStackTrace();
                System.out.println("Ошибка: " + e.getMessage());
            } finally {
                disconnect();
                server.unsubscribe(this);
            }
        }).start();
    }

    private void requestUsername() throws IOException {
        while (true) {
            sendMsg("Введите имя пользователя:");
            String requestedUsername = in.readUTF();
            if (server.isUsernameUnique(requestedUsername)) {
                username = requestedUsername;
                sendMsg("Добро пожаловать, " + username + "!\n/help, чтобы ознакомиться с командами");
                server.subscribe(this); // Подписываем клиента только если уникальное имя
                sendActiveUsers();
                break;
            } else {
                sendMsg("Имя пользователя уже занято. Пожалуйста, выберите другое.");
            }
        }

    }

    private void handleMessages() {
        try {
            while (true) {
                String message = in.readUTF();
                if (message.startsWith("/w ")) {
                    sendPrivateMessage(message);
                } else if (message.equalsIgnoreCase("/users")) {
                    sendActiveUsers();
                } else if (message.equalsIgnoreCase("/help")) {
                    sendHelpMessage();
                } else if (message.startsWith("/")) {
                    if (message.equalsIgnoreCase("/exit")) {
                        sendMsg("/exitok");
                        System.out.println("Клиент отключился: " + username);
                        break;
                    }
                } else {
                    server.broadcastMessage(username + " : " + message, this);
                }
            }
        } catch (IOException e) {
            System.out.println("Клиент отключился: " + username);
        } finally {
            disconnect();
        }
    }

    private void sendPrivateMessage(String message) {
        String[] tokens = message.split(" ", 3);
        if (tokens.length < 3) {
            sendMsg("Используйте: /w <имя> <сообщение>");
            return;
        }

        String targetUsername = tokens[1];
        String privateMessage = tokens[2];

        ClientHandler targetClient = server.findClientByUsername(targetUsername);
        if (targetClient != null) {
            targetClient.sendMsg("Privat от " + username + ": " + privateMessage);
        } else {
            sendMsg("Пользователь " + targetUsername + " не найден.");
        }
    }

    public void sendMsg(String message) {
        try {
            out.writeUTF(message);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void sendActiveUsers() {
        List<String> activeUsernames = server.getActiveUsernames();
        sendMsg("Текущие пользователи в чате: " + String.join(", ", activeUsernames));
    }

    public void disconnect() {
        try {
            if (in != null) in.close();
            if (out != null) out.close();
            if (socket != null) socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getUsername() {
        return username;
    }

    private void sendHelpMessage() {
        String helpMessage = "Доступные команды:\n" +
                "/users - показать текущих пользователей в чате\n" +
                "/w <username> <message> - отправить личное сообщение пользователю\n" +
                "/exit - выйти из чата\n" +
                "/help - показать это сообщение";
        sendMsg(helpMessage);
    }
}
