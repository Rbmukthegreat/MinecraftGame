package com.rohanluke.commands;

import com.rohanluke.game.GameState;
import com.rohanluke.game.Main;
import com.rohanluke.utils.Triplet;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

public class StopGameCommand implements CommandExecutor {

    Main main;

    public StopGameCommand(Main main) {
        this.main = main;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (main.gameState == GameState.OFF || main.gameState == GameState.ROUND_STARTING) {
            sender.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "You can't do that!");
            return false;
        }
        stopAll(main);
        sender.sendMessage(ChatColor.BOLD + "New game state: " + main.gameState);
        return false;
    }

    public static void stopAll(Main main) {
        main.round.stop();
        main.round = null;
        main.gameState = GameState.OFF;
        main.cooldownTask.stop();
        main.roundsWonByPlayers.clear();
        main.playersDead.clear();
        main.roundNumber = 0;
        resetAllInventories(main);
    }

    private static void resetAllInventories(Main main) {
        for (HashMap.Entry<Player, Triplet<ItemStack[], GameMode, Location>> entry : main.playersJoined.entrySet()) {
            Player p = entry.getKey();
            p.getInventory().setContents(entry.getValue().first);
            p.setGameMode(entry.getValue().second);
            p.teleport(entry.getValue().third);
        }
        main.playersJoined.clear();
    }
}
