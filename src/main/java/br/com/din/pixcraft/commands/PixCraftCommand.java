package br.com.din.pixcraft.commands;

import br.com.din.pixcraft.order.OrderManager;
import br.com.din.pixcraft.product.Product;
import br.com.din.pixcraft.product.ProductManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class PixCraftCommand implements CommandExecutor, TabCompleter {
    private final JavaPlugin plugin;
    private final ProductManager productManager;
    private final OrderManager orderManager;

    public PixCraftCommand (JavaPlugin plugin, ProductManager productManager, OrderManager orderManager) {
        plugin.getCommand("pixcraft").setExecutor(this);
        plugin.getCommand("pixcraft").setTabCompleter(this);

        this.plugin = plugin;
        this.orderManager = orderManager;
        this.productManager = productManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (args.length >= 0) {
            switch (args[0]) {
                case "reload":
                    if (sender.hasPermission("pixcraft.command.reload")) {
                        plugin.reloadConfig();
                        productManager.reload();
                        sender.sendMessage("§aPlugin recarregada com sucesso!");
                    } else {
                        sender.sendMessage("§cVocê não tem permissão para executar este comando.");
                    }
                    return true;

                case "product":
                    if (sender.hasPermission("pixcraft.command.product")) {

                        if (sender instanceof Player) {
                            Product product = productManager.getProduct(args[1]);

                            if (product != null) {
                                if (!orderManager.getOrders().containsKey(((Player) sender).getUniqueId())) {
                                    orderManager.processOrder(((Player) sender), product);

                                } else {
                                    sender.sendMessage("§cVocê já tem um pedido em andamento.");
                                }

                            } else {
                                sender.sendMessage("§cProduto não encontrado.");
                            }

                        } else {
                            sender.sendMessage("§cEste comando só pode ser executado por jogadores.");
                        }
                    } else {
                        sender.sendMessage("§cVocê não tem permissão para executar este comando.");
                    }
                    return true;
            }
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String s, String[] args) {
        if (args.length == 1) {
            return Arrays.asList("reload", "product");
        }

        if (args.length == 2 && args[0].equalsIgnoreCase("product")) {
            return productManager.getProducts().keySet().stream().collect(Collectors.toList());
        }

        return Collections.emptyList();
    }
}