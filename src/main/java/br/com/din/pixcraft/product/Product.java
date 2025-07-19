package br.com.din.pixcraft.product;

import org.bukkit.inventory.ItemStack;

import java.util.List;

public class Product {
    private final String id;
    private final String name;
    private final double price;
    private final List<String> reward;
    private final ItemStack icon;

    public Product(String id, String name, double price, List<String> reward, ItemStack icon) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.reward = reward;
        this.icon = icon;
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

    public ItemStack getIcon() {
        return icon;
    }
}