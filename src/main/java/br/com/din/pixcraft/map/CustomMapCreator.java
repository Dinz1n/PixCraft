package br.com.din.pixcraft.map;

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
import org.jetbrains.annotations.NotNull;

import java.awt.image.BufferedImage;
import java.util.List;

public class CustomMapCreator {
    public ItemStack create(BufferedImage image, World world, String displayname, List<String> lore) {
        MapView mapView = Bukkit.createMap(world);
        mapView.getRenderers().clear();
        mapView.addRenderer(new MapRenderer() {
            @Override
            public void render(@NotNull MapView map, @NotNull MapCanvas canvas, @NotNull Player player) {
                canvas.drawImage(0, 0, image);
            }
        });

        ItemStack itemStack = new ItemStackBuilder()
                .setMaterial(Material.FILLED_MAP)
                .setDisplayName(displayname)
                .setLore(lore)
                .setAmount(1)
                .setEnchanted(false)
                .build();
        MapMeta mapMeta = (MapMeta) itemStack.getItemMeta();
        mapMeta.setMapView(mapView);
        itemStack.setItemMeta(mapMeta);
        return itemStack;
    }
}
