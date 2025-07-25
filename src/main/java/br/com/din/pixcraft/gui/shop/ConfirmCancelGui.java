package br.com.din.pixcraft.gui.shop;

import br.com.din.pixcraft.product.Product;
import br.com.din.pixcraft.utils.ItemStackBuilder;
import br.com.din.pixcraft.utils.NBTUtils;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

public class ConfirmCancelGui implements Listener{
    private final Map<UUID, Consumer<Boolean>> playersWithGuiOpened = new HashMap<>();
    private final JavaPlugin plugin;

    public ConfirmCancelGui(JavaPlugin plugin) {
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    public void openGui(Player player, Product product, Consumer<Boolean> callback) {
        Inventory gui = buildGui(product.getIcon());
        player.openInventory(gui);
        playersWithGuiOpened.put(player.getUniqueId(), callback);
    }

    private Inventory buildGui(ItemStack productIcon) {
        Inventory inventory = Bukkit.createInventory(null, 9*3, "Confirmar pedido");

        ItemStack confirmButton = new ItemStackBuilder()
                .setMaterial(Material.EMERALD_BLOCK)
                .setDisplayName("§aConfirmar")
                .build();
        confirmButton = NBTUtils.setTag(confirmButton, ShopNBTKeys.CCG_ITEM_TYPE.name(), ShopItemType.CCG_CONFIRM.name());

        ItemStack cancelButton = new ItemStackBuilder()
                .setMaterial(Material.REDSTONE_BLOCK)
                .setDisplayName("&cCancelar")
                .build();
        cancelButton = NBTUtils.setTag(cancelButton, ShopNBTKeys.CCG_ITEM_TYPE.name(), ShopItemType.CCG_CANCEL.name());

        inventory.setItem(11, confirmButton);
        inventory.setItem(13, productIcon);
        inventory.setItem(15, cancelButton);

        return inventory;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();

        if (!playersWithGuiOpened.containsKey(player.getUniqueId())) return;
        event.setCancelled(true);

        if (!(event.isLeftClick() || event.isRightClick() || event.isShiftClick())) return;

        ItemStack itemStack = event.getCurrentItem();

        if (itemStack == null || itemStack.getType() == Material.AIR) return;

        if (NBTUtils.hasTag(itemStack, ShopNBTKeys.CCG_ITEM_TYPE.name())) {
            Consumer<Boolean> callback;
            switch (ShopItemType.valueOf(NBTUtils.getString(itemStack, ShopNBTKeys.CCG_ITEM_TYPE.name()))) {
                case CCG_CONFIRM:
                    callback = playersWithGuiOpened.remove(player.getUniqueId());
                    if (callback != null) {
                        callback.accept(true);
                    }
                    event.getWhoClicked().closeInventory();
                    break;

                case CCG_CANCEL:
                    callback = playersWithGuiOpened.remove(player.getUniqueId());
                    if (callback != null) {
                        callback.accept(false);
                    }
                    event.getWhoClicked().closeInventory();
                    break;

                case PRODUCT:
                    break;
            }
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        if (playersWithGuiOpened.containsKey(uuid)) playersWithGuiOpened.remove(uuid);
    }
}