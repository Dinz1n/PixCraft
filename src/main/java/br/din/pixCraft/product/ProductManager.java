package br.din.pixCraft.product;

import br.din.pixCraft.PixCraft;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProductManager {
    private static final Map<String, Product> products = new HashMap<>();
    private final PixCraft plugin = PixCraft.getInstance();
    private static File file;
    private static FileConfiguration productsConfig;
    private final String fileName = "products.yml";

    public ProductManager() {
        this.file = new File(plugin.getDataFolder(), fileName);

        if (!file.exists()) {
            saveDefaultFile();
        }

        productsConfig = YamlConfiguration.loadConfiguration(file);
        loadProducts();
    }

    private void saveDefaultFile() {
        try (InputStream inputStream = plugin.getResource("products.yml")) {
            if (inputStream != null) {
                Files.copy(inputStream, file.toPath());
            } else {
                file.getParentFile().mkdirs();
                file.createNewFile();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void save() {
        try {
            productsConfig.load(file);
            loadProducts();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InvalidConfigurationException e) {
            throw new RuntimeException(e);
        }
    }

    private static void loadProducts() {
        products.clear();
        for (String key : productsConfig.getKeys(false)) {
            String displayName = productsConfig.getString(key + ".displayname");
            Double price = productsConfig.getDouble(key + ".price");
            boolean tax = productsConfig.getBoolean(key + ".include-tax");
            List<String> reward = productsConfig.getStringList(key + ".reward");

            products.put(key, new Product(displayName, price, tax, reward));
        }
    }

    public static Product getProduct(String productId) {
        return products.get(productId);
    }

    public static Map<String, Product> getProducts() {
        return products;
    }
}