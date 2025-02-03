package ru.otus.java.basic.homeworks.hw18.server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.sql.SQLException;

public class Server {
    private int port;
    private List<ClientHandler> clients;
    private DatabaseAuthenticatedProvider authenticatedProvider;
    private Set<String> activeUsernames;

    public Server(int port) {
        this.port = port;
        clients = new CopyOnWriteArrayList<>();
        activeUsernames = new HashSet<>();
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
            System.out.println("Сервер запущен на порту: " + port);
            authenticatedProvider.initialize();
            while (true) {
                Socket socket = serverSocket.accept();
                new ClientHandler(socket, this);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized void subscribe(ClientHandler clientHandler) {
        String username = clientHandler.getUsername();
        clients.add(clientHandler);
        broadcastMessage("В чат вошел: " + username);
    }

    public synchronized void unsubscribe(ClientHandler clientHandler) {
        String username = clientHandler.getUsername();
        clients.remove(clientHandler);
        broadcastMessage("Из чата вышел: " + clientHandler.getUsername());
        removeActiveUser(username);
    }

    public void broadcastMessage(String message) {
        for (ClientHandler clientHandler : clients) {
            clientHandler.sendMsg(message);
        }
    }

    public List<String> getActiveUsernames() {
        List<String> activeUsernames = new ArrayList<>();
        for (ClientHandler clientHandler : clients) {
            activeUsernames.add(clientHandler.getUsername());
        }
        return activeUsernames;
    }

    public ClientHandler findClientByUsername(String username) {
        for (ClientHandler clientHandler : clients) {
            if (clientHandler.getUsername().equalsIgnoreCase(username)) {
                return clientHandler;
            }
        }
        return null;
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

    public void logout(ClientHandler clientHandler) {
        String username = clientHandler.getUsername();
        if (clientHandler.getUsername().equals(username)) {
            unsubscribe(clientHandler);
        }
    }

    public AuthenticatedProvider getAuthenticatedProvider() {
        return authenticatedProvider;
    }

}
