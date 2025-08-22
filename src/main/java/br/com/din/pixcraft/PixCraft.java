package br.com.din.pixcraft;

import br.com.din.pixcraft.commands.PCCommand;
import br.com.din.pixcraft.commands.ShopCommand;
import br.com.din.pixcraft.listeners.PaymentUpdate;
import br.com.din.pixcraft.listeners.QrCodeProtect;
import br.com.din.pixcraft.map.CustomMapCreator;
import br.com.din.pixcraft.order.Order;
import br.com.din.pixcraft.order.OrderManager;
import br.com.din.pixcraft.payment.gateway.MercadoPagoService;
import br.com.din.pixcraft.payment.gateway.PaymentProvider;
import br.com.din.pixcraft.payment.verification.PaymentChecker;
import br.com.din.pixcraft.payment.verification.Polling;
import br.com.din.pixcraft.shop.ShopManager;

import br.com.din.pixcraft.product.ProductManager;
import de.tr7zw.changeme.nbtapi.NBT;

import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import javax.security.auth.login.Configuration;
import java.util.logging.Logger;

public final class PixCraft extends JavaPlugin {
    private static JavaPlugin instance;
    private Logger logger;
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

        logger.info("Inicializando NBT-API...");
        if (!NBT.preloadApi()) {
            getLogger().warning("NBT-API wasn't initialized properly, disabling the plugin");
            getPluginLoader().disablePlugin(this);
            return;
        }

        logger.info("Carregando provedor de pagamento (MercadoPago)...");
        paymentProvider = new MercadoPagoService();
        paymentProvider.setAccessToken(getConfig().getString("payment.provider.access-token"));

        logger.info("Carregando arquivos de configuração...");
        getConfig();
        saveDefaultConfig();
        productManager = new ProductManager(this, "products.yml");
        orderManager = new OrderManager(this, paymentProvider, new CustomMapCreator(), productManager);
        shopManager = new ShopManager(this, orderManager, productManager);

        logger.info("Carregando método de verificação de pagamento (polling)...");
        paymentChecker = new Polling(this, paymentProvider, orderManager);
        paymentChecker.start();

        logger.info("Registrando listeners...");
        new QrCodeProtect(this, orderManager);
        new PaymentUpdate(this, orderManager);

        logger.info("Registrando comandos...");
        new PCCommand(this, shopManager.getProductManager(), shopManager.getCategoryManager(), paymentProvider);
        new ShopCommand(this, getConfig().getString("shop.command-name"), shopManager.getShopGui());

        logger.info("Plugin inicializado com sucesso!");
    }

    @Override
    public void onDisable() {
        logger.info("Encerrando verificador de pagamento...");
        paymentChecker.stop();
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