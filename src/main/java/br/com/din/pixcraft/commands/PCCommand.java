package br.com.din.pixcraft.commands;

import br.com.din.pixcraft.payment.gateway.PaymentProvider;

import br.com.din.pixcraft.product.Product;
import br.com.din.pixcraft.product.ProductManager;
import br.com.din.pixcraft.shop.Button;
import br.com.din.pixcraft.shop.ButtonType;
import br.com.din.pixcraft.shop.ShopManager;
import br.com.din.pixcraft.shop.category.Category;
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
    private final ProductManager productManager;
    private final ShopManager shop;
    private final PaymentProvider paymentProvider;

    public PCCommand(JavaPlugin plugin, ProductManager productManager, ShopManager shop, PaymentProvider paymentProvider) {
        plugin.getCommand("pixcraft").setExecutor(this);
        plugin.getCommand("pixcraft").setTabCompleter(this);

        this.plugin = plugin;
        this.shop = shop;
        this.productManager = productManager;
        this.paymentProvider = paymentProvider;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
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
        switch (args.length) {
            case 1: return Arrays.asList("menu", "reload", "product");
            case 2:
                if (args[0].equals("menu")) {
                    return shop.getCategoryManager().getAll().stream().map(Category::getId).collect(Collectors.toList());
                }

                if (args[0].equals("product")) {
                    return shop.getCategoryManager().getAll().stream()
                            .flatMap(category -> category.getButtons().values().stream())
                            .filter(button -> ButtonType.PRODUCT.equals(button.getType()))
                            .map(Button::getTarget)
                            .filter(Objects::nonNull)
                            .collect(Collectors.toList());
                }

            default: return Collections.emptyList();
        }
    }

    private boolean handleReload(CommandSender sender) {
        if (!sender.hasPermission("pixcraft.command.reload")) {
            sender.sendMessage("§c[PixCraft] Você não tem permissão para executar este comando.");
            return true;
        }
        plugin.reloadConfig();
        paymentProvider.setAccessToken(plugin.getConfig().getString("payment.provider.access-token"));
        productManager.reload();
        shop.getCategoryManager().reload();
        sender.sendMessage("§a[PixCraft] Plugin recarregado com sucesso!");
        return true;
    }

    private boolean handleMenu(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cEsse comando só pode ser executado por jogadores.");
            return true;
        }

        if (!sender.hasPermission("pixcraft.command.menu")) {
            sender.sendMessage("§c[PixCraft] Você não tem permissão para executar este comando.");
            return true;
        }

        if (args.length == 1 || args[1].isEmpty() || args[1].equals("") || shop.getCategoryManager().get(args[1]) == null) {
            sender.sendMessage("§c[PixCraft] Erro! Menu não encontrado.");
            return true;
        }
        shop.open((Player) sender, args[1]);
        return true;
    }

    private boolean handleProduct(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cEsse comando só pode ser executado por jogadores.");
            return true;
        }

        if (!sender.hasPermission("pixcraft.command.menu")) {
            sender.sendMessage("§c[PixCraft] Você não tem permissão para executar este comando.");
            return true;
        }

        if (args.length == 1 || args[1].isEmpty() || args[1].equals("") || productManager.getProduct(args[1]) == null) {
            sender.sendMessage("§c[PixCraft] Erro! Produto não encontrado.");
            return true;
        }

        Button productButton = shop.getCategoryManager().getAll().stream()
                .flatMap(category -> category.getButtons().values().stream())
                .filter(button -> args[1].equals(button.getTarget()))
                .findFirst().get();

        shop.buy((Player) sender, productButton);
        return true;
    }
}