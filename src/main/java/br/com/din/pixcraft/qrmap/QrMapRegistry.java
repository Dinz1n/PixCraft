package br.com.din.pixcraft.qrmap;

import br.com.din.pixcraft.PixCraft;
import br.com.din.pixcraft.yaml.YamlDataManager;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class QrMapRegistry extends YamlDataManager {
    private static final Set<Integer> qrMapIds = new HashSet<>();

    protected QrMapRegistry() {
        super(PixCraft.getInstance(), "qrmaps.yml");
        loadData();
    }

    public void addQrMapId(int id) {
        if (qrMapIds.add(id)) {
            getFileConfiguration().set("active-qrmap-ids", new ArrayList<>(qrMapIds));
            save();
        }
    }

    public void removeQrMapId(int id) {
        if (qrMapIds.remove(id)) {
            getFileConfiguration().set("active-qrmap-ids", new ArrayList<>(qrMapIds));
            save();
        }
    }

    public boolean containsQrMapId(int id) {
        return qrMapIds.contains(id);
    }

    @Override
    protected void loadData() {
        qrMapIds.clear();
        qrMapIds.addAll(getFileConfiguration().getIntegerList("active-qrmap-ids"));
    }
}
