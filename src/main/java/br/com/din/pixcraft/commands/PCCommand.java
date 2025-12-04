package br.com.din.pixcraft.commands;

import br.com.din.pixcraft.message.MessageManager;
import br.com.din.pixcraft.payment.gateway.PaymentProvider;

import br.com.din.pixcraft.product.Product;
import br.com.din.pixcraft.product.ProductManager;
import br.com.din.pixcraft.shop.button.Button;
import br.com.din.pixcraft.shop.ShopManager;
import br.com.din.pixcraft.shop.button.ButtonType;
import br.com.din.pixcraft.shop.menu.Menu;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;
import java.util.stream.Collectors;

public class PCCommand implements CommandExecutor, TabCompleter {
    private final JavaPlugin plugin;
    private final MessageManager messageManager;
    private final ProductManager productManager;
    private final ShopManager shop;
    private final PaymentProvider paymentProvider;

    public PCCommand(JavaPlugin plugin, MessageManager messageManager, ProductManager productManager, ShopManager shop, PaymentProvider paymentProvider) {
        this.plugin = plugin;
        this.messageManager = messageManager;
        this.shop = shop;
        this.productManager = productManager;
        this.paymentProvider = paymentProvider;

        plugin.getCommand("pixcraft").setExecutor(this);
        plugin.getCommand("pixcraft").setTabCompleter(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (!sender.hasPermission("pixcraft.command")) {
            sender.sendMessage(MessageManager.COMMAND_NO_PERMISSION);
            return true;
        }

        if (args.length == 0) return false;

        switch (args[0]) {
            case "reload":
                return handleReload(sender);
            case "menu":
                return handleMenu(sender, args);
            case "product":
                return handleProduct(sender, args);
            default: return false;
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String s, String[] args) {
        if  (!sender.hasPermission("pixcraft.command")) return Collections.emptyList();
        switch (args.length) {
            case 1:
                return Arrays.asList("menu", "reload", "product");
            case 2:
                if (args[0].equals("menu")) {
                    return shop.getMenuManager().getMenus().stream().map(Menu::getId).collect(Collectors.toList());
                }

                if (args[0].equals("product")) {
                    return productManager.getProducts().stream().map(Product::getId).collect(Collectors.toList());
                }

            default:
                return Collections.emptyList();
        }
    }

    private boolean handleReload(CommandSender sender) {
        plugin.reloadConfig();
        messageManager.reload();
        paymentProvider.setAccessToken(plugin.getConfig().getString("payment.provider.access-token"));
        productManager.reload();
        shop.getMenuManager().reload();
        sender.sendMessage(MessageManager.PLUGIN_RELOAD_SUCCESS);
        return true;
    }

    private boolean handleMenu(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(MessageManager.COMMAND_ONLY_PLAYER);
            return true;
        }

        if (args.length == 1 || args[1].isEmpty() || args[1].equals("") || shop.getMenuManager().getMenu(args[1]) == null) {
            sender.sendMessage(MessageManager.MENU_NOT_FOUND);
            return true;
        }
        shop.open((Player) sender, args[1]);
        return true;
    }

    private boolean handleProduct(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(MessageManager.COMMAND_ONLY_PLAYER);
            return true;
        }

        Product product = productManager.getProduct(args[1]);

        if (args.length == 1 || args[1].isEmpty() || args[1].equals("") || product == null) {
            sender.sendMessage(MessageManager.PRODUCT_NOT_FOUND.replace("&", "§"));
            return true;
        }

        Button productButton = new Button(ButtonType.PRODUCT, args[1], product.getIcon());

        shop.buy((Player) sender, productButton);
        return true;
    }
}