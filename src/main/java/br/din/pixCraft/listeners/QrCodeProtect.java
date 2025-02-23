package br.din.pixCraft.listeners;

import br.din.pixCraft.PixCraft;
import br.din.pixCraft.order.Order;
import br.din.pixCraft.order.OrderManager;
import br.din.pixCraft.payment.PaymentStatus;
import br.din.pixCraft.payment.gateway.MercadoPagoAPI;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import org.bukkit.event.inventory.*;

public class QrCodeProtect implements Listener {
    private final PixCraft plugin;
    private final NamespacedKey key;

    public QrCodeProtect(PixCraft plugin) {
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
        key = new NamespacedKey(plugin, "paymentId");
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        ItemStack item = event.getItem();
        if (isQrMap(item)) {
            if (event.getAction().isRightClick()) {
                Order order = OrderManager.getOrderById(item.getItemMeta().getPersistentDataContainer().get(key, PersistentDataType.LONG));

                event.getPlayer().sendMessage("§cCancelando pagamento...");
                MercadoPagoAPI.cancelPayment(item.getItemMeta().getPersistentDataContainer().get(key, PersistentDataType.LONG));
            }
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (isQrMap(event.getCurrentItem())) {
            event.setCancelled(true);
        }

        if (event.getClick() == ClickType.NUMBER_KEY) {
            ItemStack hotbarItem = event.getWhoClicked().getInventory().getItem(event.getHotbarButton());
            if (isQrMap(hotbarItem)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        if (isQrMap(event.getOldCursor())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventoryMoveItem(InventoryMoveItemEvent event) {
        if (isQrMap(event.getItem())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        if (isQrMap(event.getItemDrop().getItemStack())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerSwapHandItems(PlayerSwapHandItemsEvent event) {
        if (isQrMap(event.getMainHandItem()) || isQrMap(event.getOffHandItem())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onItemSpawn(ItemSpawnEvent event) {
        if (isQrMap(event.getEntity().getItemStack())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventoryCreative(InventoryCreativeEvent event) {
        if (isQrMap(event.getCursor())) {
            event.setCancelled(true);
        }
    }

    private boolean isQrMap(ItemStack item) {
        if (item == null || item.getType() != Material.FILLED_MAP) return false;
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return false;
        Order order = OrderManager.getOrderById(meta.getPersistentDataContainer().get(key, PersistentDataType.LONG));
        if (order != null) {
            if (order.getStatus().equals(PaymentStatus.PENDING)) return true;
        }
        return false;
    }
}

