package com.rohanluke.repeatingtasks;

import com.rohanluke.game.Main;
import org.bukkit.Bukkit;

public abstract class GameTask implements Runnable {

    public final Main main;

    private int taskId;
    private final long delay, period;
    private long stopAfter = -1;
    public int timePassed = 0;
    public boolean started = false;

    public void start() {
        if (started) {
            throw new RuntimeException("Cannot start a task that is already started!");
        }
        taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(main, this, delay, period);

    }

    public void stop() {
        started = false;
        onStop();
        Bukkit.getScheduler().cancelTask(taskId);
    }

    public void onStop() {}

    public GameTask(Main main, long delay, long period) {
        this.main = main;
        this.delay = delay;
        this.period = period;
    }

    public GameTask(Main main, long delay, long period, long stopAfter) {
        this.main = main;
        this.delay = delay;
        this.period = period;
        this.stopAfter = stopAfter;
    }

    @Override
    public final void run() {
        if (stopAfter != -1) {
            if (timePassed >= stopAfter) stop();
            timePassed++;
        }
        toRun();
    }

    public abstract void toRun();
}
