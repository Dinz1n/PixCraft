package br.com.din.pixcraft.commands;

import br.com.din.pixcraft.gui.shop.ShopGui;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.util.List;

public class ShopCommand extends Command {
    private final JavaPlugin plugin;
    private final ShopGui shopGui;

    public ShopCommand(JavaPlugin plugin, List<String> commandAliases, ShopGui shopGui) {
        super(commandAliases.get(0));
        this.plugin = plugin;

        this.setDescription("Abre o menu da loja.");
        this.setUsage("/" + commandAliases.get(0));
        this.setAliases(commandAliases.subList(1, commandAliases.size()));
        this.setPermission("pixcraft.shop");
        this.setPermissionMessage("Permite abrir a loja");

        registerCommand(this);

        this.shopGui = shopGui;
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
        if (args.length != 0) return false;

        if (!(sender instanceof Player)) {
            sender.sendMessage("§cEste comando só pode ser executado por jogadores.");
            return true;
        }

        if (!sender.hasPermission("pixcraft.shop")) {
            sender.sendMessage("§cVocê não tem permissão para executar este comando.");
            return true;
        }

        shopGui.openGui((Player) sender, plugin.getConfig().getString("shop.category-main"));
        return true;
    }

    private void registerCommand(Command command) {
        try {
            Field campoCommandMap = plugin.getServer().getPluginManager().getClass().getDeclaredField("commandMap");
            campoCommandMap.setAccessible(true);

            CommandMap commandMap = (CommandMap) campoCommandMap.get(plugin.getServer().getPluginManager());
            commandMap.register(plugin.getDescription().getName(), command);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
