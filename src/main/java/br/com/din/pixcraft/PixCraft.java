package br.com.din.pixcraft;

import br.com.din.pixcraft.commands.PCCommand;
import br.com.din.pixcraft.gui.ConfirmCancelGui;
import br.com.din.pixcraft.order.OrderManager;
import br.com.din.pixcraft.product.ProductManager;

import br.com.din.pixcraft.utils.minecraft_item_stack.PDCKeys;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public final class PixCraft extends JavaPlugin {
    private static JavaPlugin instance;
    private ProductManager productManager;
    private ConfirmCancelGui confirmCancelGui;
    private OrderManager orderManager;

    @Override
    public void onEnable() {
        instance = this;

        printAsciiArt();
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

    private void printAsciiArt() {
        ConsoleCommandSender console = Bukkit.getConsoleSender();
        String version = getDescription().getVersion();

        String[] lines = {
                "§c-----------------------",
                "§a __",
                "§a|__) | \\_/ §6by Din",
                "§a|    | / \\ §6v" + version,
                "§b __   __        ___ ___ ",
                "§b/  ` |__)  /\\  |__   |  ",
                "§b\\__, |  \\ /--\\ |     |  ",
                "§c-----------------------"
        };

        for (String line : lines) {
            console.sendMessage(line);
        }
    }

    public static JavaPlugin getInstance() {
        return instance;
    }
}
