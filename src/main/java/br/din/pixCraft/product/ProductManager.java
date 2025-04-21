package br.din.pixCraft.product;

import br.din.pixCraft.PixCraft;

import br.din.pixCraft.config.YamlDataManager;
import br.din.pixCraft.utils.ItemStackUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProductManager extends YamlDataManager<Product> {
    private static final Map<String, Product> products = new HashMap<>();

    public ProductManager() {
        super(PixCraft.getInstance(), "products.yml");
    }

    @Override
    protected void loadData() {
        products.clear();

        for (String key : getFileConfiguration().getKeys(false)) {
            String displayName = getFileConfiguration().getString(key + ".name");
            Double price = getFileConfiguration().getDouble(key + ".price");
            boolean tax = getFileConfiguration().getBoolean(key + ".include-tax");
            List<String> reward = getFileConfiguration().getStringList(key + ".reward");

            Material iconMaterial = Material.getMaterial(getFileConfiguration().getString(key + ".icon.material"));
            String iconDisplayName = ChatColor.translateAlternateColorCodes('&', getFileConfiguration().getString(key + ".icon.displayname"));
            List<String> iconLore = new ArrayList<>();
            for (String text : getFileConfiguration().getStringList(key + ".icon.lore")) {
                iconLore.add(ChatColor.translateAlternateColorCodes('&', text));
            }
            int iconAmount = getFileConfiguration().getInt(key + ".icon.amount");
            boolean iconEnchanted = getFileConfiguration().getBoolean(key + ".icon.enchanted");

            ItemStack icon = ItemStackUtil.create(iconMaterial, iconDisplayName, iconLore, iconAmount, iconEnchanted);
            products.put(key, new Product(key, displayName, price, tax, reward, icon));
        }
    }

    public static Product getProduct(String productId) {
        return products.get(productId);
    }

    public static Map<String, Product> getProducts() {
        return products;
    }
}