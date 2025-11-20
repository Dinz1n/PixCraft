package br.com.din.pixcraft.shop.gui;

import br.com.din.pixcraft.order.OrderManager;
import br.com.din.pixcraft.shop.button.Button;
import br.com.din.pixcraft.shop.button.ButtonType;
import br.com.din.pixcraft.shop.menu.Menu;
import br.com.din.pixcraft.shop.menu.MenuManager;
import br.com.din.pixcraft.product.ProductManager;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public class ShopGui implements Listener {
    private final JavaPlugin plugin;
    private final OrderManager orderManager;
    private final MenuManager menuManager;
    private final ProductManager productManager;

    private final Map<UUID, Deque<Menu>> openInventories = new HashMap<>();
    private final Set<UUID> navigatingPlayers = new HashSet<>();
    private final Map<UUID, Button> pendingPurchases = new HashMap<>();

    public ShopGui(JavaPlugin plugin, OrderManager orderManager, MenuManager categoryManager, ProductManager productManager) {
        Bukkit.getPluginManager().registerEvents(this, plugin);

        this.plugin = plugin;
        this.orderManager = orderManager;
        this.menuManager = categoryManager;
        this.productManager = productManager;
    }

    public void openMenu(Player player, String categoryId) {
        Menu category = menuManager.getMenu(categoryId);
        if (category == null) return;

        Deque<Menu> stack = new ArrayDeque<>();
        stack.push(category);
        openInventories.put(player.getUniqueId(), stack);

        openInventory(player, category);
    }

    public void openConfirmationMenu(Player player, Button button) {
        if (productManager.getProduct(button.getTarget()).isRequiredPermission()) {
            if (!player.hasPermission("pixcraft.product." + button.getTarget())) {
                player.sendMessage("§c[PixCraft] Você não tem permissão para comprar este produto.");
                return;
            }
        }

        pendingPurchases.put(player.getUniqueId(), button);
        openMenu(player, plugin.getConfig().getString("shop.confirmation-gui"));
    }

    private void navigate(Player player, Menu category) {
        openInventories.get(player.getUniqueId()).push(category);
        openInventory(player, category);
    }

    private void goBack(Player player) {
        Deque<Menu> stack = openInventories.get(player.getUniqueId());
        if (stack.size() <= 1) {
            player.closeInventory();
            return;
        }
        stack.pop();
        openInventory(player, stack.peek());
    }

    private void openInventory(Player player, Menu category) {
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
            case MENU:
                navigate(player, menuManager.getMenu(button.getTarget()));
                break;

            case PRODUCT:
                if (pendingPurchases.containsKey(player.getUniqueId())) {
                    break;
                }

                if (productManager.getProduct(button.getTarget()).isRequiredPermission()) {
                    if (!player.hasPermission("pixcraft.product." + button.getTarget())) {
                        player.sendMessage("§c[PixCraft] Você não tem permissão para comprar este produto.");
                        return;
                    }
                }

                pendingPurchases.put(player.getUniqueId(), button);
                navigate(player, menuManager.getMenu(plugin.getConfig().getString("shop.confirmation-gui")));
                break;

            case DECORATIVE: break; // Não faz nada

            case BACK:
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

        Menu category = openInventories.get(player.getUniqueId()).peek();
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