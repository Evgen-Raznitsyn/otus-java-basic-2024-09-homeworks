package ru.otus.java.basic.homeworks.hw16.server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class Server {
    private int port;
    private List<ClientHandler> clients;
    private AuthenticatedProvider authenticatedProvider;

    public Server(int port) {
        this.port = port;
        clients = new CopyOnWriteArrayList<>();
        authenticatedProvider = new InMemoryAuthenticatedProvider(this);
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
        clients.add(clientHandler);
        String username = clientHandler.getUsername();
        broadcastMessage("В чат вошел: " + username);
    }

    public synchronized void unsubscribe(ClientHandler clientHandler) {
        clients.remove(clientHandler);
        broadcastMessage("Из чата вышел: " + clientHandler.getUsername());
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

    public boolean isUsernameUnique(String username) {
        for (ClientHandler clientHandler : clients) {
            if (clientHandler.getUsername().equals(username)) {
                return true;
            }
        }
        return false;
    }

    public void kickUser(String username) {
        for (ClientHandler client : clients) {
            if (client.getUsername().equals(username)) {
                client.sendMsg("/kickoff");
                break;
            }
        }
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
