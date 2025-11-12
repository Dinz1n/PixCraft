package br.com.din.pixcraft.yaml;

import br.com.din.pixcraft.utils.ItemStackBuilder;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class YamlConfigReader {
    public static ItemStack buildItem(ConfigurationSection itemData, Map<String, String> placeholders) {
        if (itemData == null) {
            return new ItemStackBuilder()
                    .setMaterial("BEDROCK")
                    .setDisplayName("§7Null")
                    .hideFlags()
                    .build();
        }

        String material = itemData.getString("material");
        String displayname = applyPlaceholder(itemData.getString("displayname"), placeholders);
        List<String> lore = itemData.getStringList("lore").stream().map(
                s -> applyPlaceholder(s, placeholders)).collect(Collectors.toList());
        int amount = itemData.getInt("amount");
        boolean enchanted = itemData.getBoolean("enchanted");

        return new ItemStackBuilder()
                .setMaterial(material)
                .setDisplayName(displayname)
                .setLore(lore)
                .setAmount(amount)
                .setEnchanted(enchanted)
                .hideFlags()
                .build();
    }

    private static String applyPlaceholder(String text, Map<String, String> placeholders) {
        if (text == null || placeholders == null) {
            return text;
        }
        for (Map.Entry<String, String> entry : placeholders.entrySet()) {
            text = text.replace(entry.getKey(), entry.getValue());
        }
        return text;
    }
}