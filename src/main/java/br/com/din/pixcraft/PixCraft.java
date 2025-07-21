package br.com.din.pixcraft;

import br.com.din.pixcraft.commands.PCCommand;
import br.com.din.pixcraft.gui.ConfirmCancelGui;
import br.com.din.pixcraft.order.OrderManager;
import br.com.din.pixcraft.product.ProductManager;

import de.tr7zw.changeme.nbtapi.NBT;

import de.tr7zw.changeme.nbtapi.NBTItem;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public final class PixCraft extends JavaPlugin {
    private static JavaPlugin instance;
    private Logger logger;
    private ProductManager productManager;
    private ConfirmCancelGui confirmCancelGui;
    private OrderManager orderManager;

    @Override
    public void onEnable() {
        instance = this;
        logger = getLogger();

        printAsciiArt();

        logger.info("Inicializando NBT-API...");
        if (!NBT.preloadApi()) {
            getLogger().warning("NBT-API wasn't initialized properly, disabling the plugin");
            getPluginLoader().disablePlugin(this);
            return;
        }

        logger.info("Carregando arquivos de configuração...");
        saveDefaultConfig();
        productManager = new ProductManager(this, "products.yml");

        confirmCancelGui = new ConfirmCancelGui();
        orderManager = new OrderManager(this);

        logger.info("Registrando comandos...");
        new PCCommand(this, productManager, confirmCancelGui, orderManager);

        logger.info("Plugin inicializado com sucesso!");

        ItemStack itemStack = new ItemStack(Material.CHEST);
        NBTItem nbtItem = new NBTItem(itemStack);
        nbtItem.setString("dwa", "dawd");
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
