package com.rohanluke.repeatingtasks;

import com.rohanluke.game.Main;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.util.Vector;

public class SnowballAimbotTask extends GameTask {
    private final Snowball s;
    private Player closest = null;
    public SnowballAimbotTask(Main main, long l, long l1, Snowball s, Player shooter) {
        super(main, l, l1);
        this.s = s;
        double mindist = Double.MAX_VALUE;
        for (Player p : main.playersJoined.keySet()) {
            if (p == shooter || main.playersDead.contains(p)) continue;
            double dist = distance(s.getLocation(), p.getLocation());
            if (dist < mindist) {
                mindist = dist;
                closest = p;
            }
        }
        assert closest != null;
    }

    @Override
    public void toRun() {
        if (s.isDead()) {
            this.stop();
            return;
        }
        Location l1 = s.getLocation();
        Location l2 = closest.getLocation();
        Vector direction = new Vector(l2.getX() - l1.getX(), l2.getY() - l1.getY()+1, l2.getZ() - l1.getZ());
        direction.multiply(0.25);
        s.setVelocity(direction);
    }

    private double distance(Location l1, Location l2) {
        return Math.sqrt(Math.pow(l1.getX() - l2.getX(), 2) + Math.pow(l1.getY() - l2.getY(), 2) + Math.pow(l1.getZ() - l2.getZ(), 2));
    }
}
