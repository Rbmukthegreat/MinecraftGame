package com.rohanluke.utils;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.concurrent.ConcurrentMap;

public class Utils {
    public static double round(double toRound, int decimalPlaces) {
        return Math.round(toRound * Math.pow(10.0, decimalPlaces))/Math.pow(10.0, decimalPlaces);
    }

    public static void alertPlayerOfCooldown(ConcurrentMap<Player, Long> cmap, Player player) {
        player.sendMessage(ChatColor.RED + "You have " + ChatColor.GREEN + (cmap.get(player) - System.currentTimeMillis())/1000.0 + ChatColor.RED + " seconds before you can do that again!");
    }

    public static void setItemName(ItemStack is, String name) {
        ItemMeta m = is.getItemMeta();
        assert m != null;
        m.setDisplayName(name);
        is.setItemMeta(m);
    }
}