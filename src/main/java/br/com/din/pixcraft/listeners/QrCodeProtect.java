package br.com.din.pixcraft.listeners;

import br.com.din.pixcraft.listeners.custom.PaymentUpdateEvent;
import br.com.din.pixcraft.qrmap.MapCompatibility;
import br.com.din.pixcraft.qrmap.QrCodeMapCreator;
import br.com.din.pixcraft.qrmap.QrCodeMapRenderer;
import br.com.din.pixcraft.order.Order;
import br.com.din.pixcraft.order.OrderManager;
import br.com.din.pixcraft.payment.PaymentStatus;

import com.cryptomorin.xseries.XMaterial;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;
import org.bukkit.plugin.java.JavaPlugin;


public class QrCodeProtect implements Listener {
    private final JavaPlugin plugin;
    private final OrderManager orderManager;

    public QrCodeProtect(JavaPlugin plugin, OrderManager orderManager) {
        this.plugin = plugin;
        this.orderManager = orderManager;

        Bukkit.getPluginManager().registerEvents(this, plugin);
        if (classExists("org.bukkit.event.player.PlayerSwapHandItemsEvent")) {
            Bukkit.getPluginManager().registerEvents(new Listener() {
                @EventHandler
                public void onPlayerSwapHandItems(org.bukkit.event.player.PlayerSwapHandItemsEvent event) {
                    event.setCancelled(isQrMap(event.getOffHandItem()));
                }
            }, plugin);
        }
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

            ConfigurationSection qrCodeMapSection = plugin.getConfig().getConfigurationSection("qr-code-map");
            ItemStack qrMap = QrCodeMapCreator.create(
                    order.getPayment().getQrData(), player.getWorld(),
                    qrCodeMapSection.getString("displayname"),
                    qrCodeMapSection.getStringList("lore"));

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
        if (itemStack == null || itemStack.getType() != XMaterial.FILLED_MAP.parseMaterial()) return false;

        MapView mapView = null;

        try {
            MapMeta mapMeta = (MapMeta) itemStack.getItemMeta();
            if (mapMeta != null) mapView = mapMeta.getMapView();
        } catch (NoSuchMethodError | NoClassDefFoundError ignored) {}

        if (mapView == null) {
            int mapId = itemStack.getDurability();
            mapView = MapCompatibility.getMap(mapId);
        }

        if (mapView == null) return false;

        for (MapRenderer renderer : mapView.getRenderers()) {
            if (renderer instanceof QrCodeMapRenderer) return true;
        }

        return false;
    }

    private static boolean classExists(String className) {
        try {
            Class.forName(className);
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }
}