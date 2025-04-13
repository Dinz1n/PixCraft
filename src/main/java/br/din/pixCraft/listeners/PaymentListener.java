package br.din.pixCraft.listeners;

import br.din.pixCraft.listeners.custom.PaymentStatusUpdateEvent;
import br.din.pixCraft.payment.order.Order;

import br.din.pixCraft.payment.order.OrderManager;
import br.din.pixCraft.utils.DiscordWebhook;
import br.din.pixCraft.utils.ItemStackUtil;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class PaymentListener implements Listener {
    private final JavaPlugin plugin;
    private final NamespacedKey key;

    public PaymentListener(JavaPlugin plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        key = new NamespacedKey(plugin, "paymentId");
    }

    @EventHandler
    public void onPaymentUpdate(PaymentStatusUpdateEvent event) {
        Order order = event.getOrder();
        Player player = Bukkit.getPlayer(order.getPlayerUUID());
        ItemStackUtil.removeItemByData(player, key, PersistentDataType.LONG, order.getPaymentID());
        OrderManager.removeOrder(order.getPlayerUUID());
        switch (order.getStatus()) {
            case APPROVED:
                player.sendMessage("§aPagamento aprovado! Obrigado pela compra!");
                for (String command : order.getProduct().getRewardCommands()) {
                    if (command.contains("{player}")) {
                        command = command.replace("{player}", player.getName());
                    }
                    Bukkit.getServer().dispatchCommand(player, command);
                    String productName = order.getProduct().getDisplayName();
                    double productPrice = order.getProduct().getPrice();
                    String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
                    DiscordWebhook.sendEmbed(plugin.getConfig().getConfigurationSection("discord"), player, productName, productPrice, date);
                }
                break;
            case CANCELLED:
                if (plugin.getConfig().getString("mercadopago.access-token").startsWith("TEST")) {
                    String productName = "Isso é um pedido teste - " + order.getProduct().getDisplayName();
                    double productPrice = order.getProduct().getPrice();
                    String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
                    DiscordWebhook.sendEmbed(plugin.getConfig().getConfigurationSection("discord"), player, productName, productPrice, date);
                }
                player.sendMessage("§cPagamento cancelado.");
                break;
        }
    }
}