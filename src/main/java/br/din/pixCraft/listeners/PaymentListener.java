package br.din.pixCraft.listeners;

import br.din.pixCraft.listeners.custom.PaymentStatusUpdateEvent;
import br.din.pixCraft.order.Order;

import br.din.pixCraft.order.OrderManager;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

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
        removeItemByData(player, key, order.getPaymentID());
        OrderManager.removeOrder(order.getPaymentID());
        switch (order.getStatus()) {
            case APPROVED:
                player.sendMessage("§aPagamento aprovado! Obrigado pela compra!");
                for (String command : order.getProduct().getRewardCommands()) {
                    if (command.contains("{player}")) {
                        command = command.replace("{player}", player.getName());
                    }
                    Bukkit.getServer().dispatchCommand(player, command);
                }
                break;
            case CANCELLED:
                player.sendMessage("§cPagamento cancelado.");
                break;
        }
    }

    public static void removeItemByData(Player player, NamespacedKey key, long paymentId) {
        Inventory inventory = player.getInventory();

        for (ItemStack item : inventory.getContents()) {
            if (item != null && item.hasItemMeta()) {
                ItemMeta meta = item.getItemMeta();
                PersistentDataContainer container = meta.getPersistentDataContainer();

                // Verifica se o item tem a chave e se o valor corresponde ao paymentId
                if (container.has(key, PersistentDataType.LONG) &&
                        Objects.equals(container.get(key, PersistentDataType.LONG), paymentId)) {

                    inventory.remove(item);
                    return;
                }
            }
        }
    }
}
