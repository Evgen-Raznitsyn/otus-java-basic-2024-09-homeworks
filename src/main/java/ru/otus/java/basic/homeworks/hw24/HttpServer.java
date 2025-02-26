package ru.otus.java.basic.homeworks.hw24;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HttpServer {
    private int port;
    private Dispatcher dispatcher;
    private ExecutorService executorService;
    private static final Logger LOGGER = LogManager.getLogger(HttpServer.class);

    public HttpServer(int port) {
        this.port = port;
        this.dispatcher = new Dispatcher();
        this.executorService = Executors.newFixedThreadPool(10);
    }

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            LOGGER.info("Сервер запущен на порту: {}", port);
            while (true) {
                Socket socket = serverSocket.accept();
                LOGGER.info("Подключился новый клиент");
                executorService.submit(() -> {
                    try {
                        byte[] buffer = new byte[8192];
                        int n = socket.getInputStream().read(buffer);
                        HttpRequest request = new HttpRequest(new String(buffer, 0, n));
                        request.info(true);
                        dispatcher.execute(request, socket.getOutputStream());
                        LOGGER.info("Обработка подключения клиента завершена");
                    } catch (IOException e) {
                        LOGGER.error("Ошибка при обработке подключения клиента: ", e);
                    } finally {
                        try {
                            socket.close();
                        } catch (IOException e) {
                            LOGGER.error("Ошибка при закрытии сокета: ", e);
                        }
                    }
                });
            }
        } catch (IOException e) {
            LOGGER.error("Ошибка при запуске сервера: ", e);
        } finally {
            executorService.shutdown();
        }
    }
}
