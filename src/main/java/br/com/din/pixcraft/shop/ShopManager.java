package br.com.din.pixcraft.shop;

import br.com.din.pixcraft.order.OrderManager;
import br.com.din.pixcraft.shop.category.CategoryManager;
import br.com.din.pixcraft.shop.gui.ShopGui;
import br.com.din.pixcraft.product.ProductManager;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class ShopManager {
    private final JavaPlugin plugin;
    private final CategoryManager categoryManager;
    private final ProductManager productManager;
    private final OrderManager orderManager;
    private final ShopGui shopGui;

    public ShopManager(JavaPlugin plugin, OrderManager orderManager, ProductManager productManager) {
        this.plugin = plugin;
        this.productManager = productManager;

        this.orderManager = orderManager;
        this.categoryManager = new CategoryManager(plugin, "categories", productManager);
        this.shopGui = new ShopGui(plugin, orderManager, categoryManager, productManager);
    }

    public void open(Player player) {
        shopGui.openCategory(player, plugin.getConfig().getString("shop.default-category"));
    }

    public void open(Player player, String categoryId) {
        shopGui.openCategory(player, categoryId);
    }

    public void buy(Player player, Button button) {
        shopGui.openConfirmationMenu(player, button);
    }

    public CategoryManager getCategoryManager() {
        return categoryManager;
    }

    public ProductManager getProductManager() {
        return productManager;
    }

    public ShopGui getShopGui() {
        return shopGui;
    }
}