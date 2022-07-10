package com.rohanluke.commands;

import com.rohanluke.game.Main;
import com.rohanluke.runnables.SendTitleRunnable;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class SendTitleCommand implements CommandExecutor {

    public static boolean isOn = false;

    Main main;
    public SendTitleCommand(Main main) {
        this.main = main;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (isOn) {
            sender.sendMessage("You can't run this while its already running!");
            return false;
        }
        isOn = true;
        new SendTitleRunnable(main, 0L, 20L).start();
        return false;
    }
}
