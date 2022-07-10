package com.rohanluke.runnables;

import com.rohanluke.commands.SendTitleCommand;
import com.rohanluke.game.Main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class SendTitleRunnable extends GameRunnable {

    private int cooldown = 5;

    public SendTitleRunnable(Main main, long delay, long period) {
        super(main, delay, period);
    }

    @Override
    public void run() {
        if (cooldown <= 0) {
            stop();
            SendTitleCommand.isOn = false;
        }
        for (Player p : Bukkit.getOnlinePlayers()) {
            p.sendTitle(ChatColor.RED + String.valueOf(cooldown) + "...", "");
        }
        cooldown--;
    }
}
