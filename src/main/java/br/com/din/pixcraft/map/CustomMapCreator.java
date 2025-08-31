package br.com.din.pixcraft.map;

import br.com.din.pixcraft.utils.ItemStackBuilder;
import com.cryptomorin.xseries.XMaterial;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

import java.awt.image.BufferedImage;
import java.util.List;

public class CustomMapCreator {
    public static ItemStack create(BufferedImage image, World world, String displayname, List<String> lore) {
        MapView mapView = Bukkit.createMap(world);
        mapView.getRenderers().clear();
        mapView.addRenderer(new MapRenderer() {
            @Override
            public void render(MapView map, MapCanvas canvas, Player player) {
                canvas.drawImage(0, 0, image);
            }
        });

        ItemStack mapItem = new ItemStackBuilder(XMaterial.FILLED_MAP)
                .setDisplayName(displayname)
                .setLore(lore)
                .build();

        // Método moderno
        try {
            MapMeta mapMeta = (MapMeta) mapItem.getItemMeta();
            mapMeta.setMapView(mapView);
            mapItem.setItemMeta(mapMeta);
            return mapItem;
        } catch (Throwable throwable) {}

        // Método legado
        short mapId = getMapId(mapView);
        if (mapId >= 0) {
            mapItem.setDurability(mapId);
            return mapItem;
        }

        return null;
    }

    private static short getMapId(MapView mapView) {
        try {
            return (short) MapView.class.getMethod("getId").invoke(mapView);
        } catch (Throwable ignored) { }

        try {
            String packageName = Bukkit.getServer().getClass().getPackage().getName();
            Class<?> craftMapViewClass = Class.forName(packageName + ".map.CraftMapView");
            Object craftMap = craftMapViewClass.cast(mapView);
            return (short) craftMapViewClass.getMethod("getId").invoke(craftMap);
        } catch (Throwable t) {
            t.printStackTrace();
            return -1;
        }
    }
}