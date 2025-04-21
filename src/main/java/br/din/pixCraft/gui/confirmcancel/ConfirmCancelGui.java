package br.din.pixCraft.gui.confirmcancel;

import br.din.pixCraft.PixCraft;
import br.din.pixCraft.payment.order.OrderManager;
import br.din.pixCraft.product.Product;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

public class ConfirmCancelGui implements Listener{
    private static final JavaPlugin plugin = PixCraft.getInstance();
    private static final NamespacedKey key = new NamespacedKey(plugin, "confirmCancelButton");
    private final Player player;
    private final Product product;
    private final CCGBuilder builder;
    private final Inventory gui;

    public ConfirmCancelGui(Player player, Product product, CCGBuilder ccgBuilder, Inventory gui) {
        this.player = player;
        this.product = product;
        this.builder = ccgBuilder;
        this.gui = gui;
    }

    public void showGui() {
        Bukkit.getPluginManager().registerEvents(this, plugin);
        player.openInventory(gui);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getClickedInventory().getType().equals(InventoryType.CHEST) && event.getClickedInventory().equals(gui)) {
            if (event.getWhoClicked() == player) {
                event.setCancelled(true);
                if (event.getCurrentItem() != null) {
                    String data = event.getCurrentItem().getItemMeta().getPersistentDataContainer().get(key, PersistentDataType.STRING);
                    if (data == null) return;
                    switch (data) {
                        case "confirm-button":
                            OrderManager.processOrder(player, product.getProductId());
                            event.getWhoClicked().closeInventory();
                            HandlerList.unregisterAll(this);
                            break;
                        case "cancel-button":
                            event.getWhoClicked().closeInventory();
                            HandlerList.unregisterAll(this);
                            break;
                    }
                }
            }
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (event.getInventory().getType().equals(InventoryType.CHEST) && event.getInventory().equals(gui)) {
            HandlerList.unregisterAll(this);
        }
    }
}