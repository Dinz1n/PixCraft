package br.com.din.pixcraft.order;

import br.com.din.pixcraft.message.MessageManager;
import br.com.din.pixcraft.qrmap.QrCodeMapCreator;
import br.com.din.pixcraft.payment.PaymentStatus;
import br.com.din.pixcraft.utils.NBTItemUtils;
import br.com.din.pixcraft.payment.gateway.PaymentProvider;
import br.com.din.pixcraft.product.Product;
import br.com.din.pixcraft.product.ProductManager;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class OrderManager {
    private final JavaPlugin plugin;
    private final OrderStorage storage;
    private final PaymentProvider paymentProvider;

    public OrderManager(JavaPlugin plugin, PaymentProvider paymentProvider, ProductManager productManager) {
        this.plugin = plugin;
        this.storage = new OrderStorage(plugin, paymentProvider, productManager);
        this.paymentProvider = paymentProvider;

        if (!storage.getOrders().isEmpty()) {
            for (Order order : storage.getOrders().values()) {
                if (order.getPayment().getStatus() == PaymentStatus.PENDING) {
                    paymentProvider.getStatus(order.getPayment().getId(), paymentStatus -> {
                        order.getPayment().setStatus(paymentStatus);
                        storage.addOrder(order);
                    });
                }
            }
        }
    }

    public void processOrder(Player player, Product product) {
        if (storage.getOrders().containsKey(player.getUniqueId())) {
            player.sendMessage(MessageManager.ORDER_LIMIT_ONE.replace("&", "§"));
            return;
        }

        if (product == null) {
            player.sendMessage(MessageManager.PRODUCT_NOT_FOUND.replace("&", "§"));
            return;
        }

        paymentProvider.createPayment(player.getName(), product, paymentData -> {
            if (paymentData == null) {
                player.sendMessage(MessageManager.PAYMENT_CREATION_ERROR.replace("&", "§"));
                return;
            }

            Bukkit.getScheduler().runTask(plugin, () -> {
                Order order = new Order(player.getUniqueId(), player.getName(), product, paymentData, paymentProvider);
                storage.addOrder(order);

                if (!player.isOnline() && plugin.getConfig().getBoolean("payment.cancel-on-leave")) {
                    storage.removeOrder(player.getUniqueId()).cancel();
                    return;
                }

                ConfigurationSection qrCodeMapSection = plugin.getConfig().getConfigurationSection("qr-code-map");
                ItemStack qrMap = QrCodeMapCreator.create(
                        paymentData.getQrData(), player.getWorld(),
                        qrCodeMapSection.getString("displayname"),
                        qrCodeMapSection.getStringList("lore"));

                if (qrMap == null) {
                    order.cancel();
                    removeOrder(player.getUniqueId());
                    player.sendMessage(MessageManager.PAYMENT_UNEXPECTED_ERROR.replace("&", "§"));
                    return;
                }

                qrMap = NBTItemUtils.setTag(qrMap, "pixcraft_order_id", order.getId());

                int slotMap = qrCodeMapSection.getInt("slot");
                if (slotMap < 0 || slotMap > 8) slotMap = 3;
                player.getInventory().setHeldItemSlot(slotMap);
                player.getInventory().setItem(slotMap, qrMap);

                player.sendMessage(MessageManager.PAYMENT_CREATED.replace("&", "§"));
            });
        });
    }

    public void addOrder(Order order) {
        storage.addOrder(order);
    }

    public Order getOrder(UUID uuid) {
        return storage.getOrder(uuid);
    }

    public Order getOrderByPaymentId(long paymentId) {
        return storage.getOrders().values().stream().filter(order -> order.getPayment().getId() == paymentId).findFirst().get();
    }

    public Map<UUID, Order> getOrders() {
        return new HashMap<>(storage.getOrders());
    }

    public Order removeOrder(UUID uuid) {
        return storage.removeOrder(uuid);
    }
}