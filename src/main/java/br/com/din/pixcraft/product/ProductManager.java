package br.com.din.pixcraft.product;

import br.com.din.pixcraft.payment.PaymentStatus;
import br.com.din.pixcraft.utils.NBTUtils;
import br.com.din.pixcraft.utils.minecraft_item_stack.ItemStackUtils;
import br.com.din.pixcraft.yaml.YamlDataManager;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import javax.management.MBeanAttributeInfo;
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
        FileConfiguration productsFile = getFileConfiguration();
        if (productsFile == null) return;

        products.clear();
        for (String key : productsFile.getKeys(false)) {
            ConfigurationSection productData = productsFile.getConfigurationSection(key);

            String id = key;
            String name = productData.getString("name");
            double price = productData.getDouble("price");
            List<String> reward = productData.getStringList("reward");

            // Ícone do GUI
            Material material = Material.valueOf(productData.getString("icon.material").toUpperCase());
            String displayName = productData.getString("icon.displayname");
            List<String> lore = productData.getStringList("icon.lore");
            int amount = productData.getInt("icon.amount");

            ItemStack icon = ItemStackUtils.builder()
                    .setMaterial(material)
                    .setDisplayName(displayName)
                    .setLore(lore)
                    .setAmount(amount)
                    .build();
            icon = NBTUtils.setTag(icon, "cc_gui_item_type", "product_icon");

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