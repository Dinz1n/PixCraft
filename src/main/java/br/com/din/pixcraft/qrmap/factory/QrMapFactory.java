package br.com.din.pixcraft.qrmap.factory;

import org.bukkit.World;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public interface QrMapFactory {
    public ItemStack create(String qrData, World world, String displayName, List<String> lore);
}
