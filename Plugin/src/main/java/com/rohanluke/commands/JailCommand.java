package com.rohanluke.commands;

import com.rohanluke.game.Main;
import com.rohanluke.repeatingtasks.JailPlayerTask;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class JailCommand implements CommandExecutor {
    private final Main main;

    public JailCommand(Main main) {
        this.main = main;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (Bukkit.getPlayer(args[0]) == null) {
            sender.sendMessage(ChatColor.RED + "That is not a player!");
            return false;
        }
        Player p = Bukkit.getPlayer(args[0]);
        if (main.playersJailed.contains(p)) {
            sender.sendMessage(ChatColor.RED + "You cannot jail a player already in jail!");
            return false;
        }
        assert p != null;
        new JailPlayerTask(main, 0L, 20L, p).start();
        return false;
    }
}
