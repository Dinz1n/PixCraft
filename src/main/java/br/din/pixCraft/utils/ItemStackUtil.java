package br.din.pixCraft.utils;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;
import java.util.Objects;

public class ItemStackUtil {
    public static ItemStack create(Material material, String name, List<String> lore, int amount, boolean enchanted) {
        ItemStack itemStack = new ItemStack(material == null? Material.BEDROCK : material, amount);
        ItemMeta itemMeta = itemStack.getItemMeta();

        if (name != null) {
            itemMeta.setDisplayName(name);
        }

        if (lore != null) {
            itemMeta.setLore(lore);
        }

        if (enchanted) {
            itemMeta.addEnchant(Enchantment.DURABILITY, 1, false);
            itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS);
        }

        itemStack.setItemMeta(itemMeta);

        return itemStack;
    }

    public static ItemStack setPersistentDataOnItemMeta(ItemStack itemStack, NamespacedKey key, PersistentDataType persistentDataType, Object valorToSet) {
        ItemStack item = itemStack;
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.getPersistentDataContainer().set(key, persistentDataType, valorToSet);
        item.setItemMeta(itemMeta);
        return item;
    }

    public static void removeItemByData(Player player, NamespacedKey key, PersistentDataType persistentDataType, Object valorToCompare) {
        Inventory inventory = player.getInventory();

        for (ItemStack item : inventory.getContents()) {
            if (item != null && item.hasItemMeta()) {
                ItemMeta meta = item.getItemMeta();
                PersistentDataContainer container = meta.getPersistentDataContainer();

                if (container.has(key, persistentDataType) &&
                        Objects.equals(container.get(key, persistentDataType), valorToCompare)) {

                    inventory.remove(item);
                    return;
                }
            }
        }
    }
}