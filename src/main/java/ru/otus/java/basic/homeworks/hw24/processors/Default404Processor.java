package ru.otus.java.basic.homeworks.hw24.processors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.otus.java.basic.homeworks.hw24.HttpRequest;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class Default404Processor implements RequestProcessor {
    private static final Logger LOGGER = LogManager.getLogger(Default404Processor.class);
    @Override
    public void execute(HttpRequest request, OutputStream output) throws IOException {
        String response = "" +
                "HTTP/1.1 404 Not Found\r\n" +
                "Content-Type: text/html\r\n" +
                "\r\n" +
                "<html><body><h1>Page Not Found</h1></body></html>";
        output.write(response.getBytes(StandardCharsets.UTF_8));
        LOGGER.error("Страница не найдена");
    }
}
