package com.rohanluke.commands;

import com.rohanluke.game.Main;
import com.rohanluke.runnables.CooldownRunnable;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.HashMap;

public class ToggleGameCommand implements CommandExecutor {

    private final Main main;
    private final CooldownRunnable cooldownRunnable;

    public ToggleGameCommand(Main main) {
        this.main = main;
        cooldownRunnable = new CooldownRunnable(main, 0L, 1L);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        main.gameState = !main.gameState;
        if (main.gameState) cooldownRunnable.start();
        else cooldownRunnable.stop();
        sender.sendMessage(ChatColor.BOLD + "New game state: " + main.gameState);
        main.playersDeadList.forEach((p, value) -> {
            p.getInventory().setContents(value.first);
            p.setGameMode(value.second);
            p.teleport(value.third);
        });
        main.playersDeadList = new HashMap<>();
        return false;
    }
}
