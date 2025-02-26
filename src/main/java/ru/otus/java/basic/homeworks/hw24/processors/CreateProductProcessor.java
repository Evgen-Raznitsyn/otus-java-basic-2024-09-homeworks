package ru.otus.java.basic.homeworks.hw24.processors;

import com.google.gson.Gson;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.otus.java.basic.homeworks.hw24.HttpRequest;
import ru.otus.java.basic.homeworks.hw24.application.Product;
import ru.otus.java.basic.homeworks.hw24.application.ProductsService;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class CreateProductProcessor implements RequestProcessor {
    private static final Logger LOGGER = LogManager.getLogger(CreateProductProcessor.class);
    private ProductsService productsService;

    public CreateProductProcessor(ProductsService productsService) {
        this.productsService = productsService;
    }

    @Override
    public void execute(HttpRequest request, OutputStream output) throws IOException {
        Gson gson = new Gson();
        Product newProduct = gson.fromJson(request.getBody(), Product.class);
        productsService.createNewProduct(newProduct);
        LOGGER.info("Создание продукта - ОК: {}", newProduct.toString());

        String response = "" +
                "HTTP/1.1 201 Created\r\n" +
                "Content-Type: text/html\r\n" +
                "\r\n";
        output.write(response.getBytes(StandardCharsets.UTF_8));
    }
}
