package com.rohanluke.game;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        // Plugin startup logic
        System.out.println("Woohoo! my plugin has started!!!");
        Bukkit.getPluginManager().registerEvents(new EventManager(), this);

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}