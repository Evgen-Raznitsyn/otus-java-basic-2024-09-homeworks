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

public class GetProductsProcessor implements RequestProcessor {
    private static final Logger LOGGER = LogManager.getLogger(GetProductsProcessor.class);
    private ProductsService productsService;

    public GetProductsProcessor(ProductsService productsService) {
        this.productsService = productsService;
    }

    @Override
    public void execute(HttpRequest request, OutputStream output) throws IOException {
        String jsonResult = null;
        Gson gson = new Gson();
        if (request.containsParameter("id")) {
            Long id = Long.parseLong(request.getParameter("id"));
            Product product = productsService.getProductById(id);
            jsonResult = gson.toJson(product);
            LOGGER.info("Получение продукта по ID - ОК");
        } else {
            List<Product> products = productsService.getAllProducts();
            jsonResult = gson.toJson(products);
            LOGGER.info("Получение всех продуктов - ОК");
        }
        String response = "" +
                "HTTP/1.1 200 OK\r\n" +
                "Content-Type: application/json\r\n" +
                "\r\n" +
                jsonResult;
        output.write(response.getBytes(StandardCharsets.UTF_8));
    }
}
