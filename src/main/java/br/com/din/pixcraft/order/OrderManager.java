package br.com.din.pixcraft.order;

import br.com.din.pixcraft.product.Product;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class OrderManager {
    private final Map<UUID, Order> orders = new HashMap();

    public void processOrder(Player player, Product product) {

    }

    public Order getOrder(UUID uuid) {
        return orders.get(uuid);
    }

    public Map<UUID, Order> getOrders() {
        return new HashMap<>(orders);
    }
}
