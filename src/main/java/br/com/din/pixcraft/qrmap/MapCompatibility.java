package br.com.din.pixcraft.qrmap;

import org.bukkit.Bukkit;
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