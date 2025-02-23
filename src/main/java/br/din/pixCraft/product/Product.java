package br.din.pixCraft.product;

import java.util.List;

public final class Product {
    private final String displayName;
    private final Double price;
    private final boolean tax;
    private final List<String> reward;

    public Product(String displayName, Double price, boolean tax, List<String> reward) {
        this.displayName = displayName;
        this.price = price;
        this.tax = tax;
        this.reward = reward;
    }

    public String getDisplayName() {
        return displayName;
    }

    public Double getPrice() {
        return price;
    }

    public boolean isTax() {
        return tax;
    }

    public List<String> getRewardCommands() {
        return reward;
    }
}