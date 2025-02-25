package ru.otus.java.basic.projectWork.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


public class Server {
    private int port;
    private List<ClientHandler> clients;
    private DatabaseAuthenticatedProvider authenticatedProvider;
    private Set<String> activeUsernames;
    private final ExecutorService executorService;
    private boolean isRunning = true;
    private ServerSocket serverSocket;


    public Server(int port) {
        this.port = port;
        clients = new CopyOnWriteArrayList<>();
        activeUsernames = new HashSet<>();
        this.executorService = Executors.newFixedThreadPool(10);

        try {
            authenticatedProvider = new DatabaseAuthenticatedProvider(this);
        } catch (SQLException e) {
            System.err.println("Ошибка подключения к базе данных: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            this.serverSocket = serverSocket;
            System.out.println("Сервер запущен на порту: " + port);
            authenticatedProvider.initialize();
            while (isRunning) {
                try {
                    Socket socket = serverSocket.accept();
                    executorService.execute(() -> {
                        try {
                            new ClientHandler(socket, this);
                        } catch (IOException e) {
                            System.err.println("Ошибка при обработке клиента: " + e.getMessage());
                            e.printStackTrace();
                        }
                    });
                } catch (SocketException e) {
                    if (isRunning) {
                        System.out.println("ServerSocket был закрыт.");
                    }
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            disconnectAllClients();
            executorService.shutdown();
            try {
                if (!executorService.awaitTermination(5, TimeUnit.SECONDS)) {
                    executorService.shutdownNow();
                }
            } catch (InterruptedException e) {
                executorService.shutdownNow();
                Thread.currentThread().interrupt();
            }
            authenticatedProvider.close();
            System.out.println("Сервер остановлен.");
        }
    }

    public synchronized void subscribe(ClientHandler clientHandler) {
        String username = clientHandler.getUsername();
        clients.add(clientHandler);
        addActiveUser(username);
        Integer roomId = clientHandler.getCurrentRoom();
        if (roomId != null) {
            broadcastRoomMessage("В комнату вошел '" + clientHandler.getUsername() + "'.", roomId, null);
        }
    }

    public synchronized void unsubscribe(ClientHandler clientHandler) {
        String username = clientHandler.getUsername();
        clients.remove(clientHandler);
        removeActiveUser(username);
        Integer roomId = clientHandler.getCurrentRoom();
        if (roomId != null) {
            broadcastRoomMessage("Из комнаты вышел '" + clientHandler.getUsername() + "'.", roomId, null);
        }
    }

    public void broadcastMessage(String message) {
        for (ClientHandler clientHandler : clients) {
            clientHandler.sendMsg(message);
        }
    }

    public void broadcastRoomMessage(String message, int roomId, ClientHandler sender) {
        for (ClientHandler clientHandler : clients) {
            if (clientHandler != sender && clientHandler.getCurrentRoom() != null && clientHandler.getCurrentRoom().equals(roomId)) {
                clientHandler.sendMsg(message);
            }
        }
    }

    public ClientHandler findClientByUserId(int userId) {
        for (ClientHandler clientHandler : clients) {
            if (authenticatedProvider.getUserIdByUsername(clientHandler.getUsername()) == userId) {
                return clientHandler;
            }
        }
        return null;
    }

    public List<String> getActiveUsernamesInRoom(Integer roomId) {
        List<String> activeUsernamesInRoom = new ArrayList<>();
        for (ClientHandler clientHandler : clients) {
            if (clientHandler.getCurrentRoom() != null && clientHandler.getCurrentRoom().equals(roomId)) {
                activeUsernamesInRoom.add(clientHandler.getUsername());
            }
        }
        return activeUsernamesInRoom;
    }

    public ClientHandler findClientByUsername(String username) {
        for (ClientHandler clientHandler : clients) {
            if (clientHandler.getUsername().equalsIgnoreCase(username)) {
                return clientHandler;
            }
        }
        return null;
    }

    public synchronized List<ClientHandler> getClients() {
        return new ArrayList<>(clients);
    }

    public String kickUser(String username) {
        for (ClientHandler clientHandler : clients) {
            if (clientHandler.getUsername().equals(username)) {
                clientHandler.sendMsg("/kickoff");
                return username;
            }
        }
        return null;
    }

    public synchronized boolean isUserActive(String username) {
        return activeUsernames.contains(username);
    }

    public synchronized void addActiveUser(String username) {
        activeUsernames.add(username);
    }

    public synchronized void removeActiveUser(String username) {
        activeUsernames.remove(username);
    }

    public AuthenticatedProvider getAuthenticatedProvider() {
        return authenticatedProvider;
    }

    public void logout(ClientHandler clientHandler) {
        String username = clientHandler.getUsername();
        if (username != null) {
            unsubscribe(clientHandler);
            clientHandler.setCurrentRoom(null);
        }
    }

    public void stop() {
        System.out.println("Остановка сервера...");
        isRunning = false;
        try {
            if (serverSocket != null) {
                serverSocket.close();
            }
        } catch (IOException e) {
            System.err.println("Ошибка при закрытии serverSocket: " + e.getMessage());
        }
    }

    private void disconnectAllClients() {
        System.out.println("Отключение всех клиентов...");
        for (ClientHandler clientHandler : clients) {
            clientHandler.sendMsg("Сервер выключается. Подключение будет закрыто.");
            clientHandler.disconnect();
        }
        clients.clear();
        activeUsernames.clear();
    }
}
