package br.com.din.pixcraft.shop.gui;

import br.com.din.pixcraft.order.OrderManager;
import br.com.din.pixcraft.shop.Button;
import br.com.din.pixcraft.shop.ButtonType;
import br.com.din.pixcraft.shop.category.Category;
import br.com.din.pixcraft.shop.category.CategoryManager;
import br.com.din.pixcraft.product.ProductManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public class ShopGui implements Listener {
    private final JavaPlugin plugin;
    private final OrderManager orderManager;
    private final CategoryManager categoryManager;
    private final ProductManager productManager;

    private final Map<UUID, Deque<Category>> openInventories = new HashMap<>();
    private final Set<UUID> navigatingPlayers = new HashSet<>();
    private final Map<UUID, Button> pendingPurchases = new HashMap<>();

    public ShopGui(JavaPlugin plugin, OrderManager orderManager, CategoryManager categoryManager, ProductManager productManager) {
        Bukkit.getPluginManager().registerEvents(this, plugin);

        this.plugin = plugin;
        this.orderManager = orderManager;
        this.categoryManager = categoryManager;
        this.productManager = productManager;
    }

    public void openCategory(Player player, String categoryId) {
        Category category = categoryManager.get(categoryId);
        if (category == null) return;

        Deque<Category> stack = new ArrayDeque<>();
        stack.push(category);
        openInventories.put(player.getUniqueId(), stack);

        openInventory(player, category);
    }

    public void openConfirmationMenu(Player player, Button button) {
        pendingPurchases.put(player.getUniqueId(), button);
        openCategory(player, plugin.getConfig().getString("shop.confirmation-gui"));
    }

    private void navigate(Player player, Category category) {
        openInventories.get(player.getUniqueId()).push(category);
        openInventory(player, category);
    }

    private void goBack(Player player) {
        Deque<Category> stack = openInventories.get(player.getUniqueId());
        if (stack.size() <= 1) {
            player.closeInventory();
            return;
        }
        stack.pop();
        openInventory(player, stack.peek());
    }

    private void openInventory(Player player, Category category) {
        navigatingPlayers.add(player.getUniqueId());

        Inventory inv = Bukkit.createInventory(null, category.getSize(), category.getTitle());

        category.getButtons().forEach((slot, button) -> {
            if (button.getType() == ButtonType.PRODUCT_PREVIEW) {
                Button product = pendingPurchases.get(player.getUniqueId());
                if (product != null) {
                    inv.setItem(slot, product.getItemStack());
                }
            } else {
                inv.setItem(slot, button.getItemStack());
            }
        });

        player.openInventory(inv);

        navigatingPlayers.remove(player.getUniqueId());
    }

    private void handlerButtonClick(Player player, Button button) {
        switch (button.getType()) {
            case CATEGORY:
                navigate(player, categoryManager.get(button.getTarget()));
                break;

            case PRODUCT:
                pendingPurchases.put(player.getUniqueId(), button);
                navigate(player, categoryManager.get(plugin.getConfig().getString("shop.confirmation-gui")));
                break;

            case DECORATIVE: break; // Não faz nada

            case GO_BACK:
                if (openInventories.get(player.getUniqueId()).peek().getId().equals(plugin.getConfig().getString("shop.confirmation-gui"))) {
                    pendingPurchases.remove(player.getUniqueId());
                }
                goBack(player);
                break;

            // Gui de confirmação
            case CONFIRM:
                player.closeInventory();
                orderManager.processOrder(player, productManager.getProduct(pendingPurchases.remove(player.getUniqueId()).getTarget()));
                break;

            case CANCEL:
                pendingPurchases.remove(player.getUniqueId());
                player.closeInventory();
                break;

            case PRODUCT_PREVIEW: break; // Não faz nada
            default:
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        if (!openInventories.containsKey(player.getUniqueId())) return;

        event.setCancelled(true);
        player.updateInventory();

        if (!(event.isLeftClick() || event.isRightClick() || event.isShiftClick())) return;
        if (event.getClickedInventory() == null || !event.getClickedInventory().getType().equals(InventoryType.CHEST)) return;

        Category category = openInventories.get(player.getUniqueId()).peek();
        if (category == null) return;

        Button button = category.getButtons().get(event.getSlot());
        if (button == null) return;

        handlerButtonClick(player, button);
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e) {
        UUID uuid = e.getPlayer().getUniqueId();
        if (!navigatingPlayers.contains(uuid)) {
            openInventories.remove(uuid);
        }
    }
}