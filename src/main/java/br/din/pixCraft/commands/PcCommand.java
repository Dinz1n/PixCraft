package br.din.pixCraft.commands;

import br.din.pixCraft.PixCraft;
import br.din.pixCraft.order.OrderManager;
import br.din.pixCraft.payment.gateway.MercadoPagoAPI;
import br.din.pixCraft.product.ProductManager;

import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class PcCommand implements CommandExecutor, TabCompleter {
    private static final PixCraft plugin = PixCraft.getInstance();
    private static final Logger log = LoggerFactory.getLogger(PcCommand.class);

    public PcCommand() {
        plugin.getCommand("pixcraft").setExecutor(this);
        plugin.getCommand("pixcraft").setTabCompleter(this);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (args.length < 1) {
            sender.sendMessage("§cUso: /pc <buy|pay|reload> [produto] [jogador]");
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "buy":
                return handleBuyCommand(sender, args);

            case "pay":
                subCommandPay();
                return true;

            case "reload":
                subCommandReload(sender);
                return true;
            default:
                sender.sendMessage("§cComando inválido! Use: /pc <buy|pay|reload>");
                return true;
        }
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {

        if (sender instanceof ConsoleCommandSender) {
            List<String> players = new ArrayList<>();
            if (args.length == 3) {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    players.add(player.getName());
                }
                return players;
            }
        }

        if (args.length == 1) {
            return List.of("buy", "pay", "reload");
        }

        if (args.length == 2 && args[0].equals("buy")) {
            return ProductManager.getProducts().keySet().stream().toList();
        }

//        if (args.length == 2 && args[0].equals("orders")) {
//           List<String> players = new ArrayList<>();
//           for (UUID playerUUID : OrderManager.getOrders().keySet()) {
//               String playerName = Bukkit.getPlayer(playerUUID).getName();
//               if (!players.contains(playerName)) {
//                   players.add(playerName);
//               }
//           }
//           return players;
//        }
//
//        if (args.length == 3 && args[0].equals("orders") && OrderManager.getOrders().containsKey(Bukkit.getPlayerUniqueId(args[1]))) {
//            List<String> paymentIdString = new ArrayList<>();
//            for (Order order : OrderManager.getPlayerOrders(Bukkit.getPlayerUniqueId(args[1]))) {
//                paymentIdString.add(order.getPaymentID().toString());
//            }
//            return paymentIdString;
//        }
//
//        if (args.length == 4 &&
//                args[0].equals("orders") &&
//                OrderManager.getOrders().containsKey(Bukkit.getPlayerUniqueId(args[1])) &&
//                OrderManager.getOrderById(Long.valueOf(args[2])) != null
//        ) {
//            return List.of("cancel");
//        }

        return List.of();
    }

    private boolean handleBuyCommand(@NotNull CommandSender sender, @NotNull String[] args) {
        if (args.length < 2) {
            sender.sendMessage("§cUso: /pc buy {produto} [jogador]");
            return true;
        }

        String produto = args[1];

        if (!ProductManager.getProducts().containsKey(produto)) {
            sender.sendMessage("§cEsse produto não existe.");
            return true;
        }

        if (sender instanceof Player) {
            // Comando digitado por um jogador
            if (args.length == 2) {
                subCommandBuy((Player) sender, produto);
            } else {
                sender.sendMessage("§cUso: /pc buy {produto}");
            }
        } else {
            // Comando digitado pelo console
            if (args.length == 3) {
                Player target = Bukkit.getPlayerExact(args[2]);
                if (target != null) {
                    subCommandBuy(target, produto);
                } else {
                    sender.sendMessage("§cJogador não encontrado.");
                }
            } else {
                sender.sendMessage("§cUso: /pc buy {produto} {jogador}");
            }
        }

        return true;
    }

    private static void subCommandBuy(Player player, String productId) {
        OrderManager.processOrder(player, productId);
    }

    private static void subCommandPay() {

    }

    private static void subCommandReload(CommandSender sender) {
        plugin.reloadConfig();
        ProductManager.save();
        MercadoPagoAPI.setAccessToken(plugin.getConfig().getString("mercadopago.access-token"));
        sender.sendMessage("§aPlugin recarregado!");
    }
}