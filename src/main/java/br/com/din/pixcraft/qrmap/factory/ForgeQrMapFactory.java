package br.com.din.pixcraft.qrmap.factory;

import br.com.din.pixcraft.qrmap.QrCode;
import br.com.din.pixcraft.utils.ItemStackBuilder;
import com.cryptomorin.xseries.XMaterial;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class ForgeQrMapFactory implements QrMapFactory {

    public ForgeQrMapFactory() throws Throwable {
        Class<?> cw = Class.forName("org.bukkit.craftbukkit.v1_7_R4.CraftWorld");
        cw.getMethod("getHandle");

        Class<?> md = Class.forName("net.minecraft.world.storage.MapData");
        md.getConstructor(String.class);

        Class<?> item = Class.forName("net.minecraft.item.Item");
        item.getMethod("func_150899_d", int.class);

        Class<?> cis = Class.forName("org.bukkit.craftbukkit.v1_7_R4.inventory.CraftItemStack");
        cis.getMethod("asCraftMirror", Class.forName("net.minecraft.item.ItemStack"));
    }

    @Override
    public ItemStack create(String qrData, World world, String displayName, List<String> lore) {
        try {
            ItemStack mapItem = new ItemStackBuilder(XMaterial.FILLED_MAP)
                    .setDisplayName(displayName)
                    .setLore(lore)
                    .hideFlags()
                    .build();

            Class<?> craftWorldClass = Class.forName("org.bukkit.craftbukkit.v1_7_R4.CraftWorld");
            Object craftWorld = craftWorldClass.cast(world);
            Object nmsWorld = craftWorldClass.getMethod("getHandle").invoke(craftWorld);

            java.lang.reflect.Method getUniqueDataIdMethod = nmsWorld.getClass()
                    .getMethod("func_72841_b", String.class);
            int newMapId = (int) getUniqueDataIdMethod.invoke(nmsWorld, "map");

            String mapDataKey = "map_" + newMapId;

            Class<?> mapDataClass = Class.forName("net.minecraft.world.storage.MapData");
            java.lang.reflect.Constructor<?> mapDataConstructor = mapDataClass.getConstructor(String.class);
            Object mapData = mapDataConstructor.newInstance(mapDataKey);

            java.lang.reflect.Field xCenterField = mapDataClass.getDeclaredField("field_76201_a");
            xCenterField.setAccessible(true);
            xCenterField.setInt(mapData, Integer.MAX_VALUE);

            java.lang.reflect.Field zCenterField = mapDataClass.getDeclaredField("field_76199_b");
            zCenterField.setAccessible(true);
            zCenterField.setInt(mapData, Integer.MAX_VALUE);

            java.lang.reflect.Field dimensionField = mapDataClass.getDeclaredField("field_76200_c");
            dimensionField.setAccessible(true);
            dimensionField.setInt(mapData, -999);

            java.lang.reflect.Field scaleField = mapDataClass.getDeclaredField("field_76197_d");
            scaleField.setAccessible(true);
            scaleField.setByte(mapData, (byte) 3);

            java.lang.reflect.Field colorsField = mapDataClass.getDeclaredField("field_76198_e");
            colorsField.setAccessible(true);
            byte[] colors = (byte[]) colorsField.get(mapData);

            byte[][] qrImage = new QrCode(qrData, 128).getRaw();

            byte WHITE = 119;
            byte BLACK = 34;

            for (int y = 0; y < 128; y++) {
                for (int x = 0; x < 128; x++) {
                    colors[y * 128 + x] = (qrImage[y][x] == 1 ? WHITE : BLACK);
                }
            }

            Class<?> mapDataSuperClass = mapDataClass.getSuperclass();

            java.lang.reflect.Method setDataMethod = nmsWorld.getClass()
                    .getMethod("func_72823_a", String.class, mapDataSuperClass);
            setDataMethod.invoke(nmsWorld, mapDataKey, mapData);

            java.lang.reflect.Method markDirtyMethod = mapDataClass.getMethod("func_76185_a");
            markDirtyMethod.invoke(mapData);

            Class<?> itemClass = Class.forName("net.minecraft.item.Item");
            Class<?> itemStackClass = Class.forName("net.minecraft.item.ItemStack");

            Object mapItem_NMS = itemClass.getMethod("func_150899_d", int.class).invoke(null, 358);
            java.lang.reflect.Constructor<?> constructor = itemStackClass.getConstructor(itemClass, int.class, int.class);
            Object forgeItemStack = constructor.newInstance(mapItem_NMS, 1, newMapId);

            Class<?> craftItemStackClass = Class.forName("org.bukkit.craftbukkit.v1_7_R4.inventory.CraftItemStack");
            java.lang.reflect.Method asCraftMirror = craftItemStackClass.getMethod("asCraftMirror", itemStackClass);
            ItemStack bukkitMap = (ItemStack) asCraftMirror.invoke(null, forgeItemStack);

            bukkitMap.setItemMeta(mapItem.getItemMeta());

            return bukkitMap;
        } catch (Throwable e) {
            e.printStackTrace();
            return new ItemStack(XMaterial.FILLED_MAP.parseMaterial());
        }
    }
}