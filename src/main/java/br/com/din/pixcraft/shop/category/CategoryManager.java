package br.com.din.pixcraft.shop.category;

import br.com.din.pixcraft.shop.Button;
import br.com.din.pixcraft.shop.ButtonType;
import br.com.din.pixcraft.utils.ItemStackBuilder;
import br.com.din.pixcraft.utils.SlotParser;
import br.com.din.pixcraft.yaml.MultiYamlDataManager;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CategoryManager extends MultiYamlDataManager<Category> {
    private final JavaPlugin plugin;

    public CategoryManager(JavaPlugin plugin, String folderName) {
        super(plugin, folderName,
                "categories/shop.yml",
                "categories/confirmation_gui.yml",
                "categories/swords.yml",
                "categories/vips.yml",
                "categories/kits.yml"
        );
        this.plugin = plugin;
        loadAll();
    }

    @Override
    protected Category loadSingleData(FileConfiguration config, String fileName) {
        if (config == null) return null;

        String title = config.getString("title").replace("&", "§");
        int size = config.getInt("rows")*9;
        Map<Integer, Button> buttons = new HashMap<>();

        for (String sectionKey : config.getConfigurationSection("buttons").getKeys(false)) {
            ConfigurationSection buttonData = config.getConfigurationSection("buttons." + sectionKey);

            ButtonType buttonType = ButtonType.valueOf(buttonData.get("type").toString());
            String target = buttonType.equals(ButtonType.DECORATIVE)? null : buttonType.equals(ButtonType.GO_BACK)? null : buttonData.getString("target");

            // Configuração do item
            Material material = Material.valueOf(buttonData.get("item.material").toString());
            if (material == null) material = Material.BEDROCK;

            String displayname = buttonData.getString("item.displayname");
            List<String> lore = buttonData.getStringList("item.lore");
            int amount = buttonData.getInt("item.amount");
            boolean enchanted = buttonData.getBoolean("item.enchanted");

            ItemStack itemStack = new ItemStackBuilder()
                    .setMaterial(material)
                    .setDisplayName(displayname)
                    .setLore(lore)
                    .setAmount(amount)
                    .setEnchanted(enchanted)
                    .build();

            Button button = new Button(buttonType, target, itemStack);

            if (buttonType == ButtonType.DECORATIVE) {
                List<Integer> slots = SlotParser.parseSlots(sectionKey);
                for (int slot : slots) {
                    buttons.put(slot, button);
                }
            } else {
                buttons.put(Integer.parseInt(sectionKey), button);
            }
        }
        return new Category(fileName, title, size, buttons);
    }

    @Override
    protected String getIdFromFileName(String fileName) {
        return fileName.replace(".yml", "");
    }
}
