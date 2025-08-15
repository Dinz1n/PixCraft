package br.com.din.pixcraft.order;

import br.com.din.pixcraft.payment.PaymentStatus;
import br.com.din.pixcraft.utils.NBTItemUtils;
import br.com.din.pixcraft.payment.gateway.PaymentProvider;
import br.com.din.pixcraft.map.CustomMapCreator;
import br.com.din.pixcraft.product.Product;
import br.com.din.pixcraft.product.ProductManager;
import br.com.din.pixcraft.utils.DateUtils;
import br.com.din.pixcraft.utils.QrCodeGenerator;

import com.sun.org.apache.xpath.internal.operations.Or;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.awt.image.BufferedImage;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class OrderManager {
    private final JavaPlugin plugin;
    private final OrderStorage storage;
    private final ProductManager productManager;
    private final PaymentProvider paymentProvider;
    private final CustomMapCreator customMapCreator;

    public OrderManager(JavaPlugin plugin, PaymentProvider paymentProvider, CustomMapCreator customMapCreator, ProductManager productManager) {
        this.plugin = plugin;
        this.productManager = productManager;
        this.storage = new OrderStorage(plugin, paymentProvider, productManager);
        this.paymentProvider = paymentProvider;
        this.customMapCreator = customMapCreator;

        if (!storage.getOrders().isEmpty()) {
            for (Order order : storage.getOrders().values()) {
                if (order.getPaymentData().getStatus() == PaymentStatus.PENDING) {
                    paymentProvider.getStatus(order.getPaymentData().getId(), paymentStatus -> {
                        order.getPaymentData().setStatus(paymentStatus);
                        storage.addOrder(order);
                    });
                }
            }
        }
    }

    public void processOrder(Player player, Product product) {
        if (storage.getOrders().containsKey(player.getUniqueId())) {
            player.sendMessage("§cVocê só pode fazer um pedido por vez.");
            return;
        }

        if (product == null) {
            player.sendMessage("§cErro! Produto não encontrado.");
            return;
        }

        String dateOfExpiration = DateUtils.toMpExpirationOrDefault(
                plugin.getConfig().getString("payment.expiration-time"),
                Duration.ofMinutes(30)
        );

        paymentProvider.createPayment(player.getName(), product, dateOfExpiration, paymentData -> {
            if (paymentData == null) {
                player.sendMessage("§cErro! Não foi possível criar o pagamento.");
                return;
            }

            Bukkit.getScheduler().runTask(plugin, () -> {
                Order order = new Order(player.getUniqueId(), product, paymentData, paymentProvider);
                storage.addOrder(order);

                if (!player.isOnline() && plugin.getConfig().getBoolean("payment.cancel-on-leave")) {
                    storage.removeOrder(player.getUniqueId()).cancel();
                    return;
                }

                BufferedImage qrImage = QrCodeGenerator.generate(paymentData.getQrData(), 128, 128);
                ConfigurationSection qrCodeMapSection = plugin.getConfig().getConfigurationSection("qr-code-map");
                ItemStack qrMap = customMapCreator.create(qrImage, player.getWorld(),
                        qrCodeMapSection.getString("displayname"),
                        qrCodeMapSection.getStringList("lore"));
                qrMap = NBTItemUtils.setTag(qrMap, "pixcraft_order_id", order.getId());

                int slotMap = qrCodeMapSection.getInt("slot");
                if (slotMap < 0 || slotMap > 8) slotMap = 3;
                player.getInventory().setHeldItemSlot(slotMap);
                player.getInventory().setItem(slotMap, qrMap);

                player.sendMessage("§aPagamento criado!");
            });
        });
    }

    public void addOrder(Order order) {
        storage.addOrder(order);
    }

    public Order getOrder(UUID uuid) {
        return storage.getOrder(uuid);
    }

    public Map<UUID, Order> getOrders() {
        return new HashMap<>(storage.getOrders());
    }

    public Order removeOrder(UUID uuid) {
        return storage.removeOrder(uuid);
    }
}