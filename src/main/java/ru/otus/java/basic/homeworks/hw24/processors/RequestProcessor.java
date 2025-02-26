package ru.otus.java.basic.homeworks.hw24.processors;

import ru.otus.java.basic.homeworks.hw24.HttpRequest;

import java.io.IOException;
import java.io.OutputStream;

public interface RequestProcessor {
    void execute(HttpRequest request, OutputStream output) throws IOException;
}
