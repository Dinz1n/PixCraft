package br.com.din.pixcraft.product;

import br.com.din.pixcraft.yaml.YamlDataManager;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
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

            String name = productData.getString("name");
            double price = productData.getDouble("price");
            List<String> reward = productData.getStringList("reward");

            Product product = new Product(key, name, price, reward);
            products.put(key, product);
        }
    }

    public Product getProduct(String productId) {
        return products.get(productId);
    }

    public Map<String, Product> getProducts() {
        return new HashMap<>(products);
    }
}