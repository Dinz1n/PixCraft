package br.com.din.pixcraft.qrmap;

import br.com.din.pixcraft.utils.ItemStackBuilder;

import com.cryptomorin.xseries.XMaterial;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.qrcode.QRCodeWriter;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.map.MapView;

import java.awt.image.BufferedImage;
import java.util.List;

public class QrCodeMapCreator {
    public static ItemStack create(String qrData, World world, String displayname, List<String> lore) {
        MapView mapView = Bukkit.createMap(world);
        mapView.getRenderers().clear();
        mapView.addRenderer(new QrCodeMapRenderer(QrCodeGenerator.generate(qrData, 128, 128)));

        ItemStack mapItem = new ItemStackBuilder(XMaterial.FILLED_MAP)
                .setDisplayName(displayname)
                .setLore(lore)
                .hideFlags()
                .build();

        // Método moderno
        try {
            MapMeta mapMeta = (MapMeta) mapItem.getItemMeta();
            mapMeta.setMapView(mapView);
            mapItem.setItemMeta(mapMeta);
            return mapItem;
        } catch (Throwable throwable) {}

        // Método legado
        short mapId = getMapViewId(mapView);
        if (mapId >= 0) {
            mapItem.setDurability(mapId);
            return mapItem;
        }

        return null;
    }

    private static short getMapViewId(MapView mapView) {
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

    private static class QrCodeGenerator {
        public static BufferedImage generate(String data, int width, int height) {
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            try {
                return MatrixToImageWriter.toBufferedImage(qrCodeWriter.encode(data, BarcodeFormat.QR_CODE, width, height));
            } catch (WriterException e) {
                throw new RuntimeException(e);
            }
        }
    }
}