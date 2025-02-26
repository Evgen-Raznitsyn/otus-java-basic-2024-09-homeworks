package ru.otus.java.basic.homeworks.hw24;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.otus.java.basic.homeworks.hw24.application.*;
import ru.otus.java.basic.homeworks.hw24.processors.*;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

public class Dispatcher {
    private static final Logger LOGGER = LogManager.getLogger(Dispatcher.class);
    private Map<String, RequestProcessor> router;
    private Default400Processor default400Processor;
    private Default404Processor default404Processor;
    private Default500Processor default500Processor;

    public Dispatcher() {
        ProductsService productsService = new ProductsService();
        this.router = new HashMap<>();
        this.router.put("GET /calc", new CalculatorProcessor());
        this.router.put("GET /welcome", new WelcomeProcessor());
        this.router.put("GET /products", new GetProductsProcessor(productsService));
        this.router.put("POST /products", new CreateProductProcessor(productsService));
        this.router.put("DELETE /products", new DeleteProductsProcessor(productsService));
        this.router.put("PUT /products", new UpdateProductProcessor(productsService));
        this.default400Processor = new Default400Processor();
        this.default404Processor = new Default404Processor();
        this.default500Processor = new Default500Processor();
    }

    public void execute(HttpRequest request, OutputStream output) throws IOException {
        try {
            if (!router.containsKey(request.getRoutingKey())) {
                default404Processor.execute(request, output);
                return;
            }
            router.get(request.getRoutingKey()).execute(request, output);
        } catch (BadRequestException e) {
            LOGGER.error(e.getMessage());
            request.setErrorCause(e);
            default400Processor.execute(request, output);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            default500Processor.execute(request, output);
        }
    }
}
