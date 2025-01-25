package ru.otus.java.basic.homeworks.hw15.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class Server {
    private int port;
    private CopyOnWriteArrayList<ClientHandler> clients;

    public Server(int port) {
        this.port = port;
        clients = new CopyOnWriteArrayList<>();
    }

    public void start(){
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Сервер запущен на порту: " + port);
            while (true) {
                Socket socket = serverSocket.accept();
              new ClientHandler(socket, this);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized void subscribe(ClientHandler clientHandler){
        clients.add(clientHandler);
        broadcastMessage("В чат вошел: " + clientHandler.getUsername(),clientHandler);
    }

    public synchronized void unsubscribe(ClientHandler clientHandler){
        clients.remove(clientHandler);
        broadcastMessage("Из чата вышел: "+ clientHandler.getUsername(),clientHandler);
    }

    public void broadcastMessage(String message, ClientHandler sender){
        for (ClientHandler client : clients) {
            if (client != sender) {
                client.sendMsg(message);
            }
        }
    }

    public List<String> getActiveUsernames() {
        List<String> activeUsernames = new ArrayList<>();
        for (ClientHandler client : clients) {
            activeUsernames.add(client.getUsername());
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
            if (clientHandler.getUsername() != null && clientHandler.getUsername().equalsIgnoreCase(username)) {
                return false;
            }
        }
        return true;
    }
}
