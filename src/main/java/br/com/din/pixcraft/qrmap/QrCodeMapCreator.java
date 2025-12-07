package br.com.din.pixcraft.qrmap;

import br.com.din.pixcraft.utils.ItemStackBuilder;
import com.cryptomorin.xseries.XMaterial;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;

public class QrCodeMapCreator {
    public static ItemStack create(String qrData, World world, String displayName, List<String> lore) {
        MapView mapView = Bukkit.createMap(world);
        mapView.getRenderers().clear();
        mapView.addRenderer(new MapRenderer() {
            @Override
            public void render(MapView mapView, MapCanvas mapCanvas, Player player) {
                mapCanvas.drawImage(0, 0, QrCodeGenerator.generate(qrData, 128).getImage());
            }
        });
        mapView.setCenterX(Integer.MAX_VALUE);
        mapView.setCenterZ(Integer.MAX_VALUE);
        mapView.setScale(MapView.Scale.CLOSE);


        ItemStack mapItem = new ItemStackBuilder(XMaterial.FILLED_MAP)
                .setDisplayName(displayName)
                .setLore(lore)
                .hideFlags()
                .build();

        try {
            MapMeta mapMeta = (MapMeta) mapItem.getItemMeta();
            mapMeta.setMapView(mapView);
            mapItem.setItemMeta(mapMeta);
            QrMapRegistry.addQrMapId(mapView.getId());
            return mapItem;
        } catch (Throwable ignored) {}

        short mapId = getMapViewId(mapView);

        try {
            ItemStack forgeMap = MapCompatibility.createMapWhitForgeImplementation(qrData, world, mapItem);
            QrMapRegistry.addQrMapId((short) forgeMap.getDurability());
            return forgeMap;
        } catch (Exception ignored) {}

        if (mapId >= 0) {
            mapItem.setDurability(mapId);
            QrMapRegistry.addQrMapId(mapId);
            return mapItem;
        }

        return null;
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

    public static class QrCodeGenerator {
        public static class QrCode {
            private final BitMatrix matrix;
            private final BufferedImage image;

            private QrCode(BitMatrix matrix) {
                this.matrix = matrix;
                this.image = MatrixToImageWriter.toBufferedImage(matrix);
            }

            public BufferedImage getImage() {
                return image;
            }

            public byte[] toBytes() {
                try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                    ImageIO.write(image, "png", baos);
                    return baos.toByteArray();
                } catch (IOException e) {
                    throw new RuntimeException("Erro ao converter QR para bytes", e);
                }
            }

            public byte[][] getByteMatrix() {
                int w = matrix.getWidth();
                int h = matrix.getHeight();
                byte[][] out = new byte[h][w];
                for (int y = 0; y < h; y++) {
                    for (int x = 0; x < w; x++) {
                        out[y][x] = (byte) (matrix.get(x, y) ? 1 : 0);
                    }
                }
                return out;
            }
        }

        public static QrCode generate(String text, int size) {
            try {
                BitMatrix matrix = new QRCodeWriter().encode(text, BarcodeFormat.QR_CODE, size, size);
                return new QrCode(matrix);
            } catch (WriterException e) {
                throw new RuntimeException("Erro ao gerar QR Code", e);
            }
        }
    }
}