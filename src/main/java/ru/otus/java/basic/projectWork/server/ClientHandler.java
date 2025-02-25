package ru.otus.java.basic.projectWork.server;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.time.temporal.ChronoUnit;

public class ClientHandler implements Runnable {
    private Socket socket;
    private Server server;
    private DataInputStream in;
    private DataOutputStream out;
    private String username;
    private Roles role;
    public boolean isAuthenticated = false;
    private String login;
    private String password;
    private boolean hasLeftChat = false;
    private long lastActivityTime;
    private static final long INACTIVITY_TIMEOUT = 20 * 60 * 1000;
    public static final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
    private static final DateTimeFormatter datetimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");
    private boolean isClosed = false;
    private boolean isBanned = false;
    private Integer currentRoom;
    private String lastRoomName;

    public ClientHandler(Socket socket, Server server) throws IOException {
        this.socket = socket;
        this.server = server;
        this.in = new DataInputStream(socket.getInputStream());
        this.out = new DataOutputStream(socket.getOutputStream());
        this.lastActivityTime = System.currentTimeMillis();
        new Thread(this).start();
    }

    public synchronized String getUsername() {
        return username;
    }

    public Socket getSocket() {
        return socket;
    }

    public synchronized void setUsername(String username) {
        this.username = username;
    }

    public synchronized void setRole(Roles role) {
        this.role = role;
    }

    public synchronized Roles getRole() {
        return role;
    }

    public synchronized String getLogin() {
        return login;
    }

    public synchronized void setLogin(String login) {
        this.login = login;
    }

    public synchronized void setPassword(String password) {
        this.password = password;
    }

    public synchronized void setHasLeftChat(boolean hasLeftChat) {
        this.hasLeftChat = hasLeftChat;
    }

    public synchronized Integer getCurrentRoom() {
        return currentRoom;
    }

    public synchronized void setCurrentRoom(Integer currentRoom) {
        this.currentRoom = currentRoom;
    }

    public synchronized void setLastRoomName(String lastRoomName) {
        this.lastRoomName = lastRoomName;
    }

    @Override
    public void run() {
        try {
            System.out.println("Клиент подключился на порту: " + socket.getPort());
            authenticate();
            if (isAuthenticated) {
                handleMessages();
            }
        } catch (IOException e) {
            System.out.println("Клиент отключился: " + socket.getPort());
        } finally {
            disconnect();
            if (isAuthenticated) {
                server.unsubscribe(this);
            }
        }
    }

    private synchronized void updateActivity() {
        lastActivityTime = System.currentTimeMillis();
    }

