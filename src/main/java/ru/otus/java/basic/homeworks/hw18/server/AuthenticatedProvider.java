package ru.otus.java.basic.homeworks.hw18.server;

public interface AuthenticatedProvider {
    void initialize();
    boolean authenticate(ClientHandler clientHandler, String login, String password);
    boolean registration(ClientHandler clientHandler, String login, String password, String username);
    boolean addAdmin(ClientHandler clientHandler, String newAdminName,ClientHandler newAdminHandler);

    boolean removeAdminRole(ClientHandler clientHandler, String usernameToRemove,ClientHandler messageAdmin);
}
