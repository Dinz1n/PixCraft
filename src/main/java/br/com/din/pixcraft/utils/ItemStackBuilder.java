package br.com.din.pixcraft.utils;

import com.cryptomorin.xseries.XMaterial;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.stream.Collectors;

public class ItemStackBuilder {
    private ItemStack itemStack;

    public ItemStackBuilder() {}

    public ItemStackBuilder(ItemStack itemBase) {
        this.itemStack = itemBase.clone();
    }

    public ItemStackBuilder(Material material) {
        this.itemStack = new ItemStack(material);
    }

    public ItemStackBuilder(XMaterial xMaterial) {
        this.itemStack = xMaterial.parseItem();
        if (this.itemStack == null) {
            this.itemStack = new ItemStack(Material.BEDROCK);
        }
    }

    public ItemStackBuilder setMaterial(String materialName) {
        if (materialName == null || materialName.isEmpty()) {
            this.itemStack = new ItemStack(Material.BEDROCK);
            return this;
        }

        try {
            if (materialName.startsWith("head:")) {
                String texture = materialName.substring(5);
                this.itemStack = HeadUtils.getCustomHead(texture);
                return this;
            }

            XMaterial xMat = XMaterial.matchXMaterial(materialName).orElse(null);
            if (xMat != null) {
                ItemStack parsed = xMat.parseItem();
                if (parsed != null) {
                    this.itemStack = parsed;
                    return this;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        this.itemStack = new ItemStack(Material.BEDROCK);
        return this;
    }

    public ItemStackBuilder setDisplayName(String displayName) {
        ItemMeta meta = itemStack.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(displayName.replace("&", "§"));
            itemStack.setItemMeta(meta);
        }
        return this;
    }

    public ItemStackBuilder setLore(List<String> lore) {
        ItemMeta meta = itemStack.getItemMeta();
        if (meta != null) {
            meta.setLore(lore.stream().map(s -> s.replace("&", "§")).collect(Collectors.toList()));
            itemStack.setItemMeta(meta);
        }
        return this;
    }

    public ItemStackBuilder setAmount(int amount) {
        this.itemStack.setAmount(amount > 0 ? amount : 1);
        return this;
    }

    public ItemStackBuilder setEnchanted(boolean enchanted) {
        if (enchanted) {
            ItemMeta meta = itemStack.getItemMeta();
            if (meta != null) {
                meta.addEnchant(Enchantment.DURABILITY, 1, false);
                itemStack.setItemMeta(meta);
            }
            return hideFlags();
        }
        return this;
    }

    public ItemStackBuilder hideFlags() {
        if (Bukkit.getVersion().contains("1.7")) return this;

        ItemMeta meta = itemStack.getItemMeta();
        if (meta != null) {
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ATTRIBUTES);
            itemStack.setItemMeta(meta);
        }
        return this;
    }

    public ItemStack build() {
        return itemStack;
    }
}