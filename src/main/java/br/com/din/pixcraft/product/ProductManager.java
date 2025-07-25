package br.com.din.pixcraft.product;

import br.com.din.pixcraft.gui.shop.ShopItemType;
import br.com.din.pixcraft.gui.shop.ShopNBTKeys;
import br.com.din.pixcraft.utils.ItemStackBuilder;
import br.com.din.pixcraft.utils.NBTUtils;
import br.com.din.pixcraft.yaml.YamlDataManager;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProductManager extends YamlDataManager<Product> {
    private final Map<String, Product> products = new HashMap<>();
    private final JavaPlugin plugin;

    public ProductManager(JavaPlugin plugin, String fileName) {
        super(plugin, fileName);
        this.plugin = plugin;
        loadData();
    }

    @Override
    protected void loadData() {
        FileConfiguration productsConfig = getFileConfiguration();
        if (productsConfig == null) return;

        products.clear();
        for (String key : productsConfig.getKeys(false)) {
            ConfigurationSection productData = productsConfig.getConfigurationSection(key);

            String id = key;
            String name = productData.getString("name");
            double price = productData.getDouble("price");
            List<String> reward = productData.getStringList("reward");

            // Ícone
            Material material = Material.valueOf(productData.getString("icon.material").toUpperCase());
            String displayName = productData.getString("icon.displayname");
            List<String> lore = productData.getStringList("icon.lore");
            int amount = productData.getInt("icon.amount");
            boolean isEnchanted = productData.getBoolean("icon.enchanted");

            ItemStack icon = new ItemStackBuilder()
                    .setMaterial(material)
                    .setDisplayName(displayName)
                    .setLore(lore)
                    .setAmount(amount > 0? amount : 1)
                    .setEnchanted(isEnchanted)
                    .build();
            icon = NBTUtils.setTag(icon, ShopNBTKeys.SHOP_ITEM_TYPE.name(), ShopItemType.PRODUCT.name());
            icon = NBTUtils.setTag(icon, ShopNBTKeys.SHOP_ITEM_VALUE.name(), key);

            Product product = new Product(id, name, price, reward, icon);
            products.put(id, product);
        }
    }

    public Product getProduct(String id) {
        return products.get(id);
    }

    public Map<String, Product> getProducts() {
        return new HashMap<>(products);
    }
}