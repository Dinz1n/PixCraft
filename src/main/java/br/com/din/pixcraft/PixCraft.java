package br.com.din.pixcraft;

import br.com.din.pixcraft.utils.AsciiArtUtils;
import org.bukkit.plugin.java.JavaPlugin;

public final class PixCraft extends JavaPlugin {
    private static JavaPlugin instance;
    private String pluginVersion;

    @Override
    public void onEnable() {
        instance = this;
        AsciiArtUtils.printAsciiArt(getLogger());


    }

    @Override
    public void onDisable() {

    }

    public static JavaPlugin getInstance() {
        return instance;
    }

    public static String getPluginVersion() {
        return getPluginVersion();
    }
}
