package br.din.pixCraft.listeners;

import br.din.pixCraft.PixCraft;
import br.din.pixCraft.order.Order;
import br.din.pixCraft.order.OrderManager;
import br.din.pixCraft.payment.PaymentStatus;
import br.din.pixCraft.payment.gateway.MercadoPagoAPI;

import br.din.pixCraft.utils.ItemStackUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
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
        if (isQrMap(item, event.getPlayer())) {
            if (event.getAction().isRightClick()) {
                Order order = OrderManager.getOrders().get(event.getPlayer().getUniqueId());
                event.getPlayer().sendMessage("§cCancelando pagamento...");
                ItemStackUtil.removeItemByData(event.getPlayer(), key, PersistentDataType.LONG, order.getPaymentID());
                MercadoPagoAPI.cancelPayment(item.getItemMeta().getPersistentDataContainer().get(key, PersistentDataType.LONG));
            }
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();

        if (isQrMap(event.getCurrentItem(), player)) {
            event.setCancelled(true);
        }

        if (event.getClick() == ClickType.NUMBER_KEY) {
            ItemStack hotbarItem = event.getWhoClicked().getInventory().getItem(event.getHotbarButton());
            if (isQrMap(hotbarItem, player)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        if (isQrMap(event.getOldCursor(), (Player) event.getWhoClicked())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventoryMoveItem(InventoryMoveItemEvent event) {
        if (event.getSource().getHolder() instanceof Player) {
            Player player = (Player) event.getSource().getHolder();
            if (isQrMap(event.getItem(), player)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        if (isQrMap(event.getItemDrop().getItemStack(), event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerSwapHandItems(PlayerSwapHandItemsEvent event) {
        if (isQrMap(event.getMainHandItem(), event.getPlayer()) || isQrMap(event.getOffHandItem(), event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventoryCreative(InventoryCreativeEvent event) {
        if (isQrMap(event.getCursor(), (Player) event.getWhoClicked())) {
            event.setCancelled(true);
        }
    }

    private boolean isQrMap(ItemStack item, Player player) {
        if (item == null || item.getType() != Material.FILLED_MAP) return false;
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return false;
        Order order = OrderManager.getOrders().get(player.getUniqueId());
        if (order != null) {
            if (order.getStatus().equals(PaymentStatus.PENDING)) return true;
        }
        return false;
    }
}