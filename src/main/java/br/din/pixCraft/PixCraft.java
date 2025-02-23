package br.din.pixCraft;

import br.din.pixCraft.commands.PcCommand;
import br.din.pixCraft.listeners.PaymentListener;
import br.din.pixCraft.listeners.QrCodeProtect;
import br.din.pixCraft.payment.PaymentStatusChecker;
import br.din.pixCraft.payment.gateway.MercadoPagoAPI;
import br.din.pixCraft.payment.webhook.WebhookServer;
import br.din.pixCraft.product.ProductManager;
import br.din.pixCraft.util.AsciiArt;
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
        if (getConfig().getBoolean("mercadopago.endpoint.enable")) {
            webhookServer = new WebhookServer(getConfig().getInt("mercadopago.endpoint.port"));
            webhookServer.start();
        } else {
            statusChecker.start(100L);
        }
        getLogger().info("Registrando comandos...");
        PcCommand pcCommand = new PcCommand();

        getLogger().info("Plugin habilitado com sucesso!");
    }

    @Override
    public void onDisable() {
        if (getConfig().getBoolean("mercadopago.endpoint.enable")) {
            webhookServer.stop();
        }
    }

    public static PixCraft getInstance() {
        return instance;
    }
}
