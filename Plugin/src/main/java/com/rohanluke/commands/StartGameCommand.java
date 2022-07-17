package com.rohanluke.commands;

import com.rohanluke.game.GameState;
import com.rohanluke.game.Main;
import com.rohanluke.repeatingtasks.StartGameTask;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class StartGameCommand implements CommandExecutor {

    private final Main main;
    public StartGameCommand(Main main) {
        this.main = main;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (main.gameStarted || main.gameState == GameState.ROUND_STARTING || main.gameState == GameState.ROUND) { /// TODO: MAKE NEW ENUM!
            sender.sendMessage(ChatColor.RED + "You can't do that!");
            return false;
        }
        main.gameStarted = true;
        new StartGameTask(main, 0L, 20L).start();
        return false;
    }
}