package br.com.din.pixcraft;

import br.com.din.pixcraft.commands.PixCraftCommand;
import br.com.din.pixcraft.order.OrderManager;
import br.com.din.pixcraft.product.ProductManager;
import br.com.din.pixcraft.utils.AsciiArtUtils;
import org.bukkit.plugin.java.JavaPlugin;

public final class PixCraft extends JavaPlugin {
    private static JavaPlugin instance;

    @Override
    public void onEnable() {
        instance = this;
        AsciiArtUtils.printAsciiArt(getLogger());

        saveDefaultConfig();

        ProductManager productManager = new ProductManager(this, "products.yml");
        OrderManager orderManager = new OrderManager();

        new PixCraftCommand(this, productManager, orderManager);
    }

    @Override
    public void onDisable() {

    }

    public static JavaPlugin getInstance() {
        return instance;
    }
}
