package br.din.pixCraft.payment;

import br.din.pixCraft.listeners.custom.PaymentStatusUpdateEvent;
import br.din.pixCraft.payment.order.OrderManager;
import br.din.pixCraft.payment.order.Order;
import br.din.pixCraft.payment.gateway.MercadoPagoAPI;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

public class PaymentStatusChecker extends BukkitRunnable {
    private final JavaPlugin plugin;

    public PaymentStatusChecker(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        List<Order> pendingPayments = OrderManager.getPendingPaymentIds();
        if (pendingPayments.isEmpty()) return;

        for (Order order : pendingPayments) {
            PaymentStatus status = MercadoPagoAPI.getPaymentStatus(order.getPaymentID());
            if (status == null) continue;

            if (order == null || order.getStatus() == status) continue;

            OrderManager.updateOrderStatus(order.getPlayerUUID(), status);

            if (status != PaymentStatus.PENDING) {
                Bukkit.getScheduler().runTask(plugin, () ->
                        Bukkit.getPluginManager().callEvent(new PaymentStatusUpdateEvent(order))
                );
            }
        }
    }

    public void start(long intervalTicks) {
        this.runTaskTimerAsynchronously(plugin, 0L, intervalTicks);
    }
}