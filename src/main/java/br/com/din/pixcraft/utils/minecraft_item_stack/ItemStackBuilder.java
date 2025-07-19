package br.com.din.pixcraft.utils.minecraft_item_stack;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class ItemStackBuilder {
    private Material material = Material.STONE;
    private String displayName;
    private List<String> lore;
    private int amount = 1;

    public ItemStackBuilder setMaterial(Material material) {
        this.material = material;
        return this;
    }

    public ItemStackBuilder setDisplayName(String displayName) {
        this.displayName = displayName;
        return this;
    }

    public ItemStackBuilder setLore(List<String> lore) {
        this.lore = lore;
        return this;
    }

    public ItemStackBuilder setAmount(int amount) {
        this.amount = amount;
        return this;
    }

    public ItemStack build() {
        ItemStack itemStack = new ItemStack(material, amount);
        ItemMeta itemMeta = itemStack.getItemMeta();

        if (itemMeta != null) {
            if (displayName != null) itemMeta.setDisplayName(displayName);
            if (lore != null) itemMeta.setLore(lore);
            itemStack.setItemMeta(itemMeta);
        }

        return itemStack;
    }
}
