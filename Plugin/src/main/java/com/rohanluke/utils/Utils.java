package com.rohanluke.utils;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class Utils {
    public static double round(double d, int decimalPlaces) {
        return Math.round(d * Math.pow(10.0, decimalPlaces))/Math.pow(10.0, decimalPlaces);
    }

    public static void alertPlayerOfCooldown(HashMap<Player, Double> hashmap, Player player) {
        player.sendMessage(ChatColor.RED + "You have " + ChatColor.GREEN + Utils.round(hashmap.get(player), 1) + ChatColor.RED + " seconds before you can do that again!");
    }
}
