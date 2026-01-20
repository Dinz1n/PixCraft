package br.com.din.pixcraft.qrmap;

import br.com.din.pixcraft.qrmap.factory.ForgeQrMapFactory;
import br.com.din.pixcraft.qrmap.factory.LegacyQrMapFactory;
import br.com.din.pixcraft.qrmap.factory.ModernQrMapFactory;
import br.com.din.pixcraft.qrmap.factory.QrMapFactory;

import org.bukkit.World;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.MapMeta;

import java.util.List;

public class QrMapService {
    private QrMapFactory qrMapFactory;
    private final QrMapRegistry qrMapRegistry = new QrMapRegistry();

    public QrMapService() {
        try {
            qrMapFactory = new ForgeQrMapFactory();
        } catch (Throwable t) {
            try {
                qrMapFactory = new ModernQrMapFactory();
            } catch (Throwable throwable) {
                qrMapFactory = new LegacyQrMapFactory();
            }
        }
    }

    public ItemStack createMap(String qrData, World world, String displayname, List<String> lore) {
        ItemStack qrMap;

        try {
            qrMap = qrMapFactory.create(qrData, world, displayname, lore);
        } catch (Throwable t) {
            t.printStackTrace();
            return null;
        }

        try {
            qrMapRegistry.addQrMapId(((MapMeta) qrMap.getItemMeta()).getMapView().getId());
        } catch (Throwable t) {
            qrMapRegistry.addQrMapId(qrMap.getDurability());
        }

        return qrMap;
    }

    public QrMapRegistry getQrMapRegistry() {
        return qrMapRegistry;
    }
}