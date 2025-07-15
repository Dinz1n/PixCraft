package br.com.din.pixcraft.product;

import java.util.List;

public class Product {
    private final String id;
    private final String name;
    private final double price;
    private final List<String> reward; // Comandos que serão executados quando o produto for comprado

    public Product(String id, String name, double price, List<String> reward) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.reward = reward;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public List<String> getReward() {
        return reward;
    }
}