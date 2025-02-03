package ru.otus.java.basic.homeworks.hw18.server;

import java.sql.*;
import java.util.*;

public class DatabaseAuthenticatedProvider implements AuthenticatedProvider {
    private static final String REGISTRATION_QUERY = "INSERT INTO users (login, password, username) VALUES (?, ?, ?)";
    private static final String AUTHENTICATION_QUERY = "SELECT password, id,username FROM users WHERE login = ?";
    private static final String GET_ROLE_QUERY = "SELECT r.name FROM roles r JOIN users_to_roles ur ON r.id = ur.role_id WHERE ur.user_id = ?";
    private static final String REMOVE_ADMIN_QUERY = "DELETE FROM users_to_roles " +
            "WHERE user_id = (SELECT id FROM users WHERE username = ?) " +
            "AND role_id = (SELECT id FROM roles WHERE name = 'admin')";
    private static final String GET_USER_ID_QUERY = "SELECT id FROM users WHERE username = ?";
    private static final String SET_ROLE_FOR_USER_QUERY = "INSERT INTO users_to_roles (user_id, role_id) VALUES (?, ?)";
    private static final String ROLE_EXISTS_QUERY = "SELECT COUNT(*) FROM users_to_roles WHERE user_id = ? AND role_id = (SELECT id FROM roles WHERE name = ?)";
    private static final String IS_USERNAME_TAKEN_QUERY = "SELECT COUNT(*) FROM users WHERE login = ? OR username = ?";
    private Server server;
    private final Connection connection;

    public DatabaseAuthenticatedProvider(Server server) throws SQLException {
        this.server = server;
        this.connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/postgres", "postgres", "admin");
    }

    @Override
    public void initialize() {
        System.out.println("Инициализация DatabaseAuthenticatedProvider");
    }

    @Override
    public boolean authenticate(ClientHandler clientHandler, String login, String password) {
        String dbPassword = null;
        int userId = -1;
        String username = null;
        try (PreparedStatement ps = connection.prepareStatement(AUTHENTICATION_QUERY)) {
            ps.setString(1, login);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    dbPassword = rs.getString("password");
                    userId = rs.getInt("id");
                    username = rs.getString("username");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
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
            clientHandler.sendMsg("Роль администратора у пользователя " + username + " успешно удалена.");
            int affectedRows = pstmt.executeUpdate();
            if (clientToRemove != null) {
                clientToRemove.sendMsg("\nУведомляем вас, что ваша роль администратора была удалена!\n" +
                        "Теперь вы обладаете правами обычного пользователя и не сможете выполнять административные действия.\n");
            }
            return affectedRows > 0;
        } catch (SQLException e) {
            clientHandler.sendMsg("Произошла ошибка при удалении роли администратора. Пожалуйста, попробуйте позже.");
            return false;
        }
    }

    private int getUserIdByUsername(String username) {
        try (PreparedStatement ps = connection.prepareStatement(GET_USER_ID_QUERY)) {
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
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
}