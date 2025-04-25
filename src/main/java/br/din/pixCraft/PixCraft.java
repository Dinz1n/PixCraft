package br.din.pixCraft;

import br.din.pixCraft.commands.PcCommand;
import br.din.pixCraft.gui.confirmcancel.CCGBuilder;
import br.din.pixCraft.listeners.PaymentListener;
import br.din.pixCraft.listeners.PlayerQuitListener;
import br.din.pixCraft.listeners.QrCodeProtect;
import br.din.pixCraft.payment.order.Order;
import br.din.pixCraft.payment.order.OrderManager;
import br.din.pixCraft.payment.PaymentStatusChecker;
import br.din.pixCraft.payment.gateway.MercadoPagoAPI;
import br.din.pixCraft.payment.webhook.WebhookServer;
import br.din.pixCraft.product.ProductManager;
import br.din.pixCraft.utils.AsciiArt;
import br.din.pixCraft.utils.ItemStackUtil;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

public final class PixCraft extends JavaPlugin {
    private final PaymentStatusChecker statusChecker = new PaymentStatusChecker(this);
    WebhookServer webhookServer;
    private static PixCraft instance;

    @Override
    public void onEnable() {
        AsciiArt.printAsciiArt(getLogger());
        instance = this;

        getLogger().info("Carregando config.yml...");
        saveDefaultConfig();
        MercadoPagoAPI.setAccessToken(getConfig().getString("mercadopago.access-token"));

        getLogger().info("Carregando produtos...");
        ProductManager productManager = new ProductManager();

        getLogger().info("Registrando ouvintes...");
        QrCodeProtect qrCodeProtect = new QrCodeProtect(this);
        PaymentListener paymentListener = new PaymentListener(this);
        PlayerQuitListener playerQuitListener = new PlayerQuitListener(this);

        if (getConfig().getBoolean("mercadopago.webhook.enabled")) {
            webhookServer = new WebhookServer(getConfig().getInt("mercadopago.webhook.port"));
            webhookServer.start();
        } else {
            statusChecker.start(100L);
        }
        getLogger().info("Registrando comandos...");
        PcCommand pcCommand = new PcCommand(this, productManager, new CCGBuilder(this));

        getLogger().info("Plugin habilitado com sucesso!");
    }

    @Override
    public void onDisable() {
        getLogger().info("Cancelando pagamentos pendentes...");
        for (Order order : OrderManager.getPendingPaymentIds()) {
            Player player = Bukkit.getPlayer(order.getPlayerUUID());
            ItemStackUtil.removeItemByData(player, new NamespacedKey(this, "paymentId"), PersistentDataType.LONG, order.getPaymentID());
            order.cancel();
        }

        getLogger().info("Desligando Webhook Server...");
        if (getConfig().getBoolean("mercadopago.webhook.enabled")) {
            webhookServer.stop();
        }
    }

    public static PixCraft getInstance() {
        return instance;
    }
}