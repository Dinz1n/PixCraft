package br.din.pixCraft.product;

import org.bukkit.inventory.ItemStack;

import java.util.List;

public final class Product {
    private final String productId;
    private final String name;
    private final Double price;
    private final boolean tax;
    private final List<String> reward;
    private final ItemStack icon;

    public Product(String productId, String name, Double price, boolean tax, List<String> reward, ItemStack icon) {
        this.productId = productId;
        this.name = name;
        this.price = price;
        this.tax = tax;
        this.reward = reward;
        this.icon = icon;
    }

    public String getProductId() {
        return productId;
    }

    public String getName() {
        return name;
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

    public ItemStack getIcon() {
        return icon;
    }
}