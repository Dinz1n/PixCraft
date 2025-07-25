package br.com.din.pixcraft.gui.shop;

import br.com.din.pixcraft.category.Category;
import br.com.din.pixcraft.category.CategoryManager;
import br.com.din.pixcraft.order.OrderManager;
import br.com.din.pixcraft.product.Product;
import br.com.din.pixcraft.product.ProductManager;
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

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class ShopGui implements Listener {
    private final Set<UUID> playersWithGuiOpened = new HashSet<>();
    private final JavaPlugin plugin;
    private final OrderManager orderManager;
    private final ProductManager productManager;
    private final CategoryManager categoryManager;
    private final ConfirmCancelGui confirmCancelGui;

    public ShopGui(JavaPlugin plugin, OrderManager orderManager, ProductManager productManager, CategoryManager categoryManager, ConfirmCancelGui confirmCancelGui) {
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);

        this.orderManager = orderManager;
        this.productManager = productManager;
        this.categoryManager = categoryManager;
        this.confirmCancelGui = confirmCancelGui;
    }

    public void openGui(Player player, String categoryId) {
        Category category = categoryManager.getCategory(categoryId);

        Inventory inventory = Bukkit.createInventory(null, category.getInventory().getSize(), category.getTitle());
        inventory.setContents(category.getInventory().getContents().clone());

        player.openInventory(inventory);
        playersWithGuiOpened.add(player.getUniqueId());
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();

        if (!playersWithGuiOpened.contains(player.getUniqueId())) return;
        event.setCancelled(true);

        if (!(event.isLeftClick() || event.isRightClick() || event.isShiftClick())) return;

        ItemStack itemStack = event.getCurrentItem();

        if (itemStack == null || itemStack.getType() == Material.AIR) return;

        if (NBTUtils.hasTag(itemStack, ShopNBTKeys.SHOP_ITEM_TYPE.name())) {
            switch (ShopItemType.valueOf(NBTUtils.getString(itemStack, ShopNBTKeys.SHOP_ITEM_TYPE.name()))) {
                case CATEGORY:
                    openGui(player, NBTUtils.getString(itemStack, ShopNBTKeys.SHOP_ITEM_ID.name()));
                    break;

                case PRODUCT:
                    Product product = productManager.getProduct(NBTUtils.getString(itemStack, ShopNBTKeys.SHOP_ITEM_ID.name()));
                    confirmCancelGui.openGui(player, product,confirmOrder -> {
                        if (confirmOrder)
                            Bukkit.getScheduler().runTask(plugin, () -> {
                                orderManager.processOrder(player, product);
                            });
                    });
                    break;
            }
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (playersWithGuiOpened.contains(event.getPlayer().getUniqueId())) playersWithGuiOpened.remove(event.getPlayer().getUniqueId());
    }
}