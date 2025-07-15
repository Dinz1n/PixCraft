package br.com.din.pixcraft.product;

import br.com.din.pixcraft.yaml.YamlDataManager;
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
        FileConfiguration productsFile = getFileConfiguration();
        if (productsFile == null) return;

        products.clear();
        for (String key : productsFile.getKeys(false)) {
            String id = key;
            String name = productsFile.getString(key + ".name");
            double price = productsFile.getDouble(key + ".price");
            List<String> reward = productsFile.getStringList(key + ".reward");

            Product product = new Product(id, name, price, reward);
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