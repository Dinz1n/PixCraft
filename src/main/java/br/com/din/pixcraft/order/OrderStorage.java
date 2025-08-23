package br.com.din.pixcraft.order;

import br.com.din.pixcraft.payment.Payment;
import br.com.din.pixcraft.payment.PaymentStatus;
import br.com.din.pixcraft.payment.gateway.PaymentProvider;
import br.com.din.pixcraft.product.Product;
import br.com.din.pixcraft.product.ProductManager;
import br.com.din.pixcraft.yaml.YamlDataManager;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

class OrderStorage extends YamlDataManager<Order> {
    private final Map<UUID, Order> orders = new HashMap<>();
    private final PaymentProvider paymentProvider;
    private final ProductManager productManager;

    public OrderStorage(JavaPlugin plugin, PaymentProvider paymentProvider, ProductManager productManager) {
        super(plugin, "orders.yml");
        this.paymentProvider = paymentProvider;
        this.productManager = productManager;
        loadData();
    }

    @Override
    protected void loadData() {
        orders.clear();
        for (String key : getFileConfiguration().getKeys(false)) {
            ConfigurationSection section = getFileConfiguration().getConfigurationSection(key);
            if (section == null) continue;

            try {
                UUID orderId = UUID.fromString(key);
                String productId = section.getString("product");
                long paymentId = section.getLong("payment.id");
                String qrData = section.getString("payment.qrData");
                PaymentStatus status = PaymentStatus.valueOf(section.getString("payment.status"));

                Product product = productManager.getProduct(productId);
                if (product == null) continue;

                Payment payment = new Payment(paymentId, qrData);
                payment.setStatus(status);

                Order order = new Order(orderId, Bukkit.getPlayer(orderId).getName(), product, payment, paymentProvider);
                orders.put(orderId, order);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void addOrder(Order order) {
        ConfigurationSection section = getFileConfiguration().createSection(order.getId().toString());
        section.set("product", order.getProduct().getId());
        section.set("payment.id", order.getPayment().getId());
        section.set("payment.qrData", order.getPayment().getQrData());
        section.set("payment.status", order.getPayment().getStatus().name());
        super.save();
    }

    public Order getOrder(UUID id) {
        return orders.get(id);
    }

    public Order removeOrder(UUID id) {
        Order removed = orders.remove(id);
        if (removed != null) {
            getFileConfiguration().set(id.toString(), null);
            super.save();
        }
        return removed;
    }

    public Map<UUID, Order> getOrders() {
        return new HashMap<>(orders);
    }
}