package br.com.din.pixcraft.listeners;

import br.com.din.pixcraft.listeners.custom.PaymentUpdateEvent;
import br.com.din.pixcraft.map.CustomMapCreator;
import br.com.din.pixcraft.utils.NBTItemUtils;
import br.com.din.pixcraft.order.Order;
import br.com.din.pixcraft.order.OrderManager;
import br.com.din.pixcraft.payment.PaymentStatus;

import br.com.din.pixcraft.utils.QrCodeGenerator;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.awt.image.BufferedImage;


public class QrCodeProtect implements Listener {
    private final JavaPlugin plugin;
    private final OrderManager orderManager;

    public QrCodeProtect(JavaPlugin plugin, OrderManager orderManager) {
        this.plugin = plugin;
        this.orderManager = orderManager;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getClick() == ClickType.NUMBER_KEY) {
            int hotbarButton = event.getHotbarButton();
            ItemStack hotbarItem = event.getWhoClicked().getInventory().getItem(hotbarButton);

            if (isQrMap(hotbarItem)) {
                event.setCancelled(true);
                return;
            }
        }

        ItemStack current = event.getCurrentItem();
        ItemStack cursor = event.getCursor();

        if (isQrMap(current) || isQrMap(cursor)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        event.setCancelled(isQrMap(event.getItemDrop().getItemStack()));
    }

    @EventHandler
    public void onPlayerSwapHandItems(PlayerSwapHandItemsEvent event) {
        event.setCancelled(isQrMap(event.getOffHandItem()));
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        if (!orderManager.getOrders().containsKey(player.getUniqueId())) {
            removeQrMap(player);
            return;
        }

        Order order = orderManager.getOrder(player.getUniqueId());

        if (!order.getPayment().getStatus().equals(PaymentStatus.PENDING)) {
            removeQrMap(player);
        } else {

            for (ItemStack itemStack : player.getInventory().getContents()) {
                if (isQrMap(itemStack)) {
                    if (isQrMap(itemStack)) player.getInventory().remove(itemStack);
                }
            }

            BufferedImage qrImage = QrCodeGenerator.generate(order.getPayment().getQrData(), 128, 128);
            ConfigurationSection qrCodeMapSection = plugin.getConfig().getConfigurationSection("qr-code-map");
            ItemStack qrMap = CustomMapCreator.create(
                    qrImage, player.getWorld(),
                    qrCodeMapSection.getString("displayname"),
                    qrCodeMapSection.getStringList("lore"));
            qrMap = NBTItemUtils.setTag(qrMap, "pixcraft_order_id", order.getId());

            int slotMap = qrCodeMapSection.getInt("slot");
            if (slotMap < 0 || slotMap > 8) slotMap = 3;
            player.getInventory().setHeldItemSlot(slotMap);
            player.getInventory().setItem(slotMap, qrMap);
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        Order order = orderManager.getOrder(player.getUniqueId());

        if (order != null && plugin.getConfig().getBoolean("payment.cancel-on-leave")) {
            order.cancel();
            orderManager.removeOrder(player.getUniqueId());
            removeQrMap(player);
        }
    }

    @EventHandler
    public void onPaymentUpdate(PaymentUpdateEvent event) {
        Player player = Bukkit.getPlayer(event.getOrder().getId());
        if (player != null && player.isOnline()) {
            removeQrMap(player);
        }
    }

    private void removeQrMap(Player player) {
        ItemStack qrMap = null;
        for (ItemStack itemStack : player.getInventory().getContents()) {
            if (isQrMap(itemStack)) {
                qrMap = itemStack;
            }
        }

        if (qrMap != null) {
            player.getInventory().remove(qrMap);
        }
    }

    private boolean isQrMap(ItemStack itemStack) {
        if (itemStack == null || itemStack.getType().equals(Material.AIR)) return false;
        if (NBTItemUtils.hasTag(itemStack, "pixcraft_order_id")) return true;
        return false;
    }
}