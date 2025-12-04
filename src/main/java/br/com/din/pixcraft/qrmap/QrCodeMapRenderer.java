package br.com.din.pixcraft.qrmap;

import org.bukkit.entity.Player;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

import java.awt.image.BufferedImage;

public class QrCodeMapRenderer extends MapRenderer {
    private final BufferedImage qrImage;

    public QrCodeMapRenderer(BufferedImage qrImage) {
        this.qrImage = qrImage;
    }

    @Override
    public void render(MapView mapView, MapCanvas mapCanvas, Player player) {
        mapCanvas.drawImage(0, 0, qrImage);
    }
}