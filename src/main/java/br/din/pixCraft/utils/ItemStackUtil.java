package br.din.pixCraft.utils;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;
import java.util.Objects;

public class ItemStackUtil {
    public static ItemStack create(Material material, String name, List<String> lore, int amount) {
        ItemStack itemStack = new ItemStack(material, amount);
        ItemMeta itemMeta = itemStack.getItemMeta();

        if (name != null) {
            itemMeta.setDisplayName(name);
        }

        if (lore != null) {
            itemMeta.setLore(lore);
        }
        itemStack.setItemMeta(itemMeta);

        return itemStack;
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