package br.din.pixCraft.order;

import br.din.pixCraft.customMap.MapHandler;
import br.din.pixCraft.payment.PaymentStatus;
import br.din.pixCraft.payment.gateway.MercadoPagoAPI;
import br.din.pixCraft.product.Product;
import br.din.pixCraft.product.ProductManager;
import br.din.pixCraft.util.QrCodeGenerator;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.awt.image.BufferedImage;
import java.util.*;

public class OrderManager {
    private static final Map<UUID, List<Order>> orders = new HashMap<>();

    public static void processOrder(Player player, String productId) {
        if (player == null || productId == null) {
            return;
        }

        Product product = ProductManager.getProduct(productId);
        if (product == null) {
            player.sendMessage("§cProduto não encontrado.");
            return;
        }

        double price = product.getPrice();
        if (product.isTax()) {
            price += (0.99 / 100) * price; // Aplica taxa ao preço
        }

        Long paymentId = MercadoPagoAPI.createPayment(product.getDisplayName(), price, player.getName());
        if (paymentId == null) {
            player.sendMessage("§cErro ao criar o pagamento.");
            return;
        }

        Order order = new Order(player.getUniqueId(), paymentId, product);
        orders.computeIfAbsent(player.getUniqueId(), k -> new ArrayList<>()).add(order);

        BufferedImage qrCodeImage = QrCodeGenerator.generateQrImage(MercadoPagoAPI.getQrCodeBase64(paymentId));
        ItemStack qrCodeMap = MapHandler.createQrMap(qrCodeImage, player.getWorld(), paymentId);

        player.getInventory().setItem(3, qrCodeMap);
        player.getInventory().setHeldItemSlot(3);

        player.sendMessage("§bVocê recebeu um §6§lQR Code §bpara realizar o pagamento.");
        player.sendMessage("§bClique com o §e§lbotão direito do mouse §bsegurando o §6§lQR Code §bcaso queira §c§lCANCELAR §bo pedido.");
    }

    public static List<Order> getPlayerOrders(UUID playerUUID) {
        return orders.getOrDefault(playerUUID, Collections.emptyList());
    }

    public static Order getOrderById(Long orderId) {
        if (orderId == null) return null;

        return orders.values().stream()
                .flatMap(List::stream)
                .filter(order -> orderId.equals(order.getPaymentID()))
                .findFirst()
                .orElse(null);
    }

    public static void removeOrder(Long paymentId) {
        for (UUID playerUUID : orders.keySet()) {
            List<Order> playerOrders = orders.get(playerUUID);

            if (playerOrders != null) {
                playerOrders.removeIf(order -> order.getPaymentID().equals(paymentId));

                // Se a lista ficar vazia, removemos o jogador do mapa
                if (playerOrders.isEmpty()) {
                    orders.remove(playerUUID);
                }
            }
        }
    }

    public static List<Long> getPendingPaymentIds() {
        List<Long> pendingPayments = new ArrayList<>();
        for (List<Order> orderList : orders.values()) {
            for (Order order : orderList) {
                if (order.getStatus() == PaymentStatus.PENDING) {
                    pendingPayments.add(order.getPaymentID());
                }
            }
        }
        return pendingPayments;
    }

    public static void updateOrderStatus(Long paymentId, PaymentStatus newStatus) {
        Order order = getOrderById(paymentId);
        if (order != null) {
            order.setStatus(newStatus);
        }
    }
}