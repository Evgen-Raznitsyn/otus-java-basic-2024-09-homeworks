package ru.otus.java.basic.homeworks.hw24.application;

import java.util.*;

public class ProductsService {
    private List<Product> products;

    public ProductsService() {
        this.products = new ArrayList<>(Arrays.asList(
                new Product(1L, "Milk"),
                new Product(2L, "Bread"),
                new Product(3L, "Cheese")
        ));
    }

    public List<Product> getAllProducts() {
        return Collections.unmodifiableList(products);
    }

    public Product getProductById(Long id) {
        return products.stream().filter(p -> p.getId().equals(id)).findFirst().get();
    }

    public void createNewProduct(Product product) {
        Long newId = products.stream().mapToLong(Product::getId).max().getAsLong() + 1;
        products.add(new Product(newId, product.getTitle()));
    }

    public void deleteProductById(Long id) {
        products.remove(getProductById(id));
    }

    public void deleteAllProducts() {
        products.clear();
    }

    public void updateProduct(Long id, String title) {
        Product product = getProductById(id);
        product.setTitle(title);
    }
}
