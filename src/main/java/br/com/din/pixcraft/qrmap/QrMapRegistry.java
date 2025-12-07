package br.com.din.pixcraft.qrmap;

import java.util.HashSet;
import java.util.Set;

public class QrMapRegistry {
    private static final Set<Integer> qrMapIds = new HashSet<>();

    private QrMapRegistry(){}

    public static void addQrMapId(int id) {
        qrMapIds.add(id);
    }

    public static void removeQrMapId(int id) {
        qrMapIds.remove(id);
    }

    public static boolean containsQrMapId(int id) {
        return qrMapIds.contains(id);
    }
}
