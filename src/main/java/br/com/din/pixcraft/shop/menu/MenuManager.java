package br.com.din.pixcraft.shop.menu;

import br.com.din.pixcraft.product.ProductManager;
import br.com.din.pixcraft.shop.button.Button;
import br.com.din.pixcraft.shop.button.ButtonFactory;
import br.com.din.pixcraft.yaml.MultiYamlDataManager;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class MenuManager extends MultiYamlDataManager<Menu> {
    private final JavaPlugin plugin;
    private final ProductManager productManager;

    public MenuManager(JavaPlugin plugin, ProductManager productManager) {
        super(plugin, "menus",
                "menus/shop.yml",
                "menus/confirmation_gui.yml",
                "menus/swords.yml",
                "menus/vips.yml",
                "menus/kits.yml"
        );
        this.plugin = plugin;
        this.productManager = productManager;
        loadAll();
    }

    @Override
    protected Menu loadSingleData(FileConfiguration config, String fileName) {
        if (config == null) return null;

        String title = config.getString("title", "Shop").replace("&", "§");
        int size = config.getInt("rows", 3) * 9;
        Map<Integer, Button> buttons = new HashMap<>();

        ConfigurationSection buttonsSection = config.getConfigurationSection("buttons");
        if (buttonsSection == null) {
            plugin.getLogger().warning("Menu '" + fileName + "' não possui uma seção 'buttons'.");
            return new Menu(fileName, title, size, buttons);
        }

        for (String sectionKey : buttonsSection.getKeys(false)) {
            ConfigurationSection buttonData = buttonsSection.getConfigurationSection(sectionKey);
            ButtonFactory.createFromConfig(buttonData, sectionKey, fileName, productManager, buttons);
        }

        return new Menu(fileName, title, size, buttons);
    }

    public Menu getMenu(String id) {
        return super.get(id);
    }

    public Collection<Menu> getMenus() {
        return getAll();
    }
}