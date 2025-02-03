package ru.otus.java.basic.homeworks.hw18.server;

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
    private Roles role;
    public boolean isAuthenticated = false;

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
                if (isAuthenticated) {
                    server.unsubscribe(this);
                }
            }
        }).start();
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setRole(Roles role) {
        this.role = role;
    }

    public Roles getRole() {
        return role;
    }

    private void authenticate() throws IOException {
        while (!isAuthenticated) {
            if (server == null) {
                sendMsg("Сервер не инициализирован.");
                return;
            }
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
                    if (elements.length < 3 || elements.length > 4) {
                        sendMsg("Неверный формат команды /auth. Используйте: /auth <логин> <пароль>");
                        continue;
                    }
                    if (server.getAuthenticatedProvider().authenticate(this, elements[1], elements[2])) {
                        isAuthenticated = true;
                        sendMsg("Вы успешно аутентифицированы. Ваша текущая роль: " + role + ".\n" +
                                "Для получения списка доступных команд, пожалуйста, введите /help.");
                        server.subscribe(this);
                        break;
                    }
                }
                if (message.startsWith("/reg ")) {
                    String[] elements = message.split(" ");
                    if (elements.length != 4) {
                        sendMsg("Неверный формат команды /reg. Используйте: /reg <логин> <пароль> <имя>");
                        continue;
                    }
                    if (server.getAuthenticatedProvider().registration(this, elements[1], elements[2], elements[3])) {
                        sendMsg("Вы успешно завершили процесс регистрации!\nТеперь вы можете войти в систему, используя ваше имя пользователя.");
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
                    if (element.length != 2 || element[1].trim().isEmpty()) {
                        sendMsg("Неверный формат команды /kick. Используйте: /kick <имя>");
                        continue;
                    }
                    String usernameToKick = element[1];
                    if (usernameToKick.equals(this.username)) {
                        sendMsg("Вы не можете отключить самого себя!");
                        continue;
                    }
                    String kickedUser = server.kickUser(usernameToKick);
                    if (kickedUser != null) {
                        sendMsg("Пользователь '" + kickedUser + "' был отключен от чата администратором.");
                    } else {
                        sendMsg("Пользователь '" + usernameToKick + "' не найден.");
                    }
                } else {
                    sendMsg("У вас нет прав для выполнения этой команды.");
                }
            } else if (message.startsWith("/add_admin ")) {
                if (this.role == Roles.ADMIN) {
                    String[] element = message.split(" ");
                    if (element.length != 2 || element[1].trim().isEmpty()) {
                        sendMsg("Неверный формат команды. Используйте: /add_admin <имя_пользователя>");
                        continue;
                    }
                    String newAdminName = element[1];
                    ClientHandler newAdminHandler = server.findClientByUsername(newAdminName);
                    server.getAuthenticatedProvider().addAdmin(this, newAdminName, newAdminHandler);

                    if (newAdminHandler != null) {
                        newAdminHandler.setRole(Roles.ADMIN);
                    }
                } else {
                    sendMsg("У вас нет прав для выполнения этой команды.");
                }
            } else if (message.startsWith("/remove_admin ")) {
                if (this.role == Roles.ADMIN) {
                    String[] element = message.split(" ");
                    if (element.length != 2 || element[1].trim().isEmpty()) {
                        sendMsg("Неверный формат команды. Используйте: /remove_admin <username>");
                        continue;
                    }
                    String usernameToRemove = element[1];
                    ClientHandler clientToRemove = server.findClientByUsername(usernameToRemove);
                    server.getAuthenticatedProvider().removeAdminRole(this, usernameToRemove, clientToRemove);
                    if (clientToRemove != null) {
                        clientToRemove.setRole(Roles.USER);
                    }
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
                    isAuthenticated = false;
                    authenticate();
                }
                if (message.equalsIgnoreCase("/exit")) {
                    sendMsg("/exitok");
                    server.removeActiveUser(username);
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
            targetClient.sendMsg("л/с от " + username + ": " + privateMessage);
            sendMsg("л/c для " + targetUsername + ": " + privateMessage);
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
        if (activeUsernames.isEmpty()) {
            sendMsg("В чате нет активных пользователей.");
        } else {
            sendMsg("Текущие пользователи в чате: " + String.join(", ", activeUsernames));
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

    private void sendHelpMessage() {
        String helpMessage = "Доступные команды:\n" +
                "/users - показать текущих пользователей в чате\n" +
                "/w <username> <message> - отправить личное сообщение пользователю\n" +
                "/exit - выйти из чата\n" +
                "/logout - выйти из системы\n" +
                "/help - показать это сообщение";
        if (this.role.equals(Roles.ADMIN)) {
            helpMessage += ("\n/kick <username> - Исключить указанного пользователя из чата\n" +
                    "/add_admin <username> - Назначить пользователю роль администратора\n" +
                    "/remove_admin <username> - Удалить права администратора у пользователя\n");
        }
        sendMsg(helpMessage);
    }
}