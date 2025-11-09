package br.com.din.pixcraft.utils;

import com.cryptomorin.xseries.XMaterial;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.Field;
import java.util.Base64;
import java.util.UUID;

public class HeadUtils {

    public static ItemStack getCustomHead(String textureValue) {
        try {
            boolean legacy = !XMaterial.PLAYER_HEAD.isSupported();
            ItemStack head = legacy
                    ? new ItemStack(Material.valueOf("SKULL_ITEM"), 1, (short) 3)
                    : new ItemStack(Material.PLAYER_HEAD);

            SkullMeta meta = (SkullMeta) head.getItemMeta();
            if (meta == null) return head;

            GameProfile profile = new GameProfile(UUID.randomUUID(), null);
            String valueToUse;

            if (textureValue.startsWith("eyJ")) {
                valueToUse = textureValue;
            }
            else if (textureValue.startsWith("http")) {
                valueToUse = encodeTexture(textureValue);
            }
            else {
                valueToUse = encodeTexture("https://textures.minecraft.net/texture/" + textureValue);
            }

            profile.getProperties().put("textures", new Property("textures", valueToUse));

            Field profileField = meta.getClass().getDeclaredField("profile");
            profileField.setAccessible(true);
            profileField.set(meta, profile);

            head.setItemMeta(meta);
            return head;
        } catch (Exception e) {
            e.printStackTrace();
            return new ItemStack(Material.BARRIER);
        }
    }

    private static String encodeTexture(String url) {
        String json = "{\"textures\":{\"SKIN\":{\"url\":\"" + url + "\"}}}";
        return Base64.getEncoder().encodeToString(json.getBytes());
    }
}