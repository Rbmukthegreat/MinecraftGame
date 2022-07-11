package com.rohanluke.game;

import com.rohanluke.commands.JailCommand;
import com.rohanluke.commands.JoinCommand;
import com.rohanluke.commands.StartGameCommand;
import com.rohanluke.commands.StopGameCommand;
import com.rohanluke.events.EventManager;
import com.rohanluke.repeatingtasks.CooldownTask;
import com.rohanluke.repeatingtasks.RoundTask;
import com.rohanluke.utils.PQ;
import com.rohanluke.utils.PairComparator;
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

    // Game information
    public GameState gameState = GameState.OFF;
    public HashMap<Player, Triplet<ItemStack[], GameMode, Location>> playersJoined = new HashMap<>();
    public PQ roundsWonByPlayers = new PQ(1, new PairComparator());
    public ArrayList<Player> playersDead = new ArrayList<>();
    public int roundNumber = 0;
    public RoundTask round;

    // Cooldowns
    public final CooldownTask cooldownTask = new CooldownTask(this, 0L, 1L);
    public ArrayList<HashMap<Player, Double>> cooldowns = new ArrayList<>();
    public HashMap<Player, Double> playersOnPearlCD = new HashMap<>();
    public HashMap<Player, Double> playersOnSnowballCD = new HashMap<>();
    public HashMap<Player, Double> playersOnFishingCD = new HashMap<>();

    // Rounds

    // Jail
    public ArrayList<Player> playersJailed = new ArrayList<>();

    @Override
    public void onEnable() {
        System.out.println("Woohoo! my plugin has started!!!");

        // Cooldowns
        cooldowns.add(playersOnSnowballCD);
        cooldowns.add(playersOnPearlCD);
        cooldowns.add(playersOnFishingCD);

        // Commands
        Objects.requireNonNull(getCommand("startGame")).setExecutor(new StartGameCommand(this));
        Objects.requireNonNull(getCommand("stopGame")).setExecutor(new StopGameCommand(this));
        Objects.requireNonNull(getCommand("jail")).setExecutor(new JailCommand(this));
        Objects.requireNonNull(getCommand("join")).setExecutor(new JoinCommand(this));

        // Events
        Bukkit.getPluginManager().registerEvents(new EventManager(this), this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}