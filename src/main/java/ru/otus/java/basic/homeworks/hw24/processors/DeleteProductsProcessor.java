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
import java.util.List;
import java.util.NoSuchElementException;

public class DeleteProductsProcessor implements RequestProcessor {
    private static final Logger LOGGER = LogManager.getLogger(DeleteProductsProcessor.class);
    private ProductsService productsService;

    public DeleteProductsProcessor(ProductsService productsService) {
        this.productsService = productsService;
    }

    @Override
    public void execute(HttpRequest request, OutputStream output) throws IOException {
        try {
            String jsonResult = null;
            Gson gson = new Gson();
            if (request.containsParameter("id")) {
                Long id = Long.parseLong(request.getParameter("id"));
                productsService.deleteProductById(id);
                List<Product> products = productsService.getAllProducts();
                jsonResult = gson.toJson(products);
                LOGGER.info("Удаление продукта по ID - ОК");
            } else {
                productsService.deleteAllProducts();
                List<Product> products = productsService.getAllProducts();
                jsonResult = gson.toJson(products);
                LOGGER.info("Удаление всех продукта - ОК");
            }

            String response = "" +
                    "HTTP/1.1 200 OK\r\n" +
                    "Connect-Type: application/json\r\n" +
                    "\r\n" +
                    jsonResult;
            output.write(response.getBytes(StandardCharsets.UTF_8));
        } catch (NoSuchElementException e) {
            String response = "" +
                    "HTTP/1.1 200 OK\r\n" +
                    "Connect-Type: text/html\r\n" +
                    "\r\n" +
                    "<html><body><h1>Product not found!</h1></body></html>";
            output.write(response.getBytes(StandardCharsets.UTF_8));
            LOGGER.info("Удаление всех продукта - Продукт не найден по ID");
        }
    }
}
