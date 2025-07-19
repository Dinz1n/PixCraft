package br.com.din.pixcraft.utils.minecraft_item_stack;

import org.bukkit.NamespacedKey;
import org.bukkit.plugin.java.JavaPlugin;

public class PDCKeys {
    public static NamespacedKey CONFIRM_CANCEL_GUI_BUTTON_TYPE;

    public static void init(JavaPlugin plugin) {
        CONFIRM_CANCEL_GUI_BUTTON_TYPE = new NamespacedKey(plugin, "confirm_cancel_gui_button_type");
    }

    private PDCKeys() {
    }
}
