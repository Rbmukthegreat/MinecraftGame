package com.rohanluke.commands;

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

public class JoinCommand implements CommandExecutor {
    Main main;

    public JoinCommand(Main main) {
        this.main = main;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "You can't do that!");
            return false;
        }
        Player p = (Player) sender;
        if (main.playersJoined.entrySet().contains(p)) {
            p.sendMessage(ChatColor.RED + "You have already joined!");
            return false;
        }
        GameMode oldGameMode = p.getGameMode();
        ItemStack[] oldInventory = p.getInventory().getContents();
        Location oldLoc = p.getLocation();
        main.playersJoined.put(p, new Triplet<>(oldInventory, oldGameMode, oldLoc));
        sender.sendMessage(ChatColor.GREEN + "You joined the game!");
        return true;
    }
}
