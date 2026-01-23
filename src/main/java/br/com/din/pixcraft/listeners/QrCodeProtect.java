package br.com.din.pixcraft.listeners;

import br.com.din.pixcraft.order.Order;
import br.com.din.pixcraft.order.OrderManager;
import br.com.din.pixcraft.payment.PaymentStatus;
import br.com.din.pixcraft.payment.events.PaymentUpdateEvent;
import br.com.din.pixcraft.qrmap.QrMapService;
import com.cryptomorin.xseries.XMaterial;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.map.MapView;
import org.bukkit.plugin.java.JavaPlugin;

public class QrCodeProtect implements Listener {

    private final JavaPlugin plugin;
    private final OrderManager orderManager;
    private final QrMapService qrMapService;
    private final ConfigurationSection mapConfig;

    public QrCodeProtect(JavaPlugin plugin, OrderManager orderManager, QrMapService qrMapService) {
        this.plugin = plugin;
        this.orderManager = orderManager;
        this.qrMapService = qrMapService;
        this.mapConfig = plugin.getConfig().getConfigurationSection("payment.qr-code-map");

        Bukkit.getPluginManager().registerEvents(this, plugin);

        if (classExists("org.bukkit.event.player.PlayerSwapHandItemsEvent")) {
            Bukkit.getPluginManager().registerEvents(new SwapListener(), plugin);
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (e.getClick() == ClickType.NUMBER_KEY) {
            cancelIfQrMap(e.getWhoClicked().getInventory().getItem(e.getHotbarButton()), e);
        }
        cancelIfQrMap(e.getCurrentItem(), e);
        cancelIfQrMap(e.getCursor(), e);
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent e) {
        cancelIfQrMap(e.getItemDrop().getItemStack(), e);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        Order order = orderManager.getOrder(p.getUniqueId());

        removeAllQrMaps(p);

        if (order == null || order.getPayment().getStatus() != PaymentStatus.PENDING) return;

        ItemStack qrMap = qrMapService.createMap(
                order.getPayment().getQrData(),
                p.getWorld(),
                mapConfig.getString("displayname"),
                mapConfig.getStringList("lore")
        );

        int slot = mapConfig.getInt("slot", 3);
        if (slot < 0 || slot > 8) slot = 3;

        p.getInventory().setItem(slot, qrMap);
        p.getInventory().setHeldItemSlot(slot);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        if (!plugin.getConfig().getBoolean("payment.cancel-on-leave")) return;

        Player p = e.getPlayer();
        Order order = orderManager.getOrder(p.getUniqueId());
        if (order != null) {
            order.cancel();
            orderManager.removeOrder(p.getUniqueId());
            removeAllQrMaps(p);
        }
    }

    @EventHandler
    public void onPaymentUpdate(PaymentUpdateEvent e) {
        Player p = Bukkit.getPlayer(e.getOrder().getId());
        if (p != null && p.isOnline()) {
            removeAllQrMaps(p);
        }
    }


    private void removeAllQrMaps(Player player) {
        ItemStack[] contents = player.getInventory().getContents();
        if (contents == null) return;

        for (ItemStack item : contents) {
            Integer mapId = getQrMapId(item);
            if (mapId != null) {
                qrMapService.getQrMapRegistry().removeQrMapId(mapId);
                player.getInventory().remove(item);
            }
        }
    }

    private void cancelIfQrMap(ItemStack item, Cancellable e) {
        if (getQrMapId(item) != null) {
            e.setCancelled(true);
        }
    }

    private Integer getQrMapId(ItemStack item) {
        if (item == null) return null;
        if (item.getType() != XMaterial.FILLED_MAP.parseMaterial()) return null;
        if (!(item.getItemMeta() instanceof MapMeta)) return null;

        MapMeta meta = (MapMeta) item.getItemMeta();

        try {
            MapView view = meta.getMapView();
            if (view != null && qrMapService.getQrMapRegistry().containsQrMapId(view.getId())) {
                return view.getId();
            }
        } catch (NoSuchMethodError e) {
            if (qrMapService.getQrMapRegistry().containsQrMapId(item.getDurability())) {
                return (int) item.getDurability();
            }
        }
        return null;
    }

    private static boolean classExists(String name) {
        try {
            Class.forName(name);
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    private class SwapListener implements Listener {
        @EventHandler
        public void onSwap(org.bukkit.event.player.PlayerSwapHandItemsEvent e) {
            cancelIfQrMap(e.getOffHandItem(), e);
        }
    }
}