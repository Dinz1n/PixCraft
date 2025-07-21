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
}
