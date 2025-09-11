package br.com.din.pixcraft.shop.category;

import br.com.din.pixcraft.product.Product;
import br.com.din.pixcraft.product.ProductManager;
import br.com.din.pixcraft.shop.Button;
import br.com.din.pixcraft.shop.ButtonType;
import br.com.din.pixcraft.utils.ItemStackBuilder;
import br.com.din.pixcraft.utils.SlotParser;
import br.com.din.pixcraft.yaml.MultiYamlDataManager;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CategoryManager extends MultiYamlDataManager<Category> {
    private final JavaPlugin plugin;
    private final ProductManager productManager;

    public CategoryManager(JavaPlugin plugin, String folderName, ProductManager productManager) {
        super(plugin, folderName,
                "categories/shop.yml",
                "categories/confirmation_gui.yml",
                "categories/swords.yml",
                "categories/vips.yml",
                "categories/kits.yml"
        );
        this.plugin = plugin;
        this.productManager = productManager;
        loadAll();
    }

    @Override
    protected Category loadSingleData(FileConfiguration config, String fileName) {
        if (config == null) return null;

        String title = config.getString("title").replace("&", "§");
        int size = config.getInt("rows") * 9;
        Map<Integer, Button> buttons = new HashMap<>();

        for (String sectionKey : config.getConfigurationSection("buttons").getKeys(false)) {
            ConfigurationSection buttonData = config.getConfigurationSection("buttons." + sectionKey);

            ButtonType buttonType = ButtonType.valueOf(buttonData.get("type").toString());
            String target;
            if (buttonType == ButtonType.DECORATIVE || buttonType == ButtonType.GO_BACK) {
                target = null;
            } else {
                target = buttonData.getString("target");
            }

            String materialName = buttonData.getString("item.material");
            String displayname = buttonData.getString("item.displayname");
            List<String> lore = buttonData.getStringList("item.lore");
            int amount = buttonData.getInt("item.amount");
            boolean enchanted = buttonData.getBoolean("item.enchanted");

            if (buttonType == ButtonType.PRODUCT && target != null) {
                Product product = productManager.getProduct(target);
                if (product != null) {
                    String priceStr = String.valueOf(product.getPrice()).replace(".", ",");
                    displayname = displayname.replace("{price}", priceStr);

                    List<String> updatedLore = new ArrayList<>();
                    for (String line : lore) {
                        updatedLore.add(line.replace("{price}", priceStr));
                    }
                    lore = updatedLore;
                }
            }

            ItemStack itemStack = new ItemStackBuilder()
                    .setMaterial(materialName)
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
}
