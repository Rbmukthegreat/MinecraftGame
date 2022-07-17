package com.rohanluke.game;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.rohanluke.commands.*;
import com.rohanluke.events.EventManager;
import com.rohanluke.repeatingtasks.RoundTask;
import com.rohanluke.utils.Triplet;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public final class Main extends JavaPlugin implements Listener {

    // Game information
    public GameState gameState = GameState.OFF;
    public HashMap<Player, Triplet<ItemStack[], GameMode, Location>> playersJoined = new HashMap<>();
    public HashMap<Player, Integer> roundsWonByPlayers = new HashMap<>();
    public ArrayList<Player> playersDead = new ArrayList<>();
    public ArrayList<Location> spawnLocs = new ArrayList<>();
    public int roundNumber = 0;
    public RoundTask round;
    public boolean gameStarted = false;

    // Cooldowns
    public Cache<Player, Long> playersOnPearlCD = CacheBuilder.newBuilder().expireAfterWrite(10000, TimeUnit.MILLISECONDS).build();
    public Cache<Player, Long> playersOnSnowballCD = CacheBuilder.newBuilder().expireAfterWrite(8000, TimeUnit.MILLISECONDS).build();
    public Cache<Player, Long> playersOnFishingCD = CacheBuilder.newBuilder().expireAfterWrite(4000, TimeUnit.MILLISECONDS).build();
    public HashMap<Player, ArrayList<Snowball>> playersThrownSnowball = new HashMap<>();

    // Rounds

    // Jail
    public ArrayList<Player> playersJailed = new ArrayList<>();

    @Override
    public void onEnable() {
        System.out.println("Woohoo! my plugin has started!!!");

        // Commands
        Objects.requireNonNull(getCommand("startGame")).setExecutor(new StartGameCommand(this));
        Objects.requireNonNull(getCommand("stopGame")).setExecutor(new StopGameCommand(this));
        Objects.requireNonNull(getCommand("jail")).setExecutor(new JailCommand(this));
        Objects.requireNonNull(getCommand("join")).setExecutor(new JoinCommand(this));
        Objects.requireNonNull(getCommand("forceJoinAll")).setExecutor(new AllJoinCommand(this));

        // Events
        Bukkit.getPluginManager().registerEvents(new EventManager(this), this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}