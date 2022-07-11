package com.rohanluke.repeatingtasks;

import com.rohanluke.game.Main;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class CooldownTask extends GameTask {

    public CooldownTask(Main main, long delay, long period) {
        super(main, delay, period);
    }

    @Override
    public void run() {
        for (int i = 0; i < main.cooldowns.size(); ++i) {
            for (HashMap.Entry<Player, Double> entry : main.cooldowns.get(i).entrySet()) {
                if (entry.getValue() <= 0.0001) {
                    main.cooldowns.get(i).remove(entry.getKey());
                    continue;
                }
                Double newTime = entry.getValue() - 0.1;
                main.cooldowns.get(i).replace(entry.getKey(), newTime);
            }
        }
    }

    @Override
    public void onStop() {
        for (int i = 0; i < main.cooldowns.size(); ++i) {
            main.cooldowns.get(i).clear();
        }
    }
}