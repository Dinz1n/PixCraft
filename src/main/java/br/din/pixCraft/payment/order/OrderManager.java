package br.din.pixCraft.payment.order;

import br.din.pixCraft.customMap.MapHandler;
import br.din.pixCraft.payment.PaymentStatus;
import br.din.pixCraft.payment.gateway.MercadoPagoAPI;
import br.din.pixCraft.product.Product;
import br.din.pixCraft.product.ProductManager;
import br.din.pixCraft.utils.QrCodeGenerator;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.awt.image.BufferedImage;
import java.util.*;

public class OrderManager {
    private static final Map<UUID, Order> orders = new HashMap<>();

    public static void processOrder(Player player, String productId) {
        Product product = ProductManager.getProduct(productId);

        double price = product.getPrice();
        if (product.isTax()) {
            price += (0.99 / 100) * price;
        }

        MercadoPagoAPI.createPayment(product.getDisplayName(), price, player.getName()).thenAccept(paymentId -> {
            if (paymentId == null) {
                player.sendMessage("§cErro ao criar o pagamento.");
                return;
            }

            Order order = new Order(player.getUniqueId(), paymentId, product);
            orders.put(player.getUniqueId(), order);

            BufferedImage qrCodeImage = QrCodeGenerator.generateQrImage(MercadoPagoAPI.getQrCodeBase64(paymentId));
            ItemStack qrCodeMap = MapHandler.createQrMap(qrCodeImage, player.getWorld(), paymentId);

            player.getInventory().setItem(3, qrCodeMap);
            player.getInventory().setHeldItemSlot(3);
            player.sendMessage("§bVocê recebeu um §6§lQR Code §bpara realizar o pagamento.");
            player.sendMessage("§bClique com o §e§lbotão direito do mouse §bsegurando o §6§lQR Code §bcaso queira §c§lCANCELAR §bo pedido.");
        });
    }

    public static Map<UUID, Order> getOrders() {
        return orders;
    }

    public static void removeOrder(UUID playerUUID) {
        orders.remove(playerUUID);
    }

    public static List<Order> getPendingPaymentIds() {
        List<Order> pendingPayments = new ArrayList<>();
        for (Order order : orders.values()) {
            if (order.getStatus() == PaymentStatus.PENDING) {
                pendingPayments.add(order);
            }
        }
        return pendingPayments;
    }

    public static void updateOrderStatus(UUID playerUUID, PaymentStatus newStatus) {
        Order order = orders.get(playerUUID);
        if (order != null) {
            order.setStatus(newStatus);
        }
    }
}