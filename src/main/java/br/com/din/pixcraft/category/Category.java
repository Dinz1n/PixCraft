package br.com.din.pixcraft.category;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class Category {
    private final String id;
    private final String title;
    private final Inventory inventory;
    private final ItemStack icon;

    public Category(String id, String title, Inventory inventory, ItemStack icon) {
        this.id = id;
        this.title = title;
        this.inventory = inventory;
        this.icon = icon;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public Inventory getInventory() {
        return inventory;
    }

    public ItemStack getIcon() {
        return icon;
    }
}
