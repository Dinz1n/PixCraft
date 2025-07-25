package br.com.din.pixcraft;

import br.com.din.pixcraft.category.CategoryManager;
import br.com.din.pixcraft.commands.PCCommand;
import br.com.din.pixcraft.commands.ShopCommand;
import br.com.din.pixcraft.gui.shop.ConfirmCancelGui;
import br.com.din.pixcraft.gui.shop.ShopGui;
import br.com.din.pixcraft.order.OrderManager;
import br.com.din.pixcraft.product.ProductManager;

import de.tr7zw.changeme.nbtapi.NBT;

import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public final class PixCraft extends JavaPlugin {
    private static JavaPlugin instance;
    private Logger logger;
    private ProductManager productManager;
    private CategoryManager categoryManager;
    private OrderManager orderManager;
    private ConfirmCancelGui confirmCancelGui;
    private ShopGui shopGui;

    @Override
    public void onEnable() {
        printAsciiArt();

        instance = this;
        logger = getLogger();

        logger.info("Inicializando NBT-API...");
        if (!NBT.preloadApi()) {
            getLogger().warning("NBT-API wasn't initialized properly, disabling the plugin");
            getPluginLoader().disablePlugin(this);
            return;
        }

        logger.info("Carregando arquivos de configuração...");
        saveDefaultConfig();
        productManager = new ProductManager(this, "products.yml");
        categoryManager = new CategoryManager(this, "categories.yml", productManager);
        orderManager = new OrderManager(this);

        logger.info("Registrando listeners...");
        confirmCancelGui = new ConfirmCancelGui(this);
        shopGui = new ShopGui(this, orderManager, productManager, categoryManager, confirmCancelGui);


        logger.info("Registrando comandos...");
        new PCCommand(this, productManager, categoryManager, confirmCancelGui, orderManager);
        new ShopCommand(this, getConfig().getStringList("shop-command-aliases"), shopGui, categoryManager);

        logger.info("Plugin inicializado com sucesso!");
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
