package br.com.din.pixcraft.commands;

import br.com.din.pixcraft.message.Messages;
import br.com.din.pixcraft.order.OrderManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.List;

public class PixCommand implements CommandExecutor, TabCompleter {
    private final JavaPlugin plugin;
    private final OrderManager orderManager;

    public PixCommand(JavaPlugin plugin, OrderManager orderManager) {
        this.plugin = plugin;
        this.orderManager = orderManager;
        plugin.getCommand("pix").setExecutor(this);
        plugin.getCommand("pix").setTabCompleter(this);
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (strings.length == 0) return false;

        if (commandSender instanceof Player) {
            if (commandSender.hasPermission("pixcraft.command.pix")) {
                if (strings[0].equals("cancel")) {
                    Player player = (Player) commandSender;
                    if (!orderManager.getOrders().containsKey(player.getUniqueId())) {
                        player.sendMessage(Messages.PREFIX + " §cVocê não tem nenhum pagamento pendente.");
                        return true;
                    } else {
                        player.sendMessage(Messages.PREFIX + " §eCancelando pagamento...");
                        orderManager.getOrder(player.getUniqueId()).cancel();
                        return true;
                    }
                }
            } else {
                commandSender.sendMessage(Messages.COMMAND_NO_PERMISSION);
                return true;
            }
        } else {
            commandSender.sendMessage(Messages.COMMAND_ONLY_PLAYER);
            return true;
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        return Arrays.asList("cancel");
    }
}
