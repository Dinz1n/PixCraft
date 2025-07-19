package br.com.din.pixcraft;

import br.com.din.pixcraft.commands.PCCommand;
import br.com.din.pixcraft.gui.ConfirmCancelGui;
import br.com.din.pixcraft.order.OrderManager;
import br.com.din.pixcraft.product.ProductManager;
import br.com.din.pixcraft.utils.AsciiArt;

import br.com.din.pixcraft.utils.minecraft_item_stack.PDCKeys;
import org.bukkit.plugin.java.JavaPlugin;

public final class PixCraft extends JavaPlugin {
    private static JavaPlugin instance;
    private ProductManager productManager;
    private ConfirmCancelGui confirmCancelGui;
    private OrderManager orderManager;

    @Override
    public void onEnable() {
        instance = this;

        AsciiArt.printAsciiArt(getLogger());
        PDCKeys.init(this);

        saveDefaultConfig();
        productManager = new ProductManager(this, "products.yml");

        confirmCancelGui = new ConfirmCancelGui();
        orderManager = new OrderManager(this);

        new PCCommand(this, productManager, confirmCancelGui, orderManager);
    }

    @Override
    public void onDisable() {
    }

    public static JavaPlugin getInstance() {
        return instance;
    }
}
