package br.com.din.pixcraft.utils.minecraft_item_stack;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public class ItemStackUtils {
    private ItemStackUtils() {
    }

    public static ItemStackBuilder builder() {
        return new ItemStackBuilder();
    }

    // Métodos que manipulam a classe PersistentDataConainer do ItemMeta

    public static void setPDC(ItemStack itemStack, NamespacedKey key, PersistentDataType dataType, Object data) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.getPersistentDataContainer().set(key, dataType, data);
        itemStack.setItemMeta(itemMeta);
    }

    public static Object getPDC(ItemMeta meta, NamespacedKey key, PersistentDataType dataType) {
        return meta.getPersistentDataContainer().get(key, dataType);
    }
}
