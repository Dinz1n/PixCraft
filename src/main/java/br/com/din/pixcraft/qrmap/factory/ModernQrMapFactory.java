package br.com.din.pixcraft.qrmap.factory;

import br.com.din.pixcraft.qrmap.QrCode;
import br.com.din.pixcraft.utils.ItemStackBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

import java.util.List;

public class ModernQrMapFactory implements QrMapFactory {
    public ModernQrMapFactory() throws Throwable {
        Bukkit.class.getMethod("createMap", World.class);

        Class<?> mapView = MapView.class;
        mapView.getMethod("getRenderers");
        mapView.getMethod("addRenderer", MapRenderer.class);

        Class<?> mapMeta = MapMeta.class;
        mapMeta.getMethod("setMapView", MapView.class);
    }

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

        ItemStack mapItem = new ItemStackBuilder(Material.FILLED_MAP)
                .setDisplayName(displayName)
                .setLore(lore)
                .hideFlags()
                .build();

        MapMeta mapMeta = (MapMeta) mapItem.getItemMeta();
        mapMeta.setMapView(mapView);
        mapItem.setItemMeta(mapMeta);

        return mapItem;
    }
}