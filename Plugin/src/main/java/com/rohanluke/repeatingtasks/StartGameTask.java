package com.rohanluke.repeatingtasks;

import com.rohanluke.game.Main;
import com.rohanluke.utils.Pair;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class StartGameTask extends GameTask {

    int secondsPassed = 0;

    public StartGameTask(Main main, long delay, long period) {
        super(main, delay, period);
    }

    @Override
    public void run() {
        Bukkit.broadcastMessage(ChatColor.GREEN + "\n\nGame starting in " + (10-secondsPassed) + " seconds!");
        secondsPassed++;
        TextComponent join = new TextComponent(ChatColor.LIGHT_PURPLE + "" + ChatColor.UNDERLINE + "Click here to join!");
        join.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/join"));
        join.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("join UwU")));
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (main.playersJoined.containsKey(p)) continue;
            p.spigot().sendMessage(join);
        }
        if (secondsPassed >= 10) {
            for (Player p : main.playersJoined.keySet()) {
                main.roundsWonByPlayers.add(new Pair<>(p, 0));
            }
            new StartRoundTask(main, 0L, 20L).start();
            stop();
        }
    }
}