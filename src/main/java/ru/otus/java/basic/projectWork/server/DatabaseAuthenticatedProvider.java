package ru.otus.java.basic.projectWork.server;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DatabaseAuthenticatedProvider implements AuthenticatedProvider {
    private static final String REGISTRATION_QUERY = "INSERT INTO users (login, password, username) VALUES (?, ?, ?)";
    private static final String AUTHENTICATION_QUERY = "SELECT password, id,username FROM users WHERE login = ?";
    private static final String GET_ROLE_QUERY = "SELECT r.name FROM roles r JOIN users_to_roles ur ON r.id = ur.role_id WHERE ur.user_id = ?";
    private static final String REMOVE_ADMIN_QUERY = "DELETE FROM users_to_roles WHERE user_id = (SELECT id FROM users WHERE username = ?) AND role_id = (SELECT id FROM roles WHERE name = 'admin')";
    private static final String GET_USER_ID_QUERY = "SELECT id FROM users WHERE username = ?";
    private static final String SET_ROLE_FOR_USER_QUERY = "INSERT INTO users_to_roles (user_id, role_id) VALUES (?, ?)";
    private static final String ROLE_EXISTS_QUERY = "SELECT COUNT(*) FROM users_to_roles WHERE user_id = ? AND role_id = (SELECT id FROM roles WHERE name = ?)";
    private static final String IS_USERNAME_TAKEN_QUERY = "SELECT COUNT(*) FROM users WHERE login = ? OR username = ?";
    private static final String CHECK_QUERY = "SELECT id FROM users WHERE username = ?";
    private static final String UPDATE_QUERY = "UPDATE users SET username = ? WHERE login = ?";
    private static final String ADD_BAN_QUERY = "INSERT INTO bans (user_id, ban_start, ban_end, reason) VALUES (?, ?, ?, ?)";
    private static final String GET_BAN_QUERY = "SELECT ban_start, ban_end, reason FROM bans WHERE user_id = ? AND (ban_end IS NULL OR ban_end > NOW())";
    private static final String REMOVE_BAN_QUERY = "DELETE FROM bans WHERE user_id = ?";
    private static final String GET_USERNAME_BY_LOGIN = "SELECT username FROM users WHERE login = ?";
    private static final String GET_ROOM_ID_BY_NAME = "SELECT id FROM rooms WHERE name = ?";
    private static final String GET_ROOM_NAME_BY_ID = "SELECT name FROM rooms WHERE id = ?";
    private static final String GET_ALL_ROOMS = "SELECT name FROM rooms";
    private static final String CREATE_ROOM = "INSERT INTO rooms (name, password, owner_id, creation_date, last_activity) VALUES (?, ?, ?, ?, ?)";
    private static final String GET_ROOM_OWNER_ID = "SELECT owner_id FROM rooms WHERE id = ?";
    private static final String GET_USER_ROOM_COUNT = "SELECT COUNT(*) FROM rooms WHERE owner_id = ?";
    private static final String ENTER_ROOM = "INSERT INTO room_users (room_id, user_id) VALUES (?, ?)";
    private static final String LEAVE_ROOM = "DELETE FROM room_users WHERE room_id = ? AND user_id = ?";
    private static final String GET_ROOM_PASSWORD = "SELECT password FROM rooms WHERE name = ?";
    private static final String SAVE_MESSAGE = "INSERT INTO room_messages (room_id, sender, message, timestamp) VALUES (?, ?, ?, ?)";
    private static final String GET_MESSAGES = "SELECT sender, message, timestamp FROM room_messages WHERE room_id = ? ORDER BY timestamp ASC LIMIT 100";
    private static final String CLEANUP_OLD_ROOMS = "DELETE FROM rooms WHERE last_activity < NOW() - INTERVAL '7 days'";
    private static final String UPDATE_LAST_ACTIVITY = "UPDATE rooms SET last_activity = NOW() WHERE id = ?";
    private static final String CHECK_ROOM_USER = "SELECT COUNT(*) FROM room_users WHERE room_id = ? AND user_id = ?";
    private static final String ADD_INVITE = "INSERT INTO room_invites (room_id, user_id, invited_by) VALUES (?, ?, ?)";
    private static final String GET_INVITES_FOR_USER = "SELECT r.name, r.password FROM rooms r INNER JOIN room_invites ri ON r.id = ri.room_id WHERE ri.user_id = ?";
    private static final String REMOVE_INVITE = "DELETE FROM room_invites WHERE room_id = ? AND user_id = ?";
    private static final String CAN_INVITE = "SELECT COUNT(*) FROM room_users WHERE room_id = ? AND user_id = ?";
    private static final String GET_LAST_ROOM_NAME = "SELECT room_name FROM last_rooms WHERE user_id = ?";
    private static final String UPDATE_LAST_ROOM_NAME = "INSERT INTO last_rooms (user_id, room_name) VALUES (?, ?) ON CONFLICT (user_id) DO UPDATE SET room_name = ?";
    private static final String ROOM_EXISTS_QUERY = "SELECT COUNT(*) FROM rooms WHERE name = ?";
    private static final String DELETE_ROOM_USERS = "DELETE FROM room_users WHERE room_id = ?";
    private static final String DELETE_ROOM_MESSAGES = "DELETE FROM room_messages WHERE room_id = ?";
    private static final String DELETE_ROOM_INVITES = "DELETE FROM room_invites WHERE room_id = ?";
    private static final String DELETE_ROOM = "DELETE FROM rooms WHERE name = ?";
    private static final String GET_INVITED_USERS = "SELECT user_id FROM room_invites WHERE room_id = ?";
    private static final String GET_ALL_PUBLIC_ROOMS = "SELECT name FROM rooms WHERE password IS NULL";
    private static final String GET_USER_CREATED_ROOMS = "SELECT name, password FROM rooms WHERE owner_id = ?";
    private static final String GET_USER_INVITED_ROOMS = "SELECT r.name, r.password FROM rooms r INNER JOIN room_invites ri ON r.id = ri.room_id WHERE ri.user_id = ?";

    private Server server;
    private final Connection connection;
    private final String COMMON_ROOM_NAME = "Общая комната";

    public DatabaseAuthenticatedProvider(Server server) throws SQLException {
        this.server = server;
        this.connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/projectwork", "postgres", "admin");
    }

    @Override
    public void initialize() {
        System.out.println("Инициализация DatabaseAuthenticatedProvider");
        cleanupOldRooms();
    }

    @Override
    public boolean authenticate(ClientHandler clientHandler, String login, String password) {
        String dbPassword = null;
        int userId = -1;
        String username = null;
        ResultSet rs = null;
        PreparedStatement ps = null;
        try {
            ps = connection.prepareStatement(AUTHENTICATION_QUERY);
            ps.setString(1, login);
            rs = ps.executeQuery();
            if (rs.next()) {
                dbPassword = rs.getString("password");
                userId = rs.getInt("id");
                username = rs.getString("username");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            close(rs);
            close(ps);
        }
        if (server.isUserActive(username)) {
            clientHandler.sendMsg("Пользователь уже аутентифицирован на другом устройстве.");
            return false;
        }
        if (dbPassword == null || !dbPassword.equals(password)) {
            clientHandler.sendMsg("Неверный логин/пароль");
            return false;
        }
        List<Roles> userRoles = getRoleForUser(userId);
        clientHandler.setUsername(username);
        clientHandler.setLogin(login);
        server.addActiveUser(username);
        if (userRoles.contains(Roles.ADMIN)) {
            clientHandler.setRole(Roles.ADMIN);
            clientHandler.sendMsg("/authok " + username);
        } else if (userRoles.contains(Roles.USER)) {
            clientHandler.setRole(Roles.USER);
            clientHandler.sendMsg("/authok " + username);
        } else {
            clientHandler.sendMsg("У вас нет прав для входа.");
            return false;
        }
        String lastRoomName = getLastRoomName(userId);
        clientHandler.setLastRoomName(lastRoomName);
        return true;
    }

    @Override
    public boolean registration(ClientHandler clientHandler, String login, String password, String username) {
        Roles role = Roles.USER;
        if (login.length() < 3 || password.length() < 3 || username.length() < 3) {
            clientHandler.sendMsg("Логин 3+ символа, пароль 3+ символа, имя пользователя 3+ символа");
            return false;
        }
        String availabilityMessage = checkUsernameAvailability(login, username);
        if (availabilityMessage != null) {
            clientHandler.sendMsg(availabilityMessage);
            return false;
        }
        try (PreparedStatement ps = connection.prepareStatement(REGISTRATION_QUERY, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, login);
            ps.setString(2, password);
            ps.setString(3, username);
            ps.executeUpdate();
            ResultSet generatedKeys = ps.getGeneratedKeys();
            if (generatedKeys.next()) {
                int userId = generatedKeys.getInt(1);
                setRoleForUser(userId, role);
                clientHandler.setUsername(username);
                clientHandler.setRole(role);
                clientHandler.setLogin(login);
                clientHandler.setPassword(password);
                clientHandler.sendMsg("/regok " + username);

                return true;
            }
        } catch (SQLException e) {
            clientHandler.sendMsg("Ошибка регистрации: " + e.getMessage());
        }
        return false;
    }

    private List<Roles> getRoleForUser(int userId) {
        List<Roles> roles = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(GET_ROLE_QUERY)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String roleName = rs.getString("name");
                    roles.add(Roles.valueOf(roleName.toUpperCase()));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return roles;
    }

    public boolean addAdmin(ClientHandler clientHandler, String username, ClientHandler newAdminHandler) {
        Roles roles = clientHandler.getRole();
        if (roles != Roles.ADMIN) {
            clientHandler.sendMsg("У вас нет прав для добавления администратора.");
            return false;
        }
        int userId = getUserIdByUsername(username);
        if (userId == -1) {
            clientHandler.sendMsg("Пользователь с таким именем не найден.");
            return false;
        }
        if (roleExists(userId, Roles.ADMIN)) {
            clientHandler.sendMsg("Пользователь " + username + " уже имеет роль администратора.");
            return false;
        }
        try {
            setRoleForUser(userId, Roles.ADMIN);
            clientHandler.sendMsg("Пользователь " + username + " успешно добавлен как администратор.");
            if (newAdminHandler != null) {
                newAdminHandler.setRole(Roles.ADMIN);
                newAdminHandler.sendMsg("\nПоздравляем! Вы были назначены администратором!\n" +
                        "Ваши права уже вступили в силу.\n");
            }
            return true;
        } catch (SQLException e) {
            clientHandler.sendMsg("Ошибка при добавлении администратора: " + e.getMessage());
            return false;
        }
    }

    public boolean removeAdminRole(ClientHandler clientHandler, String username, ClientHandler clientToRemove) {
        Roles roles = clientHandler.getRole();
        if (roles != Roles.ADMIN) {
            clientHandler.sendMsg("У вас нет прав для удаления администратора.");
            return false;
        }
        int userId = getUserIdByUsername(username);
        if (userId == -1) {
            clientHandler.sendMsg("Пользователь с таким именем не найден.");
            return false;
        }
        if (!roleExists(userId, Roles.ADMIN)) {
            clientHandler.sendMsg("У пользователя " + username + " нет роли администратора.");
            return false;
        }
        try (PreparedStatement pstmt = connection.prepareStatement(REMOVE_ADMIN_QUERY)) {
            pstmt.setString(1, username);
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                clientHandler.sendMsg("Роль администратора у пользователя " + username + " успешно удалена.");
                if (clientToRemove != null) {
                    clientToRemove.setRole(Roles.USER);
                    clientToRemove.sendMsg("\nУведомляем вас, что ваша роль администратора была удалена!\n" +
                            "Теперь вы обычный пользователь.\n");
                }
            }
            return affectedRows > 0;
        } catch (SQLException e) {
            clientHandler.sendMsg("Произошла ошибка при удалении роли администратора. Пожалуйста, попробуйте позже.");
            return false;
        }
    }

    void setRoleForUser(int userId, Roles role) throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement(SET_ROLE_FOR_USER_QUERY)) {
            ps.setInt(1, userId);
            ps.setInt(2, role.ordinal() + 1);
            ps.executeUpdate();
        }
    }

    private String checkUsernameAvailability(String login, String username) {
        boolean isLoginTaken = isUsernameTaken(login);
        boolean isUsernameTaken = isUsernameTaken(username);
        if (isLoginTaken && isUsernameTaken) {
            return "Логин и имя пользователя уже заняты!";
        } else if (isLoginTaken) {
            return "Логин уже занят!";
        } else if (isUsernameTaken) {
            return "Имя пользователя уже занято!";
        }
        return null;
    }

    private boolean roleExists(int userId, Roles role) {
        try (PreparedStatement ps = connection.prepareStatement(ROLE_EXISTS_QUERY)) {
            ps.setInt(1, userId);
            ps.setString(2, role.name().toLowerCase());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean isUsernameTaken(String username) {
        try (PreparedStatement ps = connection.prepareStatement(IS_USERNAME_TAKEN_QUERY)) {
            ps.setString(1, username);
            ps.setString(2, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) {
                    return true;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean changeUsername(ClientHandler clientHandler, String newUsername) {
        try {
            PreparedStatement checkStmt = connection.prepareStatement(CHECK_QUERY);
            checkStmt.setString(1, newUsername);
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next()) {
                return false;
            }
            PreparedStatement updateStmt = connection.prepareStatement(UPDATE_QUERY);
            updateStmt.setString(1, newUsername);
            updateStmt.setString(2, clientHandler.getLogin());
            updateStmt.executeUpdate();
            clientHandler.setUsername(newUsername);
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public int getUserIdByUsername(String username) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = connection.prepareStatement(GET_USER_ID_QUERY);
            ps.setString(1, username);
            rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }
        } catch (SQLException e) {
            System.err.println("Ошибка при получении user_id по username: " + e.getMessage());
        } finally {
            close(rs);
            close(ps);
        }
        return -1;
    }
    @Override
    public String getUsernameByLogin(String login) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = connection.prepareStatement(GET_USERNAME_BY_LOGIN);
            ps.setString(1, login);
            rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getString("username");
            }
        } catch (SQLException e) {
            System.err.println("Ошибка при получении имени пользователя по логину: " + e.getMessage());
        } finally {
            close(rs);
            close(ps);
        }
        return null;
    }

    public boolean addBan(String username, LocalDateTime banEnd, String reason) {
        PreparedStatement ps = null;
        int userId = getUserIdByUsername(username);
        if (userId == -1) {
            System.err.println("Пользователь с именем " + username + " не найден.");
            return false;
        }
        try {
            ps = connection.prepareStatement(ADD_BAN_QUERY);
            ps.setInt(1, userId);
            ps.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
            if (banEnd != null) {
                ps.setTimestamp(3, Timestamp.valueOf(banEnd));
            } else {
                ps.setNull(3, Types.TIMESTAMP);
            }
            ps.setString(4, reason);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Ошибка при добавлении бана: " + e.getMessage());
            return false;
        } finally {
            close(ps);
        }
    }

    public BanInfo getBanInfo(String username) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        int userId = getUserIdByUsername(username);
        if (userId == -1) {
            System.err.println("Не удалось получить user_id для пользователя " + username);
            return null;
        }
        try {
            ps = connection.prepareStatement(GET_BAN_QUERY);
            ps.setInt(1, userId);
            rs = ps.executeQuery();
            if (rs.next()) {
                LocalDateTime banStart = rs.getTimestamp("ban_start").toLocalDateTime();
                Timestamp banEndTimestamp = rs.getTimestamp("ban_end");
                LocalDateTime banEnd = banEndTimestamp != null ? banEndTimestamp.toLocalDateTime() : null;
                String reason = rs.getString("reason");
                return new BanInfo(username, banStart, banEnd, reason);
            }
        } catch (SQLException e) {
            System.err.println("Ошибка при получении информации о бане: " + e.getMessage());
        } finally {
            close(rs);
            close(ps);
        }
        return null;
    }

    public boolean removeBan(String username) {
        PreparedStatement ps = null;
        int userId = getUserIdByUsername(username);
        if (userId == -1) {
            System.err.println("Не удалось получить user_id для пользователя " + username);
            return false;
        }
        try {
            ps = connection.prepareStatement(REMOVE_BAN_QUERY);
            ps.setInt(1, userId);
            int affectedRows = ps.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Ошибка при удалении бана: " + e.getMessage());
            return false;
        } finally {
            close(ps);
        }
    }

     public boolean createRoom(ClientHandler clientHandler, String roomName, String password) {
        int ownerId = getUserIdByUsername(clientHandler.getUsername());
        if (ownerId == -1) {
            clientHandler.sendMsg("Ошибка при создании комнаты: не удалось определить ID пользователя.");
            return false;
        }
        if (roomExists(roomName)) {
            clientHandler.sendMsg("Комната с именем '" + roomName + "' уже существует.");
            return false;
        }
        int roomCount = getUserRoomCount(ownerId);
        if (roomCount >= 5) {
            clientHandler.sendMsg("Вы достигли максимального количества комнат.");
            return false;
        }
        try (PreparedStatement ps = connection.prepareStatement(CREATE_ROOM)) {
            ps.setString(1, roomName);
            ps.setString(2, password);
            ps.setInt(3, ownerId);
            ps.setTimestamp(4, Timestamp.valueOf(LocalDateTime.now()));
            ps.setTimestamp(5, Timestamp.valueOf(LocalDateTime.now()));
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            clientHandler.sendMsg("Ошибка при создании комнаты: " + e.getMessage());
            return false;
        }
    }

    public boolean enterRoom(ClientHandler clientHandler, String roomName, String password, boolean fromAuthentication) {
        int userId = getUserIdByUsername(clientHandler.getUsername());
        if (userId == -1) {
            clientHandler.sendMsg("Ошибка: не удалось определить ID пользователя.");
            return false;
        }
        Integer roomId = getRoomIdByName(roomName);
        if (roomId == null) {
            clientHandler.sendMsg("Комната '" + roomName + "' не найдена.");
            return false;
        }
        String dbPassword = getRoomPassword(roomName);
        if (dbPassword != null && (password == null || !dbPassword.equals(password))) {
            clientHandler.sendMsg("Неверный пароль для комнаты '" + roomName + "'.");
            return false;
        }
        if (isUserInRoom(userId, roomId)) {
            if (!fromAuthentication) {
                clientHandler.sendMsg("Вы уже находитесь в комнате '" + roomName + "'.");
            }
            return true;
        }
        leaveCurrentRoom(clientHandler);
        try (PreparedStatement ps = connection.prepareStatement(ENTER_ROOM)) {
            ps.setInt(1, roomId);
            ps.setInt(2, userId);
            ps.executeUpdate();
            updateLastActivity(roomId);
            clientHandler.setCurrentRoom(roomId);
            updateLastRoomName(userId, roomName);
            if (!fromAuthentication) {
                clientHandler.sendMsg("Вы вошли в комнату '" + roomName + "'.");
                loadChatHistory(clientHandler, roomName);
            }
            server.broadcastRoomMessage("В комнату вошел '" + clientHandler.getUsername() + "'.", roomId, null);
            return true;
        } catch (SQLException e) {
            clientHandler.sendMsg("Ошибка при входе в комнату: " + e.getMessage());
            return false;
        }
    }
    @Override
    public boolean deleteRoom(ClientHandler clientHandler, String roomName) {
        int userId = getUserIdByUsername(clientHandler.getUsername());
        if (userId == -1) {
            clientHandler.sendMsg("Ошибка: не удалось определить ID пользователя.");
            return false;
        }
        Integer roomId = getRoomIdByName(roomName);
        if (roomId == null) {
            clientHandler.sendMsg("Комната '" + roomName + "' не найдена.");
            return false;
        }
        if (!isRoomOwner(clientHandler, roomName)) {
            clientHandler.sendMsg("У вас нет прав на удаление этой комнаты.");
            return false;
        }
        try {
            List<ClientHandler> usersInRoom = getUsersInRoom(roomId);

            try (PreparedStatement ps = connection.prepareStatement(DELETE_ROOM_USERS)) {
                ps.setInt(1, roomId);
                ps.executeUpdate();
            }

            try (PreparedStatement ps = connection.prepareStatement(DELETE_ROOM_MESSAGES)) {
                ps.setInt(1, roomId);
                ps.executeUpdate();
            }
            try (PreparedStatement ps = connection.prepareStatement(DELETE_ROOM_INVITES)) {
                ps.setInt(1, roomId);
                ps.executeUpdate();
            }

            try (PreparedStatement ps = connection.prepareStatement(DELETE_ROOM)) {
                ps.setString(1, roomName);
                ps.executeUpdate();
            }
            String deletionMessage = "Комната " + roomName + " была удалена пользователем " + clientHandler.getUsername();
            Set<Integer> affectedUserIds = new HashSet<>();
            for (ClientHandler affectedClient : usersInRoom) {
                affectedUserIds.add(getUserIdByUsername(affectedClient.getUsername()));
            }
            affectedUserIds.addAll(getInvitedUserIds(roomId));
            for (Integer affectedUserId : affectedUserIds) {
                ClientHandler affectedClient = server.findClientByUserId(affectedUserId);
                if (affectedClient != null) {
                    affectedClient.sendMsg(deletionMessage);
                    leaveCurrentRoom(affectedClient);
                    affectedClient.sendMsg("Вы вошли в комнату 'Общая комната'.");
                    loadChatHistory(affectedClient, "Общая комната");
                    enterCommonRoom(affectedClient);
                }
            }
            return true;
        } catch (SQLException e) {
            clientHandler.sendMsg("Ошибка при удалении комнаты: " + e.getMessage());
            return false;
        }
    }

    private boolean roomExists(String roomName) {
        try (PreparedStatement ps = connection.prepareStatement(ROOM_EXISTS_QUERY)) {
            ps.setString(1, roomName);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            return false;
        } catch (SQLException e) {
            System.err.println("Ошибка при проверке существования комнаты: " + e.getMessage());
            return true;
        }
    }

    private int getUserRoomCount(int ownerId) {
        try (PreparedStatement ps = connection.prepareStatement(GET_USER_ROOM_COUNT)) {
            ps.setInt(1, ownerId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
            return 0;
        } catch (SQLException e) {
            System.err.println("Ошибка при получении количества комнат пользователя: " + e.getMessage());
            return 0;
        }
    }

    public boolean enterRoom(ClientHandler clientHandler, String roomName, String password) {
        return enterRoom(clientHandler, roomName, password, false);
    }
    private boolean enterCommonRoom(ClientHandler clientHandler) {
        return enterRoom(clientHandler, COMMON_ROOM_NAME, null, true);
    }
    public void leaveCurrentRoom(ClientHandler clientHandler) {
        Integer currentRoomId = clientHandler.getCurrentRoom();
        if (currentRoomId != null) {
            int userId = getUserIdByUsername(clientHandler.getUsername());
            String roomName = getRoomNameById(currentRoomId);
            if (userId == -1) {
                clientHandler.sendMsg("Ошибка: не удалось определить ID пользователя.");
                return;
            }
            try (PreparedStatement ps = connection.prepareStatement(LEAVE_ROOM)) {
                ps.setInt(1, currentRoomId);
                ps.setInt(2, userId);
                ps.executeUpdate();
                if (roomName != null) {
                    server.broadcastRoomMessage("Из комнаты вышел '" + clientHandler.getUsername() + "'.", currentRoomId, null);
                    clientHandler.sendMsg("Вы покинули комнату '" + roomName + "'.");
                }

                clientHandler.setCurrentRoom(null);
                updateLastRoomName(userId, null);
            } catch (SQLException e) {
                System.err.println("Ошибка при выходе из комнаты: " + e.getMessage());
            }
        }
    }

    public void handleChatMessage(ClientHandler clientHandler, String message) {
        Integer roomId = clientHandler.getCurrentRoom();
        if (roomId == null) {
            clientHandler.sendMsg("Вы не находитесь ни в одной комнате. Войдите в комнату, чтобы отправлять сообщения.");
            return;
        }
        String sender = clientHandler.getUsername();
        try (PreparedStatement ps = connection.prepareStatement(SAVE_MESSAGE)) {
            ps.setInt(1, roomId);
            ps.setString(2, sender);
            ps.setString(3, message);
            ps.setTimestamp(4, Timestamp.valueOf(LocalDateTime.now()));
            ps.executeUpdate();
            updateLastActivity(roomId);
            String formattedMessage = "[" + LocalDateTime.now().format(ClientHandler.timeFormatter) + "] " + sender + ": " + message;
            server.broadcastRoomMessage(formattedMessage, roomId, clientHandler);
            clientHandler.sendMsg(formattedMessage);
        } catch (SQLException e) {
            clientHandler.sendMsg("Ошибка при сохранении сообщения: " + e.getMessage());
        }
    }

    public List<ChatMessage> getChatMessages(int roomId) {
        List<ChatMessage> messages = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(GET_MESSAGES)) {
            ps.setInt(1, roomId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String sender = rs.getString("sender");
                String message = rs.getString("message");
                LocalDateTime timestamp = rs.getTimestamp("timestamp").toLocalDateTime();
                messages.add(new ChatMessage(sender, message, timestamp));
            }
        } catch (SQLException e) {
            System.err.println("Ошибка при загрузке истории чата: " + e.getMessage());
        }
        return messages;
    }

    public String getRoomNameById(Integer roomId) {
        try (PreparedStatement ps = connection.prepareStatement(GET_ROOM_NAME_BY_ID)) {
            ps.setInt(1, roomId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getString("name");
            }
            return null;
        } catch (SQLException e) {
            System.err.println("Ошибка при получении имени комнаты по ID: " + e.getMessage());
            return null;
        }
    }

    public void loadChatHistory(ClientHandler clientHandler, String roomName) {
        Integer roomId = getRoomIdByName(roomName);
        if (roomId == null) {
            clientHandler.sendMsg("Комната '" + roomName + "' не найдена.");
            return;
        }
        List<ChatMessage> chatHistory = getChatMessages(roomId);
        for (ChatMessage chatMessage : chatHistory) {
            String formattedMessage = "[" + chatMessage.getTimestamp().format(ClientHandler.timeFormatter) + "] " + chatMessage.getSender() + ": " + chatMessage.getMessage();
            clientHandler.sendMsg(formattedMessage);
        }
    }

    public Integer getRoomIdByName(String roomName) {
        try (PreparedStatement ps = connection.prepareStatement(GET_ROOM_ID_BY_NAME)) {
            ps.setString(1, roomName);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int roomId = rs.getInt("id");
                return roomId;
            }
            return null;
        } catch (SQLException e) {
            System.err.println("getRoomIdByName: Ошибка при получении ID комнаты по имени: " + e.getMessage());
            return null;
        }
    }

    private String getRoomPassword(String roomName) {
        try (PreparedStatement ps = connection.prepareStatement(GET_ROOM_PASSWORD)) {
            ps.setString(1, roomName);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getString("password");
            }
            return null;
        } catch (SQLException e) {
            System.err.println("Ошибка при получении пароля комнаты: " + e.getMessage());
            return null;
        }
    }

    private boolean isUserInRoom(int userId, int roomId) {
        try (PreparedStatement ps = connection.prepareStatement(CHECK_ROOM_USER)) {
            ps.setInt(1, roomId);
            ps.setInt(2, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            return false;
        } catch (SQLException e) {
            System.err.println("Ошибка при проверке пользователя в комнате: " + e.getMessage());
            return false;
        }
    }

    private void cleanupOldRooms() {
        try (Statement statement = connection.createStatement()) {
            int deletedRooms = statement.executeUpdate(CLEANUP_OLD_ROOMS);
            System.out.println("Удалено старых комнат: " + deletedRooms);
        } catch (SQLException e) {
            System.err.println("Ошибка при очистке старых комнат: " + e.getMessage());
        }
    }

    private void updateLastActivity(int roomId) {
        try (PreparedStatement ps = connection.prepareStatement(UPDATE_LAST_ACTIVITY)) {
            ps.setInt(1, roomId);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Ошибка при обновлении времени последней активности комнаты: " + e.getMessage());
        }
    }

    public String getLastRoomName(int userId) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = connection.prepareStatement(GET_LAST_ROOM_NAME);
            ps.setInt(1, userId);
            rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getString("room_name");
            }
            return "Общая комната";
        } catch (SQLException e) {
            System.err.println("Ошибка при получении имени последней комнаты: " + e.getMessage());
            return "Общая комната";
        } finally {
            close(rs);
            close(ps);
        }
    }

    public void updateLastRoomName(int userId, String roomName) {
        PreparedStatement ps = null;
        try {
            if (roomName != null) {
                ps = connection.prepareStatement(UPDATE_LAST_ROOM_NAME);
                ps.setInt(1, userId);
                ps.setString(2, roomName);
                ps.setString(3, roomName);
            } else {
                ps = connection.prepareStatement("DELETE FROM last_rooms WHERE user_id = ?");
                ps.setInt(1, userId);
            }
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Ошибка при обновлении имени последней комнаты: " + e.getMessage());
        } finally {
            close(ps);
        }
    }

    public boolean inviteUserToRoom(ClientHandler inviter, String roomName, String inviteeUsername) {
        Integer roomId = getRoomIdByName(roomName);
        int inviterId = getUserIdByUsername(inviter.getUsername());
        int inviteeId = getUserIdByUsername(inviteeUsername);
        if (roomId == null) {
            inviter.sendMsg("Комната '" + roomName + "' не найдена.");
            return false;
        }
        if (inviterId == -1 || inviteeId == -1) {
            inviter.sendMsg("Один из пользователей не найден.");
            return false;
        }
        if (!canInvite(inviterId, roomId)) {
            inviter.sendMsg("У вас нет прав для приглашения в эту комнату.");
            return false;
        }
        if (isUserInRoom(inviteeId, roomId)) {
            inviter.sendMsg("Пользователь '" + inviteeUsername + "' уже находится в комнате.");
            return false;
        }
        String roomPassword = getRoomPassword(roomName);
        try (PreparedStatement ps = connection.prepareStatement(ADD_INVITE)) {
            ps.setInt(1, roomId);
            ps.setInt(2, inviteeId);
            ps.setInt(3, inviterId);
            ps.executeUpdate();

            ClientHandler invitee = server.findClientByUsername(inviteeUsername);
            if (invitee != null) {
                String inviteMessage = "Вас пригласили в комнату '" + roomName + "' пользователем '" + inviter.getUsername() + "'. Используйте /enter " + roomName;
                if (roomPassword != null && !roomPassword.isEmpty()) {
                    inviteMessage += " " + roomPassword;
                }
                inviteMessage += " для входа.";
                invitee.sendMsg(inviteMessage);
            }
            return true;
        } catch (SQLException e) {
            if (e.getMessage().contains("нарушает ограничение уникальности")) {
                inviter.sendMsg("Приглашение пользователю '" + inviteeUsername + "' в комнату '" + roomName + "' уже было отправлено.");
            } else {
                inviter.sendMsg("Ошибка при отправке приглашения: " + e.getMessage());
            }
            return false;
        }
    }

    private boolean canInvite(int inviterId, int roomId) {
        try (PreparedStatement ps = connection.prepareStatement(CAN_INVITE)) {
            ps.setInt(1, roomId);
            ps.setInt(2, inviterId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            return false;
        } catch (SQLException e) {
            System.err.println("Ошибка при проверке прав на приглашение: " + e.getMessage());
            return false;
        }
    }

    public List<String> getInvitesForUser(ClientHandler clientHandler) {
        List<String> inviteList = new ArrayList<>();
        int userId = getUserIdByUsername(clientHandler.getUsername());
        if (userId == -1) {
            clientHandler.sendMsg("Ошибка: не удалось определить ID пользователя.");
            return inviteList;
        }
        try (PreparedStatement ps = connection.prepareStatement(GET_INVITES_FOR_USER)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            int numRoom = 0;
            while (rs.next()) {
                numRoom++;
                String roomName = rs.getString("name");
                String password = rs.getString("password");
                String inviteInfo = numRoom + ". Комната: " + roomName;
                if (password != null && !password.isEmpty()) {
                    inviteInfo += ", Пароль: " + password;
                }
                inviteList.add(inviteInfo);
            }
        } catch (SQLException e) {
            System.err.println("Ошибка при получении списка приглашений пользователя: " + e.getMessage());
        }
        return inviteList;
    }

    public void removeInvite(String roomName, ClientHandler clientHandler) {
        Integer roomId = getRoomIdByName(roomName);
        int userId = getUserIdByUsername(clientHandler.getUsername());
        if (roomId == null) {
            clientHandler.sendMsg("Комната '" + roomName + "' не найдена.");
            return;
        }
        if (userId == -1) {
            clientHandler.sendMsg("Ошибка: не удалось определить ID пользователя.");
            return;
        }
        try (PreparedStatement ps = connection.prepareStatement(REMOVE_INVITE)) {
            ps.setInt(1, roomId);
            ps.setInt(2, userId);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Ошибка при удалении приглашения: " + e.getMessage());
        }
    }

    private List<Integer> getInvitedUserIds(int roomId) {
        List<Integer> userIds = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(GET_INVITED_USERS)) {
            ps.setInt(1, roomId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    userIds.add(rs.getInt("user_id"));
                }
            }
        } catch (SQLException e) {
            System.err.println("Ошибка при получении списка приглашенных пользователей: " + e.getMessage());
        }
        return userIds;
    }

    private List<ClientHandler> getUsersInRoom(int roomId) {
        List<ClientHandler> usersInRoom = new ArrayList<>();
        for (ClientHandler client : server.getClients()) {
            if (client.getCurrentRoom() != null && client.getCurrentRoom().equals(roomId)) {
                usersInRoom.add(client);
            }
        }
        return usersInRoom;
    }

    public boolean isRoomOwner(ClientHandler clientHandler, String roomName) {
        Integer roomId = getRoomIdByName(roomName);
        if (roomId == null) {
            return false;
        }
        try (PreparedStatement ps = connection.prepareStatement(GET_ROOM_OWNER_ID)) {
            ps.setInt(1, roomId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int ownerId = rs.getInt("owner_id");
                    int userId = getUserIdByUsername(clientHandler.getUsername());
                    return ownerId == userId;
                }
                return false;
            }
        } catch (SQLException e) {
            System.err.println("Ошибка при проверке владельца комнаты: " + e.getMessage());
            return false;
        }
    }

    public List<String> getAllPublicRooms() {
        List<String> roomList = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(GET_ALL_PUBLIC_ROOMS);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                roomList.add(rs.getString("name"));
            }
        } catch (SQLException e) {
            System.err.println("Ошибка при получении списка публичных комнат: " + e.getMessage());
        }
        return roomList;
    }

    public List<String> getUserCreatedRooms(int userId) {
        List<String> roomList = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(GET_USER_CREATED_ROOMS)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                int numRoom = 0;
                while (rs.next()) {
                    numRoom++;
                    String roomName = rs.getString("name");
                    String password = rs.getString("password");
                    String roomInfo = numRoom + ".Комната: " + roomName;
                    if (password != null && !password.isEmpty()) {
                        roomInfo += ", Пароль: " + password;
                    }
                    roomList.add(roomInfo);
                }
            }
        } catch (SQLException e) {
            System.err.println("Ошибка при получении списка комнат, созданных пользователем: " + e.getMessage());
        }
        return roomList;
    }

    public List<String> getUserInvitedRooms(int userId) {
        List<String> roomList = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(GET_USER_INVITED_ROOMS)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                int numRoom = 0;
                while (rs.next()) {
                    numRoom++;
                    String roomName = rs.getString("name");
                    String password = rs.getString("password");
                    String roomInfo = numRoom + ".Комната: " + roomName;
                    if (password != null && !password.isEmpty()) {
                        roomInfo += ", Пароль: " + password;
                    }
                    roomList.add(roomInfo);
                }
            }
        } catch (SQLException e) {
            System.err.println("Ошибка при получении списка комнат, в которые приглашен пользователь: " + e.getMessage());
        }
        return roomList;
    }

    private void close(AutoCloseable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (Exception e) {
                System.err.println("Ошибка при закрытии ресурса: " + e.getMessage());
            }
        }
    }

    public void close() {
        close(connection);
    }
}