package br.com.din.pixcraft.shop;

import br.com.din.pixcraft.order.OrderManager;
import br.com.din.pixcraft.shop.button.Button;
import br.com.din.pixcraft.shop.menu.MenuManager;
import br.com.din.pixcraft.shop.gui.ShopGui;
import br.com.din.pixcraft.product.ProductManager;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class ShopManager {
    private final JavaPlugin plugin;
    private final MenuManager menuManager;
    private final ProductManager productManager;
    private final OrderManager orderManager;
    private final ShopGui shopGui;

    public ShopManager(JavaPlugin plugin, OrderManager orderManager, ProductManager productManager) {
        this.plugin = plugin;
        this.productManager = productManager;

        this.orderManager = orderManager;
        this.menuManager = new MenuManager(plugin, productManager);
        this.shopGui = new ShopGui(plugin, orderManager, menuManager, productManager);
    }

    public void open(Player player) {
        shopGui.openCategory(player, plugin.getConfig().getString("shop.default-menu"));
    }

    public void open(Player player, String categoryId) {
        shopGui.openCategory(player, categoryId);
    }

    public void buy(Player player, Button button) {
        shopGui.openConfirmationMenu(player, button);
    }

    public MenuManager getMenuManager() {
        return menuManager;
    }

    public ProductManager getProductManager() {
        return productManager;
    }

    public ShopGui getShopGui() {
        return shopGui;
    }
}