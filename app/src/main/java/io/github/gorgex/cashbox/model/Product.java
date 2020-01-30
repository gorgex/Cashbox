package io.github.gorgex.cashbox.model;

public class Product {
    private String name, quantity;

    public Product(String name, String quantity) {
        this.name = name;
        this.quantity = quantity;
    }

    public String getName() {
        return name;
    }

    public String getQuantity() {
        return quantity;
    }
}
