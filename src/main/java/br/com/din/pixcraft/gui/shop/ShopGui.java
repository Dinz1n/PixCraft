package br.com.din.pixcraft.gui.shop;

import br.com.din.pixcraft.category.Category;
import br.com.din.pixcraft.category.CategoryManager;
import br.com.din.pixcraft.order.OrderManager;
import br.com.din.pixcraft.product.Product;
import br.com.din.pixcraft.product.ProductManager;
import br.com.din.pixcraft.utils.ItemStackBuilder;
import br.com.din.pixcraft.utils.NBTUtils;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public class ShopGui implements Listener {
    private final Map<UUID, Deque<Category>> playersWithGuiOpened = new HashMap<>();
    private ItemStack returnButton;
    private final JavaPlugin plugin;
    private final OrderManager orderManager;
    private final ProductManager productManager;
    private final CategoryManager categoryManager;
    private final ConfirmCancelGui confirmCancelGui;

    public ShopGui(JavaPlugin plugin,
                   OrderManager orderManager,
                   ProductManager productManager,
                   CategoryManager categoryManager,
                   ConfirmCancelGui confirmCancelGui) {
        this.plugin = plugin;
        this.orderManager = orderManager;
        this.productManager = productManager;
        this.categoryManager = categoryManager;
        this.confirmCancelGui = confirmCancelGui;

        loadReturnButton();

        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    public void open(Player player, Category category) {
        Inventory inventory = Bukkit.createInventory(null, category.getInventory().getSize(), category.getTitle());
        inventory.setContents(category.getInventory().getContents().clone());

        player.openInventory(inventory);

        Deque<Category> stack = new ArrayDeque<>();
        stack.push(category);
        playersWithGuiOpened.put(player.getUniqueId(), stack);
    }

    private void navigate(Player player, Category category) {
        UUID uuid = player.getUniqueId();
        Deque<Category> stack = playersWithGuiOpened.get(uuid);

        if (stack == null) return;

        stack.push(category);

        Inventory inventory = Bukkit.createInventory(null, category.getInventory().getSize(), category.getTitle());

        for (int i = 0; i < category.getInventory().getSize(); i++) {
            ItemStack item = category.getInventory().getItem(i);
            if (item != null) {
                inventory.setItem(i, item.clone());
            }
        }

        if (stack.size() > 1) {
            inventory.setItem(inventory.getSize()-9, returnButton);
        }

        player.openInventory(inventory);
    }

    private void goBack(Player player) {
        UUID uuid = player.getUniqueId();
        Deque<Category> stack = playersWithGuiOpened.get(uuid);

        if (stack == null) return;

        stack.pop();

        Category category = stack.peek();

        Inventory inventory = Bukkit.createInventory(null, category.getInventory().getSize(), category.getTitle());

        for (int i = 0; i < category.getInventory().getSize(); i++) {
            ItemStack item = category.getInventory().getItem(i);
            if (item != null) {
                inventory.setItem(i, item.clone());
            }
        }

        if (stack.size() > 1) {
            inventory.setItem(inventory.getSize()-9, returnButton);
        }

        player.openInventory(inventory);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;
        Player player = (Player) event.getWhoClicked();
        UUID uuid = player.getUniqueId();

        if (!playersWithGuiOpened.containsKey(uuid)) return;

        event.setCancelled(true);

        if (!(event.isLeftClick() || event.isRightClick() || event.isShiftClick())) return;

        ItemStack itemStack = event.getCurrentItem();
        if (itemStack == null || itemStack.getType() == Material.AIR) return;

        if (!NBTUtils.hasTag(itemStack, ShopNBTKeys.SHOP_ITEM_TYPE.name())) return;

        String typeString = NBTUtils.getString(itemStack, ShopNBTKeys.SHOP_ITEM_TYPE.name());

        ShopItemType type;
        try {
            type = ShopItemType.valueOf(typeString);
        } catch (IllegalArgumentException e) {
            return;
        }

        switch (type) {
            case CATEGORY:
                String categoryId = NBTUtils.getString(itemStack, ShopNBTKeys.SHOP_ITEM_VALUE.name());
                Category category = categoryManager.getCategory(categoryId);
                if (category != null) {
                    navigate(player, category);
                }
                break;

            case PRODUCT:
                String productId = NBTUtils.getString(itemStack, ShopNBTKeys.SHOP_ITEM_VALUE.name());
                Product product = productManager.getProduct(productId);
                if (product != null) {
                    confirmCancelGui.openGui(player, product, confirmOrder -> {
                        if (confirmOrder) {
                            if (!orderManager.getOrders().containsKey(player.getUniqueId())) {
                                Bukkit.getScheduler().runTask(plugin, () -> orderManager.processOrder(player, product));
                            } else {
                                player.sendMessage("§cVocê só pode fazer um pedido por vez");
                            }
                        } else {
                            player.openInventory(playersWithGuiOpened.get(player.getUniqueId()).peek().getInventory());
                        }
                    });
                }
                break;

            case RETURN:
                goBack(player);
                break;
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (!playersWithGuiOpened.containsKey(event.getPlayer().getUniqueId())) return;
        if (event.getReason().equals(InventoryCloseEvent.Reason.PLAYER)) {
            playersWithGuiOpened.remove(event.getPlayer().getUniqueId());
        }
    }

    public void loadReturnButton() {
        ConfigurationSection returnButtonData = plugin.getConfig().getConfigurationSection("shop.gui.return-button.icon");

        Material material = Material.valueOf(returnButtonData.getString("material"));
        String displayname = returnButtonData.getString("displayname");
        List<String> lore = returnButtonData.getStringList("lore");
        int amount = returnButtonData.getInt("amount");
        boolean enchanted = returnButtonData.getBoolean("enchanted");

        returnButton = new ItemStackBuilder()
                .setMaterial(material)
                .setDisplayName(displayname)
                .setLore(lore)
                .setAmount(amount)
                .setEnchanted(enchanted)
                .build();
        returnButton = NBTUtils.setTag(returnButton, ShopNBTKeys.SHOP_ITEM_TYPE.name(), ShopItemType.RETURN.name());
    }
}