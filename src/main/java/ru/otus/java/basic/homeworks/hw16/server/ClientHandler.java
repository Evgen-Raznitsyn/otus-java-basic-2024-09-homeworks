package ru.otus.java.basic.homeworks.hw16.server;

import java.io.*;
import java.net.Socket;
import java.util.List;
import java.util.Objects;

public class ClientHandler {
    private Socket socket;
    private Server server;
    private DataInputStream in;
    private DataOutputStream out;
    private String username;
    private String login;
    private Roles role;


    public ClientHandler(Socket socket, Server server) throws IOException {
        this.socket = socket;
        this.server = server;
        this.in = new DataInputStream(socket.getInputStream());
        this.out = new DataOutputStream(socket.getOutputStream());

        new Thread(() -> {
            try {
                System.out.println("Клиент подключился на порту: " + socket.getPort());
                authenticate();
                sendActiveUsers();
                handleMessages();
            } catch (IOException e) {
                System.out.println("Клиент отключился: " + socket.getPort());
            } finally {
                disconnect();
                server.unsubscribe(this);
            }
        }).start();
    }

    private void authenticate() throws IOException {
        while (true) {
            sendMsg("Для начала работы надо пройти аутентификацию. Формат команды /auth <логин> <пароль> \n" +
                    "или регистрацию. Формат команды /reg <логин> <пароль> <имя>");

            String message = in.readUTF();
            if (message.startsWith("/")) {
                if (message.equalsIgnoreCase("/exit")) {
                    sendMsg("/exitok");
                    break;
                }
                if (message.startsWith("/auth ")) {
                    String[] elements = message.split(" ");
                    if (elements.length != 3) {
                        sendMsg("Неверный формат команды /auth. Используйте: /auth <логин> <пароль>");
                        continue;
                    }
                    if (server.getAuthenticatedProvider()
                            .authenticate(this, elements[1], elements[2])) {
                        break;
                    }
                }
                if (message.startsWith("/reg ")) {
                    String[] elements = message.split(" ");
                    if (elements.length != 4) {
                        sendMsg("Неверный формат команды /reg. Используйте: /reg <логин> <пароль> <имя>");
                        continue;
                    }
                    if (server.getAuthenticatedProvider()
                            .registration(this, elements[1], elements[2], elements[3], Roles.USER)) {
                        break;
                    }
                }
            }
        }
    }

    private void handleMessages() throws IOException {
        while (true) {
            String message = in.readUTF();
            if (message.startsWith("/kick ")) {
                if (this.role.equals(Roles.ADMIN)) {
                    String[] element = message.split(" ");
                    if (element.length !=2|| element[1].trim().isEmpty()) {
                        sendMsg("Неверный формат команды /kick. Используйте: /kick <имя>");
                        continue;
                    }
                    server.kickUser(element[1]);
                    sendMsg("Пользователь " + element[1] + " был отключен от чата администратором.");
                } else {
                    sendMsg("У вас нет прав для выполнения этой команды.");
                }
            } else if (message.startsWith("/w ")) {
                sendPrivateMessage(message);
            } else if (message.equalsIgnoreCase("/users")) {
                sendActiveUsers();
            } else if (message.equalsIgnoreCase("/help")) {
                sendHelpMessage();
            } else if (message.startsWith("/")) {
                if (message.equalsIgnoreCase("/logout")) {
                    sendMsg("/logoutok");
                    server.logout(this);
                    authenticate();
                }
                if (message.equalsIgnoreCase("/exit")) {
                    sendMsg("/exitok");
                    System.out.println("Клиент отключился: " + socket.getPort());
                    break;
                }
            } else {
                server.broadcastMessage(username + ": " + message);
            }
        }
    }

    private void sendPrivateMessage(String message) {
        String[] tokens = message.split(" ", 3);
        if (tokens.length < 3) {
            sendMsg("Неверный формат команды /w. Используйте: /w <имя> <сообщение>");
            return;
        }
        String targetUsername = tokens[1];
        String privateMessage = tokens[2];
        ClientHandler targetClient = server.findClientByUsername(targetUsername);
        if (targetClient != null) {
            targetClient.sendMsg("Личное от " + username + ": " + privateMessage);
            sendMsg("Личное для " + targetUsername + ": " + privateMessage);
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
            System.err.println("Ошибка при отключении соединения: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setLogin(String login) {
        this.login = login;
    }

     public void setRole(Roles role) {
        this.role = role;
    }

    private void sendHelpMessage() {
        String helpMessage = "Доступные команды:\n" +
                "/users - показать текущих пользователей в чате\n" +
                "/w <username> <message> - отправить личное сообщение пользователю\n" +
                "/exit - выйти из чата\n" +
                "/logout - выйти из системы\n" +
                "/help - показать это сообщение";
        if (this.role.equals(Roles.ADMIN)) {
            helpMessage += ("\n/kick <username> - Исключить указанного пользователя из чата");
        }
        sendMsg(helpMessage);
    }
}