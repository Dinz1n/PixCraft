package br.din.pixCraft.gui.confirmcancel;

import br.din.pixCraft.PixCraft;
import br.din.pixCraft.config.YamlDataManager;
import br.din.pixCraft.utils.ItemStackUtil;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CCGBuilder extends YamlDataManager {
    private static final NamespacedKey namespacedKey = new NamespacedKey(PixCraft.getInstance(), "confirmCancelButton");
    private Inventory gui;
    private static ConfigurationSection buttonsSection;
    private static ConfigurationSection guiSection;

    public CCGBuilder(JavaPlugin plugin) {
        super(plugin, "payment_confirmation_gui.yml");
    }

    public Inventory build(ItemStack productIcon) {
        Inventory newGui = Bukkit.createInventory(null, gui.getSize(), guiSection.getString("title"));

        for (int i = 0; i < gui.getSize(); i++) {
            newGui.setItem(i, gui.getItem(i));
        }

        newGui.setItem(guiSection.getInt("slots.product"), productIcon);
        return newGui;
    }

    @Override
    protected void loadData() {
        guiSection = getFileConfiguration().getConfigurationSection("gui");
        buttonsSection = getFileConfiguration().getConfigurationSection("buttons");

        String title = ChatColor.translateAlternateColorCodes('&', guiSection.getString("title"));
        int size = guiSection.getInt("rows") * 9;

        gui = Bukkit.createInventory(null, size, title);

        Map<String, ItemStack> buttons = new HashMap<>();
        for (String key : List.of("confirm-button", "cancel-button")) {
            ConfigurationSection buttonSection = buttonsSection.getConfigurationSection(key);

            List<String> lore = new ArrayList<>();
            if (!buttonSection.getStringList("lore").isEmpty()) {
                for (String text : buttonSection.getStringList("lore")) {
                    lore.add(ChatColor.translateAlternateColorCodes('&', text));
                }
            }

            ItemStack button = ItemStackUtil.create(
                    Material.valueOf(buttonSection.getString("material")),
                    ChatColor.translateAlternateColorCodes('&', buttonSection.getString("displayname")),
                    lore,
                    buttonSection.getInt("amount"),
                    buttonSection.getBoolean("enchanted")
            );
            button = ItemStackUtil.setPersistentDataOnItemMeta(button, namespacedKey, PersistentDataType.STRING, key);
            buttons.put(key, button);
        }

        for (String key : guiSection.getConfigurationSection("slots").getKeys(false)) {
            if (buttons.containsKey(key)) {
                gui.setItem(guiSection.getInt("slots." + key), buttons.get(key));
            }
        }
    }
}