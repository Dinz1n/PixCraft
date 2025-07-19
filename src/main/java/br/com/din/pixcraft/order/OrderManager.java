package br.com.din.pixcraft.order;

import br.com.din.pixcraft.product.Product;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class OrderManager {
    private final Map<UUID, Order> orders = new HashMap();
    private final JavaPlugin plugin;

    public OrderManager(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void processOrder(Player player, Product product) {
        player.sendMessage("§aCriando pagamento...");
    }

    public void removeOrder(UUID uuid) {
        orders.remove(uuid);
    }

    public Order getOrder(UUID uuid) {
        return orders.get(uuid);
    }

    public Map<UUID, Order> getOrders() {
        return new HashMap<>(orders);
    }
}
