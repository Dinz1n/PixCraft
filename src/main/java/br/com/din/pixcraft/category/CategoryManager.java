package br.com.din.pixcraft.category;

import br.com.din.pixcraft.gui.shop.ShopItemType;
import br.com.din.pixcraft.gui.shop.ShopNBTKeys;
import br.com.din.pixcraft.product.Product;
import br.com.din.pixcraft.product.ProductManager;
import br.com.din.pixcraft.utils.ItemStackBuilder;
import br.com.din.pixcraft.utils.NBTUtils;
import br.com.din.pixcraft.yaml.YamlDataManager;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CategoryManager extends YamlDataManager<Category> {
    private final Map<String, Category> categories = new HashMap<>();
    private final JavaPlugin plugin;
    private final ProductManager productManager;

    public CategoryManager(JavaPlugin plugin, String fileName, ProductManager productManager) {
        super(plugin, fileName);
        this.plugin = plugin;
        this.productManager = productManager;
        loadData();
    }

    @Override
    protected void loadData() {
        FileConfiguration categoriesConfig = getFileConfiguration();
        if (categoriesConfig == null) return;

        categories.clear();
        for (String key : categoriesConfig.getConfigurationSection("categories").getKeys(false)) {
            ConfigurationSection categoryData = categoriesConfig.getConfigurationSection("categories." + key);
            System.out.println(key);
            // Inventário ainda vazio
            String title = categoryData.getString("title").replace("&", "§");
            int rows = categoryData.getInt("rows")*9;
            Inventory inventory = Bukkit.createInventory(null, rows, title);

            // Ícone
            Material material = Material.valueOf(categoryData.getString("icon.material").toUpperCase());
            String displayName = categoryData.getString("icon.displayname");
            List<String> lore = categoryData.getStringList("icon.lore");
            int amount = categoryData.getInt("icon.amount");
            boolean isEnchanted = categoryData.getBoolean("icon.enchanted");

            ItemStack icon = new ItemStackBuilder()
                    .setMaterial(material)
                    .setDisplayName(displayName)
                    .setLore(lore)
                    .setAmount(amount > 0? amount : 1)
                    .setEnchanted(isEnchanted)
                    .build();
            icon = NBTUtils.setTag(icon, ShopNBTKeys.SHOP_ITEM_TYPE.name(), ShopItemType.CATEGORY.name());
            icon = NBTUtils.setTag(icon, ShopNBTKeys.SHOP_ITEM_VALUE.name(), key);

            categories.put(key, new Category(key, title, inventory, icon));
        }

        for (Category category : categories.values()) {
            ConfigurationSection slots = categoriesConfig.getConfigurationSection("categories." + category.getId() + ".slots");
            for (String slotKey : slots.getKeys(false)) {
                String ref = slots.getString(slotKey);
                ItemStack item = null;

                if (categories.containsKey(ref)) {
                    item = categories.get(ref).getIcon();
                } else {
                    Product product = productManager.getProduct(ref);
                    if (product != null) {
                        item = product.getIcon();
                    } else {
                        plugin.getLogger().warning("Item não encontrado: " + ref + " na categoria " + category.getId());
                    }
                }

                if (item != null) {
                    category.getInventory().setItem(Integer.parseInt(slotKey), item);
                }
            }
        }
    }

    public Category getCategory(String key) {
        return categories.get(key);
    }

    public Map<String, Category> getCategories() {
        return categories;
    }
}