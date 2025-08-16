package br.com.din.pixcraft.commands;

import br.com.din.pixcraft.payment.gateway.PaymentProvider;
import br.com.din.pixcraft.shop.category.CategoryManager;
import br.com.din.pixcraft.product.ProductManager;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public class PCCommand implements CommandExecutor, TabCompleter {
    private final JavaPlugin plugin;
    private final ProductManager productManager;
    private final CategoryManager categoryManager;
    private final PaymentProvider paymentProvider;

    public PCCommand(JavaPlugin plugin, ProductManager productManager, CategoryManager categoryManager, PaymentProvider paymentProvider) {
        plugin.getCommand("pixcraft").setExecutor(this);
        plugin.getCommand("pixcraft").setTabCompleter(this);

        this.plugin = plugin;
        this.productManager = productManager;
        this.categoryManager = categoryManager;
        this.paymentProvider = paymentProvider;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (args.length == 0) return false;

        switch (args[0]) {
            case "reload":
                if (sender.hasPermission("pixcraft.command.reload")) {
                    plugin.reloadConfig();
                    paymentProvider.setAccessToken(plugin.getConfig().getString("payment.provider.access-token"));
                    productManager.reload();
                    categoryManager.reload();
                    sender.sendMessage("§a[PixCraft] Plugin recarregado com sucesso!");
                } else {
                    sender.sendMessage("§[PixCraft] cVocê não tem permissão para executar este comando.");
                }
                return true;
            default: return false;
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String s, String[] args) {
        if (args.length == 1) {
            return Arrays.asList("reload");
        }
        return Collections.emptyList();
    }
}