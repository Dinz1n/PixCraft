package br.com.din.pixcraft.listeners;

import br.com.din.pixcraft.discord.DiscordWebhook;
import br.com.din.pixcraft.discord.WebhookConfigLoader;
import br.com.din.pixcraft.listeners.custom.PaymentUpdateEvent;
import br.com.din.pixcraft.order.Order;
import br.com.din.pixcraft.order.OrderManager;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;

public class PaymentUpdate implements Listener {
    private final JavaPlugin plugin;
    private final OrderManager orderManager;

    public PaymentUpdate(JavaPlugin plugin, OrderManager orderManager) {
        this.plugin = plugin;
        this.orderManager = orderManager;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPaymentUpdate(PaymentUpdateEvent event) {
        Order order = event.getOrder();
        handleOrder(order, true);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (!orderManager.getOrders().containsKey(event.getPlayer().getUniqueId())) return;
        handleOrder(orderManager.getOrder(event.getPlayer().getUniqueId()), false);
    }

    private void handleOrder(Order order, boolean notifyDiscord) {
        Player player = Bukkit.getPlayer(order.getId());

        switch (order.getPayment().getStatus()) {
            case APPROVED:
                if (player != null && player.isOnline()) {
                    player.sendMessage("§a[PixCraft] Pagamento aprovado!");
                    for (String command : order.getProduct().getReward()) {
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.replace("{player}", player.getName()));
                    }
                    orderManager.removeOrder(order.getId());
                }

                if (notifyDiscord) sendDiscordNotification(order, player);
                break;

            case CANCELLED:
                if (player != null && player.isOnline()) {
                    player.sendMessage("§c[PixCraft] Pagamento cancelado.");
                    orderManager.removeOrder(order.getId());
                }
                break;

            case PENDING:
                break;
        }
    }

    private void sendDiscordNotification(Order order, Player player) {
        ConfigurationSection discordSection = plugin.getConfig().getConfigurationSection("discord.notification");
        if (discordSection == null) return;

        boolean enabled = discordSection.getBoolean("enable", false);
        if (!enabled) return;

        String webhookUrl = discordSection.getString("webhook-url");
        if (webhookUrl == null || webhookUrl.isEmpty()) return;

        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("player_name", player != null ? player.getName() : order.getPayerName());
        placeholders.put("product", order.getProduct().getName());
        placeholders.put("price", String.valueOf(order.getProduct().getPrice())).replace(".", ",");

        ConfigurationSection messageSection = discordSection.getConfigurationSection("message");
        if (messageSection == null) return;

        try {
            DiscordWebhook webhook = WebhookConfigLoader.fromConfig(webhookUrl, messageSection, placeholders);
            webhook.send();
        } catch (Exception e) {
            plugin.getLogger().warning("Falha ao enviar webhook do Discord: " + e.getMessage());
        }
    }
}