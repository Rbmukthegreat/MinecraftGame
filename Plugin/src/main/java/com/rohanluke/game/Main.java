package com.rohanluke.game;

import com.rohanluke.commands.SendTitleCommand;
import com.rohanluke.commands.ToggleGameCommand;
import com.rohanluke.events.EventManager;
import com.rohanluke.utils.Triplet;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public final class Main extends JavaPlugin implements Listener {
    public boolean gameState = false;
    public HashMap<Player, Triplet<ItemStack[], GameMode, Location>> playersDeadList = new HashMap<>();

    // Cooldowns
    public ArrayList<HashMap<Player, Double>> cooldowns = new ArrayList<>();
    public HashMap<Player, Double> playersOnPearlCD = new HashMap<>();
    public HashMap<Player, Double> playersOnSnowballCD = new HashMap<>();
    public HashMap<Player, Double> playersOnFishingCD = new HashMap<>();

    @Override
    public void onEnable() {
        System.out.println("Woohoo! my plugin has started!!!");

        // Cooldowns
        cooldowns.add(playersOnSnowballCD);
        cooldowns.add(playersOnPearlCD);
        cooldowns.add(playersOnFishingCD);

        // Commands
        Objects.requireNonNull(getCommand("toggleGame")).setExecutor(new ToggleGameCommand(this));
        Objects.requireNonNull(getCommand("sendTitle")).setExecutor(new SendTitleCommand(this));

        // Events
        Bukkit.getPluginManager().registerEvents(new EventManager(this), this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}