package br.din.pixCraft.listeners;

import br.din.pixCraft.PixCraft;
import br.din.pixCraft.order.Order;
import br.din.pixCraft.order.OrderManager;

import br.din.pixCraft.payment.gateway.MercadoPagoAPI;
import br.din.pixCraft.utils.ItemStackUtil;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.persistence.PersistentDataType;

public class PlayerQuitListener implements Listener {
    private final PixCraft plugin;

    public PlayerQuitListener(PixCraft plugin) {
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if (OrderManager.getOrders().containsKey(player.getUniqueId())) {
            plugin.getLogger().info(player.getName() + " saiu. Cancelando seu pedido...");
            Order order = OrderManager.getOrders().get(player.getUniqueId());
            NamespacedKey key = new NamespacedKey(plugin, "paymentId");

            MercadoPagoAPI.cancelPayment(order.getPaymentID());
            ItemStackUtil.removeItemByData(player, key, PersistentDataType.LONG, order.getPaymentID());
            OrderManager.removeOrder(player.getUniqueId());
        }
    }
}