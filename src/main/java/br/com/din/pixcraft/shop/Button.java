package br.com.din.pixcraft.shop;

import org.bukkit.inventory.ItemStack;

public class Button {
    private final ButtonType type;
    private final String target;
    private final ItemStack itemStack;

    public Button(ButtonType buttonType, String target, ItemStack itemStack) {
        this.type = buttonType;
        this.target = target;
        this.itemStack = itemStack;
    }

    public ButtonType getType() {
        return type;
    }

    public String getTarget() {
        return target;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }
}