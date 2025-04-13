package br.din.pixCraft.gui;

import br.din.pixCraft.product.Product;
import br.din.pixCraft.utils.ItemStackUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;

import java.util.List;

public class ConfirmCancelGui implements Listener{
    public static void showGui(Player player, Product product) {
        Inventory inventory = Bukkit.createInventory(null, 3*9, "Confirmar pedido");
        inventory.setItem(11, ItemStackUtil.create(
                Material.LIME_WOOL,
                "§aConfirmar",
                null,
                1
        ));

        inventory.setItem(15, ItemStackUtil.create(
                Material.RED_WOOL,
                "§cCancelar",
                null,
                1
        ));

        inventory.setItem(13, ItemStackUtil.create(
                Material.NAME_TAG,
                product.getDisplayName(),
                List.of("", "§aR$" + product.getPrice() + " no §b§lPIX"),
                1
        ));

        player.openInventory(inventory);
    }
}
