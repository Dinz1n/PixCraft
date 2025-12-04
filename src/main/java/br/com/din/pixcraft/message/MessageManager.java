package br.com.din.pixcraft.message;

import br.com.din.pixcraft.yaml.YamlDataManager;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;

public class MessageManager extends YamlDataManager {
    public static String PREFIX;
    public static String COMMAND_NO_PERMISSION;
    public static String COMMAND_ONLY_PLAYER;
    public static String PRODUCT_NO_PERMISSION;
    public static String PLUGIN_RELOAD_SUCCESS;
    public static String MENU_NOT_FOUND;
    public static String PRODUCT_NOT_FOUND;
    public static String ORDER_LIMIT_ONE;
    public static String PAYMENT_APPROVED;
    public static String PAYMENT_CANCELLED;
    public static String PAYMENT_CREATED;
    public static String PAYMENT_CREATION_ERROR;
    public static String PAYMENT_UNEXPECTED_ERROR;

    public MessageManager(JavaPlugin plugin) {
        super(plugin, "messages.yml");
        loadData();
    }

    @Override
    protected void loadData() {
        ConfigurationSection fileConfig = getFileConfiguration();

        PREFIX = fileConfig.getString("prefix").replace("&", "§");
        COMMAND_NO_PERMISSION = fileConfig.getString("command-no-permission").replace("{prefix}", PREFIX).replace("&", "§");
        PRODUCT_NO_PERMISSION = fileConfig.getString("product-no-permission").replace("{prefix}", PREFIX).replace("&", "§");
        PLUGIN_RELOAD_SUCCESS = fileConfig.getString("plugin-reload-success").replace("{prefix}", PREFIX).replace("&", "§");
        COMMAND_ONLY_PLAYER = fileConfig.getString("command-only-player").replace("{prefix}", PREFIX).replace("&", "§");
        MENU_NOT_FOUND = fileConfig.getString("menu-not-found").replace("{prefix}", PREFIX).replace("&", "§");
        PRODUCT_NOT_FOUND = fileConfig.getString("product-not-found").replace("{prefix}", PREFIX).replace("&", "§");
        ORDER_LIMIT_ONE = fileConfig.getString("order-limit-one").replace("{prefix}", PREFIX).replace("&", "§");
        PAYMENT_APPROVED = fileConfig.getString("payment-approved").replace("{prefix}", PREFIX).replace("&", "§");
        PAYMENT_CANCELLED = fileConfig.getString("payment-cancelled").replace("{prefix}", PREFIX).replace("&", "§");
        PAYMENT_CREATED = fileConfig.getString("payment-created").replace("{prefix}", PREFIX).replace("&", "§");
        PAYMENT_CREATION_ERROR = fileConfig.getString("payment-creation-error").replace("{prefix}", PREFIX).replace("&", "§");
        PAYMENT_UNEXPECTED_ERROR = fileConfig.getString("payment-unexpected-error").replace("{prefix}", PREFIX).replace("&", "§");
    }
}
