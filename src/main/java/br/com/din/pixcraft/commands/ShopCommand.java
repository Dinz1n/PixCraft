package br.com.din.pixcraft.commands;

import br.com.din.pixcraft.shop.ShopManager;

import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Field;

public class ShopCommand extends Command {
    private final JavaPlugin plugin;
    private final ShopManager shopManager;

    public ShopCommand(JavaPlugin plugin, String commandName, ShopManager shopManager) {
        super(commandName);
        this.plugin = plugin;

        this.setDescription("Abre o menu da loja.");
        this.setUsage("/" + commandName);
        this.setPermission("pixcraft.shop.command");
        this.setPermissionMessage("§c[PixCraft] Você não tem permissão para executar este comando.");

        this.shopManager = shopManager;

        registerCommand(this);
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (args.length != 0) return false;

        if (!(sender instanceof Player)) {
            sender.sendMessage("§c[PixCraft] Este comando só pode ser executado por jogadores.");
            return true;
        }

        if (!sender.hasPermission("pixcraft.shop.command")) {
            sender.sendMessage("§c[PixCraft] Você não tem permissão para executar este comando.");
            return true;
        }

        shopManager.open((Player) sender);
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
