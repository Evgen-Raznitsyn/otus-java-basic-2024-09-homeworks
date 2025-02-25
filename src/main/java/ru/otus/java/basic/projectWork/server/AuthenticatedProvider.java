package ru.otus.java.basic.projectWork.server;

import java.time.LocalDateTime;
import java.util.List;

public interface AuthenticatedProvider {
    void initialize();

    boolean authenticate(ClientHandler clientHandler, String login, String password);

    boolean registration(ClientHandler clientHandler, String login, String password, String username);

    boolean addAdmin(ClientHandler clientHandler, String newAdminName, ClientHandler newAdminHandler);

    boolean removeAdminRole(ClientHandler clientHandler, String usernameToRemove, ClientHandler messageAdmin);

    boolean changeUsername(ClientHandler clientHandler, String newNick);

    boolean addBan(String usernameToBan, LocalDateTime banEnd, String reason);


    String getUsernameByLogin(String login);

    BanInfo getBanInfo(String username);

    boolean removeBan(String usernameToUnban);

    void loadChatHistory(ClientHandler clientHandler, String roomName);

    boolean enterRoom(ClientHandler clientHandler, String roomName, String password);

    boolean inviteUserToRoom(ClientHandler clientHandler, String roomName, String inviteeUsername);

    List<String> getInvitesForUser(ClientHandler clientHandler);

    void removeInvite(String roomName, ClientHandler clientHandler);

    void handleChatMessage(ClientHandler clientHandler, String message);

    boolean createRoom(ClientHandler clientHandler, String roomName, String password);

    void leaveCurrentRoom(ClientHandler clientHandler);

    Integer getRoomIdByName(String roomName);

    String getLastRoomName(int userId);

    boolean deleteRoom(ClientHandler clientHandler, String roomName);

    boolean isRoomOwner(ClientHandler clientHandler, String roomName);

    String getRoomNameById(Integer roomId);

    List<String> getUserInvitedRooms(int userId);

    List<String> getUserCreatedRooms(int userId);

    List<String> getAllPublicRooms();

    int getUserIdByUsername(String username);
}