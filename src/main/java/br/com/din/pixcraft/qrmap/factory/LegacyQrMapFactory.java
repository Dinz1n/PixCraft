package br.com.din.pixcraft.qrmap.factory;

import br.com.din.pixcraft.qrmap.QrCode;
import br.com.din.pixcraft.utils.ItemStackBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

import java.util.List;

public class LegacyQrMapFactory implements QrMapFactory {
    @Override
    public ItemStack create(String qrData, World world, String displayName, List<String> lore) {
        MapView mapView = Bukkit.createMap(world);

        mapView.getRenderers().clear();
        mapView.addRenderer(new MapRenderer() {
            @Override
            public void render(MapView mapView, MapCanvas mapCanvas, Player player) {
                mapCanvas.drawImage(0, 0, new QrCode(qrData, 128).getImage());
            }
        });

        mapView.setCenterX(Integer.MAX_VALUE);
        mapView.setCenterZ(Integer.MAX_VALUE);
        mapView.setScale(MapView.Scale.CLOSE);

        ItemStack mapItem = new ItemStackBuilder(Material.MAP)
                .setDisplayName(displayName)
                .setLore(lore)
                .hideFlags()
                .build();

        mapItem.setDurability(getMapViewId(mapView));

        return mapItem;
    }

    private static short getMapViewId(MapView mapView) {
        try {
            return (short) MapView.class.getMethod("getId").invoke(mapView);
        } catch (Throwable ignored) {}

        try {
            String packageName = Bukkit.getServer().getClass().getPackage().getName();
            Class<?> craftMapClass = Class.forName(packageName + ".map.CraftMapView");
            Object craftMap = craftMapClass.cast(mapView);
            return (short) craftMapClass.getMethod("getId").invoke(craftMap);
        } catch (Throwable t) {
            t.printStackTrace();
            return -1;
        }
    }
}
