package br.com.din.pixcraft.payment.verification;

import br.com.din.pixcraft.listeners.custom.PaymentUpdateEvent;
import br.com.din.pixcraft.order.Order;
import br.com.din.pixcraft.order.OrderManager;
import br.com.din.pixcraft.payment.PaymentStatus;
import br.com.din.pixcraft.payment.gateway.PaymentProvider;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

public class Polling implements PaymentChecker {
    private final JavaPlugin plugin;
    private BukkitTask task;
    private final PaymentProvider paymentProvider;
    private final OrderManager orderManager;

    public Polling(JavaPlugin plugin, PaymentProvider paymentProvider, OrderManager orderManager) {
        this.plugin = plugin;
        this.paymentProvider = paymentProvider;
        this.orderManager = orderManager;
    }

    @Override
    public void start() {
        task = Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, () -> {
            if (!orderManager.getOrders().isEmpty()) {
                for (Order order : orderManager.getOrders().values()) {
                    paymentProvider.getStatus(order.getPaymentData().getId(), paymentStatus -> {
                        if (!paymentStatus.equals(PaymentStatus.PENDING)) {
                            order.getPaymentData().setStatus(paymentStatus);
                            orderManager.addOrder(order);
                            Bukkit.getScheduler().runTask(plugin, () -> {
                                Bukkit.getPluginManager().callEvent(new PaymentUpdateEvent(order));
                            });
                        }
                    });
                }
            }
        }, 100L, 100L);
    }

    @Override
    public void stop() {
        task.cancel();
    }
}