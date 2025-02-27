package br.din.pixCraft.utils;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.Objects;

public class ItemStackUtil {
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
