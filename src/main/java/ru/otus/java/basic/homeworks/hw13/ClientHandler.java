package ru.otus.java.basic.homeworks.hw13;

import java.io.*;
import java.net.*;

public class ClientHandler implements Runnable {
    private final Socket clientSocket;

    public ClientHandler(Socket socket) {
        this.clientSocket = socket;
    }

    @Override
    public void run() {
        try (DataInputStream inputStream = new DataInputStream(clientSocket.getInputStream());
             DataOutputStream outputStream = new DataOutputStream(clientSocket.getOutputStream())) {

            String availableOperations = "Доступные операции: +, -, *, /, ^ (возведение в степень), sqrt (извлечение корня)";
            outputStream.writeUTF(availableOperations);
            outputStream.flush();

            while (true) {
                String userInput = inputStream.readUTF();
                String result = CalculatorServer.processRequest(userInput);
                outputStream.writeUTF(result);
                outputStream.flush();
            }
        } catch (IOException e) {
            System.out.println("Клиент отключен: " + clientSocket.getPort());
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}