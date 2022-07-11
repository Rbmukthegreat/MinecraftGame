package com.rohanluke.repeatingtasks;

import com.rohanluke.game.GameState;
import com.rohanluke.game.Main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class StartRoundTask extends GameTask {

    private int secondsPassed;

    public StartRoundTask(Main main, long delay, long period) {
        super(main, delay, period);
        main.round = new RoundTask(main, 0, 20L);
        main.round.changePlayersToStartingPosition();
        this.secondsPassed = 0;
        main.playersDead.clear();
    }

    @Override
    public void run() {
        if (main.playersJoined.size() == 0) { // TODO: change this to 1
            stop();
            Bukkit.broadcastMessage(ChatColor.RED + "Nobody joined!");
            return;
        }
        main.gameState = GameState.ROUND_STARTING;
        for (Player p : Bukkit.getOnlinePlayers()) {
            p.sendTitle("Round " + (main.roundNumber + 1) + "staring in... " + ChatColor.RED + (3 - secondsPassed) + "...", "");
        }
        secondsPassed++;
        if (secondsPassed > 3) {
            for (Player p : Bukkit.getOnlinePlayers()) {
                p.sendTitle("Game start!", "");
                main.gameState = GameState.ROUND;
                main.round.start();
            }
            stop();
        }
    }
}
