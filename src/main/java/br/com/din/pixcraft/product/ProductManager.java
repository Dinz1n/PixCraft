package br.com.din.pixcraft.product;

import br.com.din.pixcraft.yaml.MultiYamlDataManager;
import br.com.din.pixcraft.yaml.YamlConfigReader;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProductManager extends MultiYamlDataManager<Product> {
    private final JavaPlugin plugin;

    public ProductManager(JavaPlugin plugin) {
        super(plugin, "products",
                "products/vip-anual.yml",
                "products/vip-mensal.yml",
                "products/vip-semestral.yml",
                "products/kit-avancado.yml",
                "products/kit-iniciante.yml",
                "products/kit-profissional.yml",
                "products/espada-de-pedra.yml",
                "products/espada-de-diamante.yml",
                "products/espada-de-ferro.yml"
        );
        this.plugin = plugin;
        loadAll();
    }

    @Override
    protected Product loadSingleData(FileConfiguration productData, String fileName) {
        if (productData == null) return null;

        String name = productData.getString("name");
        double price = productData.getDouble("price");
        List<String> reward = productData.getStringList("reward");

        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("{price}", String.valueOf(price).replace(".", ","));
        ItemStack icon = YamlConfigReader.buildItem(productData.getConfigurationSection("icon"), placeholders);

        return new Product(fileName, name, price, reward, icon);
    }

    public Product getProduct(String productId) {
        return get(productId);
    }

    public Collection<Product> getProducts() {
        return getAll();
    }
}
