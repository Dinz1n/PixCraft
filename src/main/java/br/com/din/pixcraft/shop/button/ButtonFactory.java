package br.com.din.pixcraft.shop.button;

import br.com.din.pixcraft.PixCraft;
import br.com.din.pixcraft.product.Product;
import br.com.din.pixcraft.product.ProductManager;
import br.com.din.pixcraft.utils.SlotParser;
import br.com.din.pixcraft.yaml.YamlConfigReader;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ButtonFactory {
    private static final JavaPlugin plugin = PixCraft.getInstance();

    public static void createFromConfig(ConfigurationSection buttonData, String sectionKey, String fileName, ProductManager productManager,
            Map<Integer, Button> buttons
    ) {
        if (buttonData == null) return;

        try {
            ButtonType buttonType = ButtonType.valueOf(buttonData.getString("type", "DECORATIVE").toUpperCase());
            String id = buttonData.getString("id");
            ItemStack itemStack;

            if (buttonType == ButtonType.PRODUCT) {
                if (id == null) {
                    if (fileName.equals(plugin.getConfig().getString("shop.confirmation-gui"))) return;
                    plugin.getLogger().warning("Botão de produto '" + sectionKey + "' em '" + fileName + "' não tem um 'id'. Pulando.");
                    return;
                }
                Product product = productManager.getProduct(id);
                if (product == null) {
                    plugin.getLogger().warning("Produto '" + id + "' não encontrado para o botão '" + sectionKey + "' em '" + fileName + "'. Pulando.");
                    return;
                }

                ConfigurationSection itemSection = buttonData.getConfigurationSection("icon");
                if (itemSection != null) {
                    Map<String, String> placeholders = new HashMap<>();
                    placeholders.put("{price}", String.valueOf(product.getPrice()).replace(".", ","));
                    itemStack = YamlConfigReader.buildItem(itemSection, placeholders);
                } else {
                    itemStack = product.getIcon().clone();
                }
            } else {
                ConfigurationSection itemSection = buttonData.getConfigurationSection("icon");
                if (itemSection == null && !buttonType.equals(ButtonType.PRODUCT_PREVIEW)) {
                    plugin.getLogger().warning("Botão '" + sectionKey + "' em '" + fileName + "' não tem uma seção 'icon'. Pulando.");
                    return;
                }
                itemStack = YamlConfigReader.buildItem(itemSection, Collections.emptyMap());
            }

            String buttonTarget = (buttonType == ButtonType.PRODUCT || buttonType == ButtonType.CATEGORY) ? id : null;
            Button button = new Button(buttonType, buttonTarget, itemStack);

            if (buttonType == ButtonType.DECORATIVE) {
                List<Integer> slots = SlotParser.parseSlots(sectionKey);
                for (int slot : slots) {
                    buttons.put(slot, button);
                }
            } else {
                buttons.put(Integer.parseInt(sectionKey), button);
            }
        } catch (IllegalArgumentException e) {
            plugin.getLogger().warning("Tipo de botão inválido para a chave '" + sectionKey + "' em '" + fileName + "'.");
        } catch (Exception e) {
            plugin.getLogger().severe("Falha ao carregar o botão '" + sectionKey + "' em '" + fileName + "': " + e.getMessage());
        }
    }
}