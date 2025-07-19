package br.com.din.pixcraft.gui;

import br.com.din.pixcraft.PixCraft;
import br.com.din.pixcraft.product.Product;
import br.com.din.pixcraft.utils.minecraft_item_stack.ItemStackUtils;
import br.com.din.pixcraft.utils.minecraft_item_stack.PDCKeys;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

public class ConfirmCancelGui implements Listener{
    private final JavaPlugin plugin = PixCraft.getInstance();
    private final Map<UUID, Consumer<Boolean>> playersWithGuiOpened = new HashMap<>();

    public ConfirmCancelGui() {
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    public void openGui(Player player, Product product, Consumer<Boolean> callback) {
        Inventory inventory = createGui(product.getIcon());
        playersWithGuiOpened.put(player.getUniqueId(), callback);
        player.openInventory(inventory);
    }

    private Inventory createGui(ItemStack productIcon) {
        Inventory inventory = Bukkit.createInventory(null, 9*3, "Confirmar pedido");

        ItemStack confirmButton = ItemStackUtils.builder()
                .setMaterial(Material.EMERALD_BLOCK)
                .setDisplayName("§aConfirmar")
                .build();
        ItemStackUtils.setPDC(confirmButton, PDCKeys.CONFIRM_CANCEL_GUI_BUTTON_TYPE, PersistentDataType.STRING, "confirm_button");

        ItemStack cancelButton = ItemStackUtils.builder()
                .setMaterial(Material.REDSTONE_BLOCK)
                .setDisplayName("§cCancelar")
                .build();
        ItemStackUtils.setPDC(cancelButton, PDCKeys.CONFIRM_CANCEL_GUI_BUTTON_TYPE, PersistentDataType.STRING, "cancel_button");

        inventory.setItem(11, confirmButton);
        inventory.setItem(13, productIcon);
        inventory.setItem(15, cancelButton);

        return inventory;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        UUID uuid = event.getWhoClicked().getUniqueId();

        if (playersWithGuiOpened.containsKey(uuid)) event.setCancelled(true);

        ItemStack button = event.getCurrentItem();
        if (button == null) return;

        ItemMeta itemMeta = button.getItemMeta();
        if (itemMeta != null) {
            if (event.isLeftClick() || event.isRightClick() || event.isShiftClick()) {
                Consumer<Boolean> callback;
                switch ((String) ItemStackUtils.getPDC(itemMeta, PDCKeys.CONFIRM_CANCEL_GUI_BUTTON_TYPE, PersistentDataType.STRING)) {
                    case "confirm_button":
                        callback = playersWithGuiOpened.remove(uuid);
                        if (callback != null) {
                            callback.accept(true);
                        }
                        event.getWhoClicked().closeInventory();
                        break;

                    case "cancel_button":
                        callback = playersWithGuiOpened.remove(uuid);
                        if (callback != null) {
                            callback.accept(false);
                        }
                        event.getWhoClicked().closeInventory();
                        break;

                    case "product_icon":
                        break;
                }
            }
        }
    }

    @EventHandler
    public void onPlayerSwapHandItems(PlayerSwapHandItemsEvent event) {
        if (playersWithGuiOpened.containsKey(event.getPlayer().getUniqueId())) event.setCancelled(true);
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        if (playersWithGuiOpened.containsKey(uuid)) playersWithGuiOpened.remove(uuid);
    }
}