package br.com.din.pixcraft.utils;

import de.tr7zw.changeme.nbtapi.NBTItem;

import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;

public class NBTUtils {
    private NBTUtils() {}

    public static ItemStack setTag(ItemStack item, String key, String value) {
        NBTItem nbtItem = new NBTItem(item);
        nbtItem.setString(key, value);
        return nbtItem.getItem();
    }

    public static ItemStack setTag(ItemStack item, String key, int value) {
        NBTItem nbtItem = new NBTItem(item);
        nbtItem.setInteger(key, value);
        return nbtItem.getItem();
    }

    public static ItemStack setTag(ItemStack item, String key, double value) {
        NBTItem nbtItem = new NBTItem(item);
        nbtItem.setDouble(key, value);
        return nbtItem.getItem();
    }

    public static ItemStack setTag(ItemStack item, String key, boolean value) {
        NBTItem nbtItem = new NBTItem(item);
        nbtItem.setBoolean(key, value);
        return nbtItem.getItem();
    }

    public static ItemStack setTag(ItemStack item, String key, byte value) {
        NBTItem nbtItem = new NBTItem(item);
        nbtItem.setByte(key, value);
        return nbtItem.getItem();
    }

    public static String getString(ItemStack item, String key) {
        return new NBTItem(item).getString(key);
    }

    public static int getInt(ItemStack item, String key) {
        return new NBTItem(item).getInteger(key);
    }

    public static double getDouble(ItemStack item, String key) {
        return new NBTItem(item).getDouble(key);
    }

    public static boolean getBoolean(ItemStack item, String key) {
        return new NBTItem(item).getBoolean(key);
    }

    public static byte getByte(ItemStack item, String key) {
        return new NBTItem(item).getByte(key);
    }

    public static boolean hasTag(ItemStack item, String key) {
        return new NBTItem(item).hasKey(key);
    }

    public static ItemStack removeTag(ItemStack item, String key) {
        NBTItem nbtItem = new NBTItem(item);
        nbtItem.removeKey(key);
        return nbtItem.getItem();
    }
}
