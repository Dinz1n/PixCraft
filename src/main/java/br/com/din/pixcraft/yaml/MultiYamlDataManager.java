package br.com.din.pixcraft.yaml;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

public abstract class MultiYamlDataManager<T> {
    private final JavaPlugin plugin;
    private final File folder;
    private final Map<String, T> loadedData = new HashMap<>();

    public MultiYamlDataManager(JavaPlugin plugin, String folderName, String... defaultResources) {
        this.plugin = plugin;
        this.folder = new File(plugin.getDataFolder(), folderName);

        if (!folder.exists()) {
            folder.mkdirs();
            for (String resourcePath : defaultResources) {
                copyDefaultFile(resourcePath, new File(folder, new File(resourcePath).getName()));
            }
        }
    }

    private void copyDefaultFile(String resourcePath, File outputFile) {
        try (InputStream in = plugin.getResource(resourcePath)) {
            if (in == null) {
                plugin.getLogger().warning("Arquivo padrão não encontrado no JAR: " + resourcePath);
                return;
            }
            Files.copy(in, outputFile.toPath());
            plugin.getLogger().info("Arquivo padrão copiado: " + outputFile.getName());
        } catch (IOException e) {
            plugin.getLogger().severe("Falha ao copiar arquivo padrão " + outputFile.getName() + ": " + e.getMessage());
        }
    }

    protected void loadAll() {
        loadedData.clear();
        File[] files = folder.listFiles((dir, name) -> name.endsWith(".yml"));
        if (files == null) return;

        for (File file : files) {
            FileConfiguration config = YamlConfiguration.loadConfiguration(file);
            T data = loadSingleData(config, file.getName().replace(".yml", ""));
            if (data != null) {
                loadedData.put(file.getName().replace(".yml", ""), data);
            }
        }
    }

    public void reload() {
        loadAll();
    }

    protected abstract T loadSingleData(FileConfiguration config, String fileName);

    public T get(String id) {
        return loadedData.get(id);
    }

    public Collection<T> getAll() {
        return loadedData.values();
    }
}