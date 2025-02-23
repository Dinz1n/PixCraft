package br.din.pixCraft.payment;

import br.din.pixCraft.listeners.custom.PaymentStatusUpdateEvent;
import br.din.pixCraft.order.OrderManager;
import br.din.pixCraft.order.Order;
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
        List<Long> pendingPayments = OrderManager.getPendingPaymentIds();
        if (pendingPayments.isEmpty()) return;

        for (Long paymentId : pendingPayments) {
            PaymentStatus status = MercadoPagoAPI.getPaymentStatus(paymentId);
            if (status == null) continue;

            Order order = OrderManager.getOrderById(paymentId);
            if (order == null || order.getStatus() == status) continue;

            OrderManager.updateOrderStatus(paymentId, status);

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
