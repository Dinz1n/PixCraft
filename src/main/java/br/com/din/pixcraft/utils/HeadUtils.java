package br.com.din.pixcraft.utils;

import com.cryptomorin.xseries.XMaterial;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Base64;
import java.util.UUID;

public class HeadUtils {

    public static ItemStack getCustomHead(String textureValue) {
        if (Bukkit.getVersion().contains("1.7")) {
            return new ItemStack(XMaterial.PLAYER_HEAD.parseItem());
        }

        try {
            ItemStack head = XMaterial.PLAYER_HEAD.parseItem();
            if (head == null) {
                head = new ItemStack(Material.valueOf("SKULL_ITEM"), 1, (short) 3);
            }

            SkullMeta meta = (SkullMeta) head.getItemMeta();
            if (meta == null) return head;

            applyTexture(meta, textureValue);

            head.setItemMeta(meta);
            return head;

        } catch (Exception e) {
            e.printStackTrace();
            return new ItemStack(Material.BEDROCK);
        }
    }

    private static void applyTexture(SkullMeta meta, String textureValue) throws Exception {
        try {
            applyTextureModern(meta, resolveSkinUrl(textureValue));
            return;
        } catch (ClassNotFoundException | NoSuchMethodException e) {
        }

        applyTextureLegacy(meta, resolveBase64(textureValue));
    }

    private static void applyTextureModern(SkullMeta meta, String skinUrl) throws Exception {
        Class<?> playerProfileClass  = Class.forName("org.bukkit.profile.PlayerProfile");
        Class<?> playerTexturesClass = Class.forName("org.bukkit.profile.PlayerTextures");

        Method createProfile = Bukkit.class.getMethod("createProfile", UUID.class, String.class);
        Object profile = createProfile.invoke(null, UUID.randomUUID(), null);

        Object textures = playerProfileClass.getMethod("getTextures").invoke(profile);

        playerTexturesClass.getMethod("setSkin", URL.class).invoke(textures, new URL(skinUrl));

        playerProfileClass.getMethod("setTextures", playerTexturesClass).invoke(profile, textures);

        SkullMeta.class.getMethod("setOwnerProfile", playerProfileClass).invoke(meta, profile);
    }

    private static void applyTextureLegacy(SkullMeta meta, String base64) throws Exception {
        Class<?> gameProfileClass = Class.forName("com.mojang.authlib.GameProfile");
        Class<?> propertyClass    = Class.forName("com.mojang.authlib.properties.Property");

        Object profile = gameProfileClass
                .getConstructor(UUID.class, String.class)
                .newInstance(UUID.randomUUID(), "");

        Object property;
        try {
            property = propertyClass
                    .getConstructor(String.class, String.class)
                    .newInstance("textures", base64);
        } catch (NoSuchMethodException e) {
            property = propertyClass
                    .getConstructor(String.class, String.class, String.class)
                    .newInstance("textures", base64, null);
        }

        Object propertyMap = gameProfileClass.getMethod("getProperties").invoke(profile);
        propertyMap.getClass().getMethod("put", Object.class, Object.class)
                .invoke(propertyMap, "textures", property);

        setProfileField(meta, profile);
    }

    private static void setProfileField(Object meta, Object profile) throws Exception {
        Class<?> clazz = meta.getClass();
        while (clazz != null) {
            try {
                Field field = clazz.getDeclaredField("profile");
                field.setAccessible(true);
                field.set(meta, profile);
                return;
            } catch (NoSuchFieldException e) {
                clazz = clazz.getSuperclass();
            }
        }
        throw new NoSuchFieldException("Campo 'profile' não encontrado na hierarquia de SkullMeta");
    }

    private static String resolveSkinUrl(String value) {
        if (value.startsWith("eyJ")) {
            String decoded = new String(Base64.getDecoder().decode(value));
            return decoded.split("\"url\":\"")[1].split("\"")[0];
        } else if (value.startsWith("http")) {
            return value;
        } else {
            return "https://textures.minecraft.net/texture/" + value;
        }
    }

    private static String resolveBase64(String value) {
        if (value.startsWith("eyJ"))  return value;
        if (value.startsWith("http")) return encodeTexture(value);
        return encodeTexture("https://textures.minecraft.net/texture/" + value);
    }

    private static String encodeTexture(String url) {
        String json = "{\"textures\":{\"SKIN\":{\"url\":\"" + url + "\"}}}";
        return Base64.getEncoder().encodeToString(json.getBytes());
    }
}