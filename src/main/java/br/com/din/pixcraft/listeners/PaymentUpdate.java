package br.com.din.pixcraft.listeners;

import br.com.din.pixcraft.listeners.custom.PaymentUpdateEvent;
import br.com.din.pixcraft.order.Order;
import br.com.din.pixcraft.order.OrderManager;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class PaymentUpdate implements Listener {
    private final JavaPlugin plugin;
    private final OrderManager orderManager;

    public PaymentUpdate(JavaPlugin plugin, OrderManager orderManager) {
        this.plugin = plugin;
        this.orderManager = orderManager;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPaymentUpdate(PaymentUpdateEvent event) {
        Order order = event.getOrder();

        Player player = Bukkit.getPlayer(order.getId());
        if (player != null && player.isOnline()) {
            action(player, order);
            orderManager.getOrder(player.getUniqueId());
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (!orderManager.getOrders().containsKey(event.getPlayer().getUniqueId())) return;
        action(event.getPlayer(), orderManager.getOrder(event.getPlayer().getUniqueId()));
    }

    private void action(Player player, Order order) {
        switch (order.getPaymentData().getStatus()) {
            case APPROVED:
                player.sendMessage("§aPagamento aprovado!");
                orderManager.removeOrder(player.getUniqueId());
                for (String command : order.getProduct().getReward()) {
                    Bukkit.dispatchCommand(player, command.replace("{player}", player.getName()));
                }
                break;

            case CANCELLED:
                orderManager.removeOrder(player.getUniqueId());
                player.sendMessage("§cPagamento cancelado.");
                break;

            case PENDING:
                break;
        }
    }
}