package br.com.din.pixcraft.yaml;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

public abstract class YamlDataManager<T> {
    private final JavaPlugin plugin;
    private final File file;
    private final String fileName;
    private final FileConfiguration fileConfiguration;

    protected YamlDataManager(JavaPlugin plugin, String fileName) {
        this.plugin = plugin;
        this.fileName = fileName;
        this.file = new File(plugin.getDataFolder(), fileName);
        if (!file.exists()) {
            saveDefaultFile();
        }
        fileConfiguration = YamlConfiguration.loadConfiguration(file);
    }

    private void saveDefaultFile() {
        try (InputStream inputStream = plugin.getResource(fileName)) {
            if (inputStream != null) {
                Files.copy(inputStream, file.toPath());
            } else {
                file.getParentFile().mkdirs();
                file.createNewFile();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected void save() {
        try {
            fileConfiguration.save(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void load() {
        try {
            fileConfiguration.load(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InvalidConfigurationException e) {
            throw new RuntimeException(e);
        }
    }

    protected abstract void loadData();

    protected FileConfiguration getFileConfiguration() {
        return fileConfiguration;
    }

    public void reload() {
        if (file.exists()) {
            load();
        } else {
            saveDefaultFile();
        }
        loadData();
    }
}