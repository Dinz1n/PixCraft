package br.com.din.pixcraft.commands;

import br.com.din.pixcraft.gui.ConfirmCancelGui;
import br.com.din.pixcraft.order.OrderManager;
import br.com.din.pixcraft.product.Product;
import br.com.din.pixcraft.product.ProductManager;
import org.bukkit.Bukkit;
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

public class PCCommand implements CommandExecutor, TabCompleter {
    private final JavaPlugin plugin;
    private final ProductManager productManager;
    private final ConfirmCancelGui confirmCancelGui;
    private final OrderManager orderManager;

    public PCCommand(JavaPlugin plugin, ProductManager productManager, ConfirmCancelGui confirmCancelGui, OrderManager orderManager) {
        plugin.getCommand("pixcraft").setExecutor(this);
        plugin.getCommand("pixcraft").setTabCompleter(this);

        this.plugin = plugin;
        this.orderManager = orderManager;
        this.confirmCancelGui = confirmCancelGui;
        this.productManager = productManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (args.length == 0) return false;

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
                if (!sender.hasPermission("pixcraft.command.product")) {
                    sender.sendMessage("§cVocê não tem permissão para executar este comando.");
                    return true;
                }

                if (!(sender instanceof Player)) {
                    sender.sendMessage("§cEste comando só pode ser executado por jogadores.");
                    return true;
                }

                Player player = (Player) sender;

                if (args.length < 2) {
                    player.sendMessage("§cUso correto: /comando product <produto>");
                    return true;
                }

                Product product = productManager.getProduct(args[1]);
                if (product == null) {
                    player.sendMessage("§cProduto não encontrado.");
                    return true;
                }

                if (orderManager.getOrders().containsKey(player.getUniqueId())) {
                    player.sendMessage("§cVocê já tem um pedido em andamento.");
                    return true;
                }

                confirmCancelGui.openGui(player, product, confirmOrder -> {
                    if (confirmOrder)
                    Bukkit.getScheduler().runTask(plugin, () -> {
                        orderManager.processOrder(player, product);
                    });
                });

                return true;

            default: return false;
        }
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