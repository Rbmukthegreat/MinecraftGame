package com.rohanluke.commands;

import com.rohanluke.game.Main;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AllJoinCommand implements CommandExecutor {

    Main main;
    public AllJoinCommand(Main main) {
        this.main = main;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        for (Player p : Bukkit.getOnlinePlayers()) {
            p.performCommand("join");
        }
        return false;
    }
}
