package br.din.pixCraft.commands;

import br.din.pixCraft.PixCraft;
import br.din.pixCraft.gui.ConfirmCancelGui;
import br.din.pixCraft.payment.order.OrderManager;
import br.din.pixCraft.payment.gateway.MercadoPagoAPI;
import br.din.pixCraft.product.ProductManager;

import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class PcCommand implements CommandExecutor, TabCompleter {
    private final PixCraft plugin;

    public PcCommand(PixCraft plugin) {
        this.plugin = plugin;
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
                return subCommandBuy(sender, args);
            case "reload":
                subCommandReload(sender);
                return true;
            default:
                sender.sendMessage("§cComando inválido! Use: /pc <buy|reload>");
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
            return List.of("buy", "reload");
        }

        if (args.length == 2 && args[0].equals("buy")) {
            return ProductManager.getProducts().keySet().stream().toList();
        }
        return List.of();
    }

    private boolean subCommandBuy(CommandSender sender, String args[]) {
        if (args.length < 2) {
            sender.sendMessage("§cUso: /pc buy {produto} [jogador]");
            return true;
        }

        String product = args[1];

        if (!ProductManager.getProducts().containsKey(product)) {
            sender.sendMessage("§cEsse produto não existe.");
            return true;
        }

        Player payer = null;

        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (OrderManager.getOrders().containsKey(player.getUniqueId())) {
                sender.sendMessage("§cVocê só pode fazer um pedido por vez.");
                return true;
            }

            if (args.length == 2) {
                payer = player;
            } else {
                sender.sendMessage("§cUso: /pc buy {produto}");
            }
        } else {
            if (args.length == 3) {
                Player target = Bukkit.getPlayer(args[2]);
                if (target != null) {
                    payer = target;
                } else {
                    sender.sendMessage("§cJogador não encontrado.");
                }
            } else {
                sender.sendMessage("§cUso: /pc buy {produto} {jogador}");
            }
        }

        if (payer != null) {
            Player finalPayer = payer;
            Listener listener = new Listener() {
                @EventHandler
                public void onInventoryClick(InventoryClickEvent event) {
                    if (event.getClickedInventory().getType().equals(InventoryType.CHEST) && event.getView().getTitle().equals("Confirmar pedido")) {
                        event.setCancelled(true);
                        if (event.getWhoClicked() == finalPayer) {
                            if (event.getCurrentItem() != null) {
                                switch (event.getCurrentItem().getType()) {
                                    case LIME_WOOL:
                                        OrderManager.processOrder(finalPayer, product);
                                        event.getWhoClicked().closeInventory();
                                        HandlerList.unregisterAll(this);
                                        break;
                                    case RED_WOOL:
                                        event.getWhoClicked().closeInventory();
                                        HandlerList.unregisterAll(this);
                                        break;
                                    case NAME_TAG:
                                        event.setCancelled(true);
                                        break;
                                    default:
                                        event.setCancelled(true);
                                        break;
                                }
                            }
                        }
                    }
                }

                @EventHandler
                public void onInventoryClose(InventoryCloseEvent event) {
                    if (event.getInventory().getType().equals(InventoryType.CHEST) && event.getView().getTitle().equals("Confirmar pedido")) {
                        HandlerList.unregisterAll(this);
                    }
                }
            };
            Bukkit.getPluginManager().registerEvents(listener, plugin);

            ConfirmCancelGui.showGui(payer, ProductManager.getProduct(product));
        }

        return true;
    }

    private void subCommandReload(CommandSender sender) {
        plugin.reloadConfig();
        ProductManager.save();
        MercadoPagoAPI.setAccessToken(plugin.getConfig().getString("mercadopago.access-token"));
        sender.sendMessage("§aPlugin recarregado!");
    }
}