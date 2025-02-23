package br.din.pixCraft.customMap;

import br.din.pixCraft.PixCraft;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;
import org.bukkit.persistence.PersistentDataType;

import java.awt.image.BufferedImage;

public class MapHandler {
    private static PixCraft plugin = PixCraft.getInstance();

    public static ItemStack createQrMap(BufferedImage qrImage, World world, Long paymentID) {
        if (qrImage == null) return null;

        // Criando um novo mapa no mundo especificado
        MapView mapView = Bukkit.createMap(world);

        // Removendo renderizadores padrão
        for (MapRenderer renderer : mapView.getRenderers()) {
            mapView.removeRenderer(renderer);
        }

        // Adicionando o renderizador de QR Code
        mapView.addRenderer(new Renderer(qrImage));

        // Criando o ItemStack do mapa
        ItemStack mapItem = new ItemStack(Material.FILLED_MAP);
        MapMeta meta = (MapMeta) mapItem.getItemMeta();
        if (meta != null) {
            meta.setMapView(mapView);
            meta.setDisplayName("§bCódigo QR §7§ox botão direito para cancelar");

            NamespacedKey key = new NamespacedKey(plugin, "paymentId");
            meta.getPersistentDataContainer().set(key, PersistentDataType.LONG, paymentID);

            mapItem.setItemMeta(meta);
        }

        return mapItem;
    }
}