    private void checkActivity() {
        new Thread(() -> {
            try {
                while (!isClosed) {
                    Thread.sleep(60000);
                    if (System.currentTimeMillis() - lastActivityTime > INACTIVITY_TIMEOUT) {
                        System.out.println("Отключаем клиента " + username + " за неактивность.");
                        inactiveClients();
                        break;
                    }
                }
            } catch (InterruptedException e) {
                System.out.println("Поток проверки активности прерван.");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

    private void authenticate() throws IOException {
        isAuthenticated = false;
        while (!isAuthenticated && !isClosed) {
            if (server == null) {
                sendMsg("Сервер не инициализирован.");
                return;
            }
            sendMsg("Для начала работы надо пройти аутентификацию. Формат команды /auth <логин> <пароль> \n" +
                    "или регистрацию. Формат команды /reg <логин> <пароль> <имя>");
            try {
                if (socket.isClosed()) {
                    System.err.println("Сокет закрыт, прекращаем аутентификацию.");
                    break;
                }
                String message = in.readUTF();
                if (message.startsWith("/")) {
                    if (message.equalsIgnoreCase("/exit")) {
                        sendMsg("/exitok");
                        break;
                    }
                    if (message.startsWith("/auth ")) {
                        String[] elements = message.trim().split("\\s+");
                        if (!isValidCommandFormat(message, 3)) {
                            sendMsg("Неверный формат команды /auth. Используйте: /auth <логин> <пароль>");
                            continue;
                        }
                        String login = elements[1];
                        String password = elements[2];
                        String username = server.getAuthenticatedProvider().getUsernameByLogin(login);
                        if (username != null) {
                            BanInfo banInfo = server.getAuthenticatedProvider().getBanInfo(username);
                            if (banInfo != null && banInfo.isActive()) {
                                sendMsg(banInfo.getBanMessage());
                                sendMsg("/banned");
                                break;
                            }
                        }
                        if (server.getAuthenticatedProvider().authenticate(this, login, password)) {
                            isAuthenticated = true;
                            Integer commonRoomId = server.getAuthenticatedProvider().getRoomIdByName(lastRoomName);
                            if (commonRoomId != null) {
                                setCurrentRoom(commonRoomId);
                            } else {
                                System.err.println(lastRoomName + " не найдена!");
                            }
                            sendMsg("Вы успешно аутентифицированы. Ваша текущая роль: " + role + ".\n" +
                                    "Для получения списка доступных команд, пожалуйста, введите /help.\n" +
                                    "Вы вошли в комнату '" + lastRoomName + "'.");
                            updateActivity();
                            checkActivity();
                            server.getAuthenticatedProvider().loadChatHistory(this, lastRoomName);
                            server.subscribe(this);
                            sendActiveUsers();
                            break;
                        } else {
                            sendMsg("Аутентификация не пройдена. Проверьте ваши данные.");
                        }
                    }
                    if (message.startsWith("/reg ")) {
                        String[] elements = message.trim().split("\\s+");
                        if (!isValidCommandFormat(message, 4)) {
                            sendMsg("Неверный формат команды /reg. Используйте: /reg <логин> <пароль> <имя>");
                            continue;
                        }
                        if (server.getAuthenticatedProvider().registration(this, elements[1], elements[2], elements[3])) {
                            isAuthenticated = true;
                            String lastRoomName = "Общая комната";
                            Integer commonRoomId = server.getAuthenticatedProvider().getRoomIdByName(lastRoomName);
                            if (commonRoomId != null) {
                                setCurrentRoom(commonRoomId);
                            } else {
                                System.err.println(lastRoomName + " не найдена!");
                            }
                            sendMsg("Вы успешно завершили процесс регистрации! Ваша текущая роль: " + role + ".\n" +
                                    "В дальнейшем используйте логин: " + login + " и пароль: " + password + " для аутентификации.\n" +
                                    "Для получения списка доступных команд, пожалуйста, введите /help.\n" +
                                    "Вы вошли в комнату '" + lastRoomName + "'.");
                            updateActivity();
                            checkActivity();
                            server.getAuthenticatedProvider().loadChatHistory(this, lastRoomName);
                            server.subscribe(this);
                            sendActiveUsers();
                            break;
                        }
                    }
                }
            } catch (IOException e) {
                System.err.println("Ошибка при чтении сообщения во время аутентификации: " + e.getMessage());
                e.printStackTrace();
                break;
            }
        }
    }

    private void handleMessages() throws IOException {
        try {
            while (!isClosed) {
                updateActivity();
                String message = in.readUTF();
                if (isBanned) {
                    sendMsg("Вы забанены и не можете отправлять сообщения.");
                    continue;
                }
                if (message.startsWith("/ban ")) {
                    handleBanCommand(message);
                } else if (message.startsWith("/unban ")) {
                    handleUnbanCommand(message);
                } else if (message.startsWith("/kick ")) {
                    handleKickCommand(message);
                } else if (message.startsWith("/add_admin ")) {
                    handleAddAdminCommand(message);
                } else if (message.startsWith("/remove_admin ")) {
                    handleRemoveAdminCommand(message);
                } else if (message.startsWith("/rename ")) {
                    handleRenameUserCommand(message);
                } else if (message.startsWith("/w ")) {
                    sendPrivateMessage(message);
                } else if (message.equalsIgnoreCase("/users")) {
                    sendActiveUsers();
                } else if (message.equalsIgnoreCase("/help")) {
                    sendHelpMessage();
                } else if (message.equalsIgnoreCase("/listrooms")) {
                    handleListRoomsCommand();
                } else if (message.startsWith("/createroom ")) {
                    handleCreateRoomCommand(message);
                } else if (message.startsWith("/enter ")) {
                    handleEnterRoomCommand(message);
                } else if (message.equalsIgnoreCase("/leaveroom")) {
                    handleLeaveRoomCommand();
                } else if (message.startsWith("/invite ")) {
                    handleInviteUserCommand(message);
                } else if (message.equalsIgnoreCase("/myinvites")) {
                    handleMyInvitesCommand();
                } else if (message.startsWith("/decline ")) {
                    handleDeclineInviteCommand(message);
                } else if (message.startsWith("/deleteroom ")) {
                    handleDeleteRoomCommand(message);
                } else if (message.startsWith("/")) {
                    handleSystemCommands(message);
                } else {
                    handleChatMessage(message);
                    updateActivity();
                }
            }
        } catch (IOException e) {
            if (e instanceof SocketException && "Socket closed".equals(e.getMessage())) {
                System.out.println("Соединение с клиентом закрыто.");
            } else if (isBanned) {
                System.out.println(username + " Был забанен администратором.");
            } else {
                System.err.println("Ошибка при обработке сообщения: " + e.getMessage());
            }
        }
    }

    private void handleCreateRoomCommand(String message) {
        String[] elements = message.trim().split("\\s+");
        if (elements.length < 2) {
            sendMsg("Неверный формат команды /createroom. Используйте: /createroom <room_name> [password]");
            return;
        }
        String roomName = elements[1];
        String password = (elements.length > 2) ? elements[2] : null;
        if (server.getAuthenticatedProvider().createRoom(this, roomName, password)) {
            sendMsg("Комната '" + roomName + "' успешно создана.");
        }
    }

    private void handleEnterRoomCommand(String message) {
        String[] elements = message.trim().split("\\s+");
        if (elements.length < 2) {
            sendMsg("Неверный формат команды /enter. Используйте: /enter <room_name> [password]");
            return;
        }
        String roomName = elements[1];
        String password = (elements.length > 2) ? elements[2] : null;
        boolean enterSuccessful = server.getAuthenticatedProvider().enterRoom(this, roomName, password);
        if (enterSuccessful) {
            Integer roomId = server.getAuthenticatedProvider().getRoomIdByName(roomName);
            if (roomId != null) {
                setCurrentRoom(roomId);
            } else {
            }
        }
    }
    private void handleDeleteRoomCommand(String message) {
        String[] elements = message.trim().split("\\s+");
        if (elements.length != 2) {
            sendMsg("Неверный формат команды /deleteroom. Используйте: /deleteroom <room_name>");
            return;
        }
        String roomName = elements[1];
        if (server.getAuthenticatedProvider().deleteRoom(this, roomName)) {
            sendMsg("Вы успешно удалили комнату '" + roomName + "'.");
        }
    }

    private void handleListRoomsCommand() {
        int userId = server.getAuthenticatedProvider().getUserIdByUsername(this.username);
        if (userId == -1) {
            sendMsg("Ошибка: не удалось определить ID пользователя.");
            return;
        }
        List<String> publicRooms = server.getAuthenticatedProvider().getAllPublicRooms();
        List<String> createdRooms = server.getAuthenticatedProvider().getUserCreatedRooms(userId);
        List<String> invitedRooms = server.getAuthenticatedProvider().getUserInvitedRooms(userId);
        StringBuilder message = new StringBuilder();
        if (!publicRooms.isEmpty()) {
            message.append("Публичные комнаты: ").append(String.join(", ", publicRooms)).append("\n");
        }
        if (!createdRooms.isEmpty()) {
            message.append("Ваши комнаты:\n   ").append(String.join("\n   ", createdRooms)).append("\n");
        }
        if (!invitedRooms.isEmpty()) {
            message.append("Комнаты, в которые вас пригласили:\n   ").append(String.join("\n   ", invitedRooms)).append("\n");
        }
        if (message.length() == 0) {
            sendMsg("Нет доступных комнат.");
        } else {
            sendMsg(message.toString());
        }
    }
    private void handleLeaveRoomCommand() {
        Integer currentRoomId = getCurrentRoom();
        if (currentRoomId == null) {
            sendMsg("Вы не находитесь ни в одной комнате.");
            return;
        }
        Integer commonRoomId = server.getAuthenticatedProvider().getRoomIdByName("Общая комната");
        if (commonRoomId == null) {
            sendMsg("Общая комната не найдена.");
            return;
        }
        server.getAuthenticatedProvider().leaveCurrentRoom(this);
        boolean enterSuccessful = server.getAuthenticatedProvider().enterRoom(this, "Общая комната", null);
        if (enterSuccessful) {
            setCurrentRoom(commonRoomId);
        } else {
            sendMsg("Не удалось войти в общую комнату.");
        }
    }

    private void handleInviteUserCommand(String message) {
        String[] elements = message.trim().split("\\s+");
        if (elements.length != 3) {
            sendMsg("Неверный формат команды /invite. Используйте: /invite <room_name> <username>");
            return;
        }
        String roomName = elements[1];
        String inviteeUsername = elements[2];
        if (server.getAuthenticatedProvider().inviteUserToRoom(this, roomName, inviteeUsername)) {
            sendMsg("Приглашение пользователю '" + inviteeUsername + "' в комнату '" + roomName + "' отправлено.");
        }
    }

    private void handleMyInvitesCommand() {
        List<String> inviteList = server.getAuthenticatedProvider().getInvitesForUser(this);
        if (inviteList.isEmpty()) {
            sendMsg("У вас нет приглашений в комнаты.");
        } else {
            sendMsg("Ваши приглашения в комнаты:\n" + String.join("\n", inviteList));
        }
    }

    private void handleDeclineInviteCommand(String message) {
        String[] elements = message.trim().split("\\s+");
        if (elements.length != 2) {
            sendMsg("Неверный формат команды /decline. Используйте: /decline <room_name>");
            return;
        }
        String roomName = elements[1];
        server.getAuthenticatedProvider().removeInvite(roomName, this);
        sendMsg("Вы отклонили приглашение в комнату '" + roomName + "'.");
    }

    private synchronized void handleSystemCommands(String message) throws IOException {
        if (message.equalsIgnoreCase("/shutdown")) {
            handleShutdownCommand(message);
        } else if (message.equalsIgnoreCase("/logout")) {
            sendMsg("/logoutok");
            server.logout(this);
            isAuthenticated = false;
            authenticate();
        } else if (message.equalsIgnoreCase("/exit")) {
            setHasLeftChat(true);
            sendMsg("/exitok");
            server.removeActiveUser(username);
        }
    }

    private void handleShutdownCommand(String message) {
        if (this.role == Roles.ADMIN) {
            System.out.println("Администратор " + username + " запросил остановку сервера.");
            server.stop();
        } else {
            sendMsg("У вас нет прав для выполнения этой команды.");
        }
    }


    private void handleKickCommand(String message) {
        String[] elements = message.trim().split("\\s+");
        if (!isValidCommandFormat(message, 2)) {
            sendMsg("Неверный формат команды /kick. Используйте: /kick <имя>");
            return;
        }
        String usernameToKick = elements[1];
        if (usernameToKick.equals(this.username)) {
            sendMsg("Вы не можете отключить самого себя!");
            return;
        }
        ClientHandler kickedClient = server.findClientByUsername(usernameToKick);
        if (kickedClient != null) {
            Integer currentRoomId = getCurrentRoom();
            Integer commonRoomId = server.getAuthenticatedProvider().getRoomIdByName("Общая комната");

            if (currentRoomId != null && currentRoomId.equals(commonRoomId)) {
                if (this.role.equals(Roles.ADMIN)) {
                    server.kickUser(usernameToKick);
                    sendMsg("Пользователь " + usernameToKick + " был отключен от чата администратором.");
                } else {
                    sendMsg("У вас нет прав для выполнения этой команды.");
                }
            } else {
                if (this.role.equals(Roles.ADMIN) || server.getAuthenticatedProvider().isRoomOwner(this, server.getAuthenticatedProvider().getRoomNameById(currentRoomId))) {
                    Integer kickedUserRoomId = kickedClient.getCurrentRoom();
                    if (kickedUserRoomId != null) {
                        if (!kickedUserRoomId.equals(currentRoomId)) {
                            sendMsg("Вы можете кикать только из своей комнаты.");
                            return;
                        }
                        server.getAuthenticatedProvider().leaveCurrentRoom(kickedClient);
                        server.getAuthenticatedProvider().enterRoom(kickedClient, "Общая комната", null);
                        sendMsg("Пользователь " + usernameToKick + " перемещен в общую комнату.");
                    } else {
                        sendMsg("Пользователь " + usernameToKick + " не находится в комнате.");
                    }
                } else {
                    sendMsg("У вас нет прав для выполнения этой команды.");
                }
            }
        } else {
            sendMsg("Пользователь " + usernameToKick + " не найден.");
        }
    }

    private void handleAddAdminCommand(String message) {
        if (this.role.equals(Roles.ADMIN)) {
            String[] elements = message.trim().split("\\s+");
            if (!isValidCommandFormat(message, 2)) {
                sendMsg("Неверный формат команды. Используйте: /add_admin <имя_пользователя>");
                return;
            }
            String newAdminName = elements[1];
            ClientHandler newAdminHandler = server.findClientByUsername(newAdminName);
            server.getAuthenticatedProvider().addAdmin(this, newAdminName, newAdminHandler);
        } else {
            sendMsg("У вас нет прав для выполнения этой команды.");
        }
    }

    private void handleRemoveAdminCommand(String message) {
        if (this.role.equals(Roles.ADMIN)) {
            String[] elements = message.trim().split("\\s+");
            if (!isValidCommandFormat(message, 2)) {
                sendMsg("Неверный формат команды. Используйте: /remove_admin <username>");
                return;
            }
            String usernameToRemove = elements[1];
            ClientHandler clientToRemove = server.findClientByUsername(usernameToRemove);
            server.getAuthenticatedProvider().removeAdminRole(this, usernameToRemove, clientToRemove);
        } else {
            sendMsg("У вас нет прав для выполнения этой команды.");
        }
    }

    private void handleRenameUserCommand(String message) {
        String oldNick = username;
        String[] elements = message.trim().split("\\s+");
        if (!isValidCommandFormat(message, 2)) {
            sendMsg("Неверный формат команды /rename. Используйте: /rename <new username>");
            return;
        }
        String usernameToRename = elements[1];
        boolean changeSuccessful = server.getAuthenticatedProvider().changeUsername(this, usernameToRename);
        if (changeSuccessful) {
            username = usernameToRename;
            sendMsg("/renameok");
            server.broadcastMessage("Пользователь " + oldNick + " сменил ник на: " + username);
        } else {
            sendMsg("Не удалось изменить ник. Возможно, ник уже занят.");
        }
    }

    public void handleChatMessage(String message) {
        if (currentRoom == null) {
            sendMsg("Вы не находитесь ни в одной комнате. Войдите в комнату, чтобы отправлять сообщения.");
            return;
        }
        server.getAuthenticatedProvider().handleChatMessage(this, message);
    }

    private void handleBanCommand(String message) {
        if (this.role == Roles.ADMIN) {
            String[] elements = message.trim().split("\\s+");
            if (!isValidCommandFormat(message, 2)) {
                sendMsg("Неверный формат команды /ban. Используйте: /ban <username> [время] [причина]");
                return;
            }
            String usernameToBan = elements[1];
            if (usernameToBan.equals(this.username)) {
                sendMsg("Вы не можете забанить самого себя!");
                return;
            }
            BanInfo existingBan = server.getAuthenticatedProvider().getBanInfo(usernameToBan);
            if (existingBan != null && existingBan.isActive()) {
                sendMsg("Пользователь " + usernameToBan + " уже забанен.  Срок бана: " + (existingBan.isPermanent() ? "бессрочно" : existingBan.getBanEnd().format(datetimeFormatter)) + ", причина: " + existingBan.getReason());
                return;
            }
            LocalDateTime banEnd = null;
            String reason = "Без причины";
            int banDuration = 0;
            if (elements.length >= 3) {
                try {
                    banDuration = Integer.parseInt(elements[2]);
                    banEnd = LocalDateTime.now().plus(banDuration, ChronoUnit.MINUTES);
                    if (elements.length == 4) {
                        reason = elements[3];
                    }
                } catch (NumberFormatException e) {
                    reason = String.join(" ", Arrays.copyOfRange(elements, 2, elements.length));
                    banEnd = null;
                    banDuration = 0;
                }
            }
            boolean banSuccessful = server.getAuthenticatedProvider().addBan(usernameToBan, banEnd, reason);
            if (banSuccessful) {
                BanInfo banInfo = server.getAuthenticatedProvider().getBanInfo(usernameToBan);
                for (ClientHandler client : server.getClients()) {
                    if (!client.getUsername().equals(usernameToBan)) {
                        if (banInfo != null) {
                            client.sendMsg("Пользователь " + usernameToBan + " был забанен" + (banEnd != null ? " до " + banEnd.format(datetimeFormatter) : " перманентно") + " по причине: " + reason);
                        }
                    }
                }
                ClientHandler bannedClient = server.findClientByUsername(usernameToBan);
                if (bannedClient != null) {
                    if (banInfo != null) {
                        bannedClient.isBanned = true;
                        bannedClient.sendMsg(banInfo.getBanMessage());
                        bannedClient.sendMsg("/banned");
                    }
                }
            } else {
                sendMsg("Не удалось забанить пользователя " + usernameToBan);
            }
        } else {
            sendMsg("У вас нет прав для выполнения этой команды.");
        }
    }

    private void handleUnbanCommand(String message) {
        if (this.role == Roles.ADMIN) {
            String[] elements = message.trim().split("\\s+");
            if (!isValidCommandFormat(message, 2)) {
                sendMsg("Неверный формат команды /unban. Используйте: /unban <username>");
                return;
            }
            String usernameToUnban = elements[1];
            boolean unbanSuccessful = server.getAuthenticatedProvider().removeBan(usernameToUnban);
            if (unbanSuccessful) {
                server.broadcastMessage("Пользователь " + usernameToUnban + " был разбанен администратором.");
                ClientHandler unbannedClient = server.findClientByUsername(usernameToUnban);
                if (unbannedClient != null) {
                    unbannedClient.isBanned = false;
                    unbannedClient.sendMsg("Вы были разбанены администратором.");
                }
            } else {
                sendMsg("Не удалось разбанить пользователя " + usernameToUnban);
            }
        } else {
            sendMsg("У вас нет прав для выполнения этой команды.");
        }
    }

    private boolean isValidCommandFormat(String message, int minArgs) {
        String[] elements = message.trim().split("\\s+");
        return elements.length >= minArgs;
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
        String currentTime = LocalDateTime.now().format(timeFormatter);
        if (targetClient != null) {
            targetClient.sendMsg("л/с от " + username + ": " + privateMessage);
            sendMsg("[" + currentTime + "] л/c для " + targetUsername + ": " + privateMessage);
        } else {
            sendMsg("Пользователь " + targetUsername + " не найден.");
        }
    }

    public synchronized void sendMsg(String message) {
        try {
            if (!isClosed) {
                out.writeUTF(message);
            }
        } catch (IOException e) {
            System.err.println("Ошибка при отправке сообщения: " + e.getMessage());
        }
    }

    private void sendActiveUsers() {
        Integer currentRoomId = getCurrentRoom();
        if (currentRoomId == null) {
            sendMsg("Вы не находитесь ни в одной комнате.");
            return;
        }
        List<String> activeUsersInRoom = server.getActiveUsernamesInRoom(currentRoomId);
        if (activeUsersInRoom.isEmpty()) {
            sendMsg("В этой комнате нет активных пользователей.");
        } else {
            sendMsg("Текущие пользователи в этой комнате: " + String.join(", ", activeUsersInRoom));
        }
    }

    public synchronized void inactiveClients() throws IOException {
        sendMsg("Вы были отключены за неактивность. Пожалуйста, войдите снова.");
        server.logout(this);
        isAuthenticated = false;
        authenticate();
    }

    public synchronized void disconnect() {
        System.out.println("Клиент отключился: " + socket.getPort());
        isClosed = true;
        try {
            server.getAuthenticatedProvider().leaveCurrentRoom(this);

            if (in != null) in.close();
            if (out != null) out.close();
            if (socket != null && !socket.isClosed()) socket.close();
        } catch (IOException e) {
            System.err.println("Ошибка при отключении соединения: " + e.getMessage());

        }
    }

    private void sendHelpMessage() {
        String helpMessage = "Доступные команды:\n" +
                "/users - показать текущих пользователей в чате\n" +
                "/w <username> <message> - отправить личное сообщение пользователю\n" +
                "/exit - выход из приложения\n" +
                "/logout - выйти из системы\n" +
                "/rename <username> - изменить ник\n" +
                "/listrooms - показать список доступных комнат\n" +
                "/createroom <room_name> [password] - создать новую комнату (пароль опционален)\n" +
                "/enter <room_name> [password] - войти в комнату (пароль требуется, если комната запаролена)\n" +
                "/deleteroom <room_name> - Удалить комнату\n" +
                "/leaveroom - покинуть текущую комнату\n" +
                "/invite <room_name> <username> - пригласить пользователя в комнату\n" +
                "/myinvites - посмотреть свои приглашения\n" +
                "/decline <room_name> - отклонить приглашение в комнату" +
                "/help - показать это сообщение\n";
        if (this.role.equals(Roles.ADMIN)) {
            helpMessage += ("\n/kick <username> - Исключить указанного пользователя из чата\n" +
                    "/add_admin <username> - Назначить пользователю роль администратора\n" +
                    "/remove_admin <username> - Удалить права администратора\n" +
                    "/ban <username> <reason> - Перманентный бан (например, /ban Evgen Нарушение правил)\n" +
                    "/ban <username> <время в минутах> <reason> - Временный бан  (например, /ban Evgen 60 Флуд)\n" +
                    "/unban <username> - Удалить пользователя из Бана)\n" +
                    "/shutdown - Остановить сервер\n");
        }
        sendMsg(helpMessage);
    }


}