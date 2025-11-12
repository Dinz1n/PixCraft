package br.com.din.pixcraft.yaml;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class MultiYamlDataManager<T> {
    private final JavaPlugin plugin;
    private final File folder;
    private final Map<String, T> loadedData = new HashMap<>();
    private final String[] defaultResources;

    public MultiYamlDataManager(JavaPlugin plugin, String folderName, String... defaultResources) {
        this.plugin = plugin;
        this.defaultResources = defaultResources;
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
        } catch (IOException e) {
            plugin.getLogger().severe("Falha ao copiar arquivo padrão " + outputFile.getName() + ": " + e.getMessage());
        }
    }

    protected void loadAll() {
        loadedData.clear();
        List<File> yamlFiles = new ArrayList<>();
        findYamlFiles(folder, yamlFiles);

        for (File file : yamlFiles) {
            FileConfiguration config = YamlConfiguration.loadConfiguration(file);
            T data = loadSingleData(config, file.getName().replace(".yml", ""));
            if (data != null) {
                loadedData.put(file.getName().replace(".yml", ""), data);
            }
        }
    }

    private void findYamlFiles(File dir, List<File> yamlFiles) {
        if (!dir.exists() || !dir.isDirectory()) {
            return;
        }

        File[] files = dir.listFiles();
        if (files == null) {
            return;
        }

        for (File file : files) {
            if (file.isDirectory()) {
                findYamlFiles(file, yamlFiles);
            } else if (file.getName().endsWith(".yml")) {
                yamlFiles.add(file);
            }
        }
    }

    public void reload() {
        if (!folder.exists()) {
            folder.mkdirs();
            for (String resourcePath : defaultResources) {
                copyDefaultFile(resourcePath, new File(folder, new File(resourcePath).getName()));
            }
        }
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
