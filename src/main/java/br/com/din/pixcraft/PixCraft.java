package br.com.din.pixcraft;

import br.com.din.pixcraft.commands.PCCommand;
import br.com.din.pixcraft.commands.ShopCommand;
import br.com.din.pixcraft.listeners.PaymentUpdate;
import br.com.din.pixcraft.listeners.QrCodeProtect;
import br.com.din.pixcraft.message.MessageManager;
import br.com.din.pixcraft.order.Order;
import br.com.din.pixcraft.order.OrderManager;
import br.com.din.pixcraft.payment.gateway.MercadoPagoService;
import br.com.din.pixcraft.payment.gateway.PaymentProvider;
import br.com.din.pixcraft.payment.verification.PaymentChecker;
import br.com.din.pixcraft.payment.verification.Polling;
import br.com.din.pixcraft.payment.verification.Webhook;
import br.com.din.pixcraft.shop.ShopManager;

import br.com.din.pixcraft.product.ProductManager;

import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public final class PixCraft extends JavaPlugin {
    private static JavaPlugin instance;
    private Logger logger;
    private MessageManager messageManager;
    private ShopManager shopManager;
    private OrderManager orderManager;
    private ProductManager productManager;
    private PaymentProvider paymentProvider;
    private PaymentChecker paymentChecker;

    @Override
    public void onEnable() {
        printAsciiArt();

        instance = this;
        logger = getLogger();

        logger.info("Carregando arquivos de configuração...");
        getConfig();
        saveDefaultConfig();
        messageManager = new MessageManager(this);
        productManager = new ProductManager(this);
        orderManager = new OrderManager(this, paymentProvider, productManager);
        shopManager = new ShopManager(this, orderManager, productManager);

        logger.info("Carregando provedor de pagamento (MercadoPago)...");
        paymentProvider = new MercadoPagoService();
        paymentProvider.setAccessToken(getConfig().getString("payment.provider.access-token"));


        if (getConfig().getBoolean("payment.webhook.enabled")) {
            logger.info("Carregando método de verificação de pagamento (webhook)...");
            paymentChecker = new Webhook(this, paymentProvider, getConfig().getInt("payment.webhook.port"), orderManager);
            paymentChecker.start();
        } else {
            logger.info("Carregando método de verificação de pagamento (polling)...");
            paymentChecker = new Polling(this, paymentProvider, orderManager);
            paymentChecker.start();
        }

        logger.info("Registrando listeners...");
        new QrCodeProtect(this, orderManager);
        new PaymentUpdate(this, orderManager);

        logger.info("Registrando comandos...");
        new PCCommand(this, messageManager, productManager, shopManager, paymentProvider);
        new ShopCommand(this, getConfig().getString("shop.command-name"), shopManager);

        logger.info("Plugin inicializado com sucesso!");
    }

    @Override
    public void onDisable() {
        if (paymentChecker != null) {
            logger.info("Encerrando verificador de pagamento...");
            paymentChecker.stop();
        }
        if (getConfig().getBoolean("payment.cancel-on-leave")) {
            logger.info("Cancelando pagamentos pendentes...");
            for (Order order : orderManager.getOrders().values()) {
                order.cancel();
                orderManager.removeOrder(order.getId());
            }
        }
    }

    private void printAsciiArt() {
        ConsoleCommandSender console = Bukkit.getConsoleSender();
        String version = getDescription().getVersion();

        String[] lines = {
                "§c-----------------------",
                "§a __",
                "§a|__) | \\_/ §6by Din",
                "§a|    | / \\ §6v" + version,
                "§b __   __        ___ ___ ",
                "§b/  ` |__)  /\\  |__   |  ",
                "§b\\__, |  \\ /--\\ |     |  ",
                "§c-----------------------"
        };

        for (String line : lines) {
            console.sendMessage(line);
        }
    }

    public static JavaPlugin getInstance() {
        return instance;
    }
}