package com.example.group21.balancebasket;

import java.io.Serializable;

public class Product implements Serializable {
    String name;
    String price;

    public Product(String name, String price) {
        this.name = name;
        this.price = price;
    }

    public String getName() {
        return this.name;
    }

    public String getPrice() {
        return this.price;
    }
}
