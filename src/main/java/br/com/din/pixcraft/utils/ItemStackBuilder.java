package br.com.din.pixcraft.utils;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.stream.Collectors;

public class ItemStackBuilder {
    private Material material = Material.BEDROCK;
    private String displayName;
    private List<String> lore;
    private int amount = 1;
    private boolean isEnchanted = false;

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

    public ItemStackBuilder setEnchanted(boolean isEnchanted) {
        this.isEnchanted = isEnchanted;
        return this;
    }

    public ItemStack build() {
        ItemStack itemStack = new ItemStack(material, amount);
        ItemMeta itemMeta = itemStack.getItemMeta();

        if (itemMeta != null) {
            if (displayName != null) itemMeta.setDisplayName(displayName.replace("&", "§"));
            if (lore != null) itemMeta.setLore(lore.stream().map(s -> s.replace("&", "§")).collect(Collectors.toList()));
            if (isEnchanted) itemMeta.addEnchant(Enchantment.DURABILITY, 1, false);
            itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ATTRIBUTES);
            itemStack.setItemMeta(itemMeta);
        }
        return itemStack;
    }
}