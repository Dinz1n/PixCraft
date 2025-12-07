package br.com.din.pixcraft.qrmap;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;
import org.bukkit.map.MapView;
import java.lang.reflect.Method;

public class MapCompatibility {

    private static Method GET_MAP_METHOD = null;

    private MapCompatibility() {}

    public static MapView getMap(int mapId) {
        if (GET_MAP_METHOD != null) {
            return invokeMethod(GET_MAP_METHOD, mapId);
        }


        try {
            GET_MAP_METHOD = Bukkit.class.getMethod("getMap", int.class);
            return invokeMethod(GET_MAP_METHOD, mapId);
        } catch (NoSuchMethodException e) {
        }

        try {
            GET_MAP_METHOD = Bukkit.class.getMethod("getMap", short.class);
            return invokeMethod(GET_MAP_METHOD, mapId);
        } catch (NoSuchMethodException e) {
            System.err.println("PixCraft: Falha crítica! Não foi possível encontrar o método Bukkit.getMap() no servidor.");
            return null;
        }
    }

    public static ItemStack createMapWhitForgeImplementation(String qrData, World world, ItemStack mapItem) throws Exception {
        // Pega o World do Forge
        Class<?> craftWorldClass = Class.forName("org.bukkit.craftbukkit.v1_7_R4.CraftWorld");
        Object craftWorld = craftWorldClass.cast(world);
        Object nmsWorld = craftWorldClass.getMethod("getHandle").invoke(craftWorld);

        // Cria novo ID de mapa único
        java.lang.reflect.Method getUniqueDataIdMethod = nmsWorld.getClass()
                .getMethod("func_72841_b", String.class); // getUniqueDataId
        int newMapId = (int) getUniqueDataIdMethod.invoke(nmsWorld, "map");

        // Cria a chave do MapData
        String mapDataKey = "map_" + newMapId;

        // Cria novo MapData
        Class<?> mapDataClass = Class.forName("net.minecraft.world.storage.MapData");
        java.lang.reflect.Constructor<?> mapDataConstructor = mapDataClass.getConstructor(String.class);
        Object mapData = mapDataConstructor.newInstance(mapDataKey);

        // field_76201_a = xCenter (int)
        java.lang.reflect.Field xCenterField = mapDataClass.getDeclaredField("field_76201_a");
        xCenterField.setAccessible(true);
        xCenterField.setInt(mapData, Integer.MAX_VALUE);

        // field_76199_b = zCenter (int)
        java.lang.reflect.Field zCenterField = mapDataClass.getDeclaredField("field_76199_b");
        zCenterField.setAccessible(true);
        zCenterField.setInt(mapData, Integer.MAX_VALUE);

        // field_76200_c = dimension (int, não byte!)
        java.lang.reflect.Field dimensionField = mapDataClass.getDeclaredField("field_76200_c");
        dimensionField.setAccessible(true);
        dimensionField.setInt(mapData, -999);

        // field_76197_d = scale (byte)
        java.lang.reflect.Field scaleField = mapDataClass.getDeclaredField("field_76197_d");
        scaleField.setAccessible(true);
        scaleField.setByte(mapData, (byte) 3);

        // field_76198_e = colors (byte[])
        java.lang.reflect.Field colorsField = mapDataClass.getDeclaredField("field_76198_e");
        colorsField.setAccessible(true);
        byte[] colors = (byte[]) colorsField.get(mapData);

        // Pega a imagem do QR Code
        byte[][] qrImage = QrCodeMapCreator.QrCodeGenerator.generate(qrData, 128).getByteMatrix();

        // Copia os pixels do QR para o mapa
        byte WHITE = 119;
        byte BLACK = 34;

        for (int y = 0; y < 128; y++) {
            for (int x = 0; x < 128; x++) {
                colors[y * 128 + x] = (qrImage[y][x] == 1 ? WHITE : BLACK);
            }
        }

        // Tenta salvar usando a classe pai de MapData
        Class<?> mapDataSuperClass = mapDataClass.getSuperclass();

        java.lang.reflect.Method setDataMethod = nmsWorld.getClass()
                .getMethod("func_72823_a", String.class, mapDataSuperClass);
        setDataMethod.invoke(nmsWorld, mapDataKey, mapData);

        // Marca o mapa como modificado
        java.lang.reflect.Method markDirtyMethod = mapDataClass.getMethod("func_76185_a"); // markDirty
        markDirtyMethod.invoke(mapData);

        // Cria o ItemStack com o novo ID
        Class<?> itemClass = Class.forName("net.minecraft.item.Item");
        Class<?> itemStackClass = Class.forName("net.minecraft.item.ItemStack");

        Object mapItem_NMS = itemClass.getMethod("func_150899_d", int.class).invoke(null, 358); // filled_map
        java.lang.reflect.Constructor<?> constructor = itemStackClass.getConstructor(itemClass, int.class, int.class);
        Object forgeItemStack = constructor.newInstance(mapItem_NMS, 1, newMapId);

        // Converte para Bukkit
        Class<?> craftItemStackClass = Class.forName("org.bukkit.craftbukkit.v1_7_R4.inventory.CraftItemStack");
        java.lang.reflect.Method asCraftMirror = craftItemStackClass.getMethod("asCraftMirror", itemStackClass);
        ItemStack bukkitMap = (ItemStack) asCraftMirror.invoke(null, forgeItemStack);

        bukkitMap.setItemMeta(mapItem.getItemMeta());

        return bukkitMap;
    }

    private static MapView invokeMethod(Method method, int mapId) {
        try {
            Class<?>[] params = method.getParameterTypes();

            if (params.length > 0 && params[0] == short.class) {
                return (MapView) method.invoke(null, (short) mapId);
            } else {
                return (MapView) method.invoke(null, mapId);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}