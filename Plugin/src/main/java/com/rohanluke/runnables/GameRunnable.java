package com.rohanluke.runnables;

import com.rohanluke.game.Main;
import org.bukkit.Bukkit;

public abstract class GameRunnable implements Runnable {

    public final Main main;

    private int taskId;
    private final long delay, period;

    public void start() {
        taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(main, this, delay, period);

    }

    public void stop() {
        Bukkit.getScheduler().cancelTask(taskId);
        onStop();
    }

    public void onStop() {}

    public GameRunnable(Main main, long delay, long period) {
        this.main = main;
        this.delay = delay;
        this.period = period;
    }

    @Override
    public abstract void run();
}
