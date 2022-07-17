package com.rohanluke.repeatingtasks;

import com.rohanluke.game.Main;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class JailPlayerTask extends GameTask{
    private final Player p;
    private final Location base;
    private final World world;
    private int secondsInJail = 0;

    public JailPlayerTask(Main main, long delay, long period, Player p) {
        super(main, delay, period);
        this.p = p;
        this.base = p.getLocation();
//        base.setX(Utils.round(base.getX(), 0));
//        base.setY(Utils.round(base.getY(), 0) + 40);
        base.setY(base.getY() + 40);
//        base.setZ(Utils.round(base.getZ(), 0));
        base.setPitch(0);
        base.setYaw(0);
        main.playersJailed.add(p);
        world = base.getWorld();
        p.teleport(new Location(world, base.getX(), base.getY() + 1, base.getZ()));
        p.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "You are in jail!");
        constructJail();
    }

    @Override
    public void toRun() {
        if (secondsInJail <= 3) {
            constructJail();
        }
        if (secondsInJail >= 20) {
            stop();
        }
        p.sendMessage(ChatColor.RED + "You have " + (20 - secondsInJail) + " seconds left in jail!");
        secondsInJail++;
    }

    private void constructJail() {
        if (secondsInJail == 0 || secondsInJail == 3) {
            Location newLoc = new Location(world, base.getX(), base.getY() + secondsInJail, base.getZ());
            world.getBlockAt(newLoc).setType(Material.REDSTONE_BLOCK);
        }
        constructPanes();
    }

    private void constructPanes() {
        double[][] locs = {{1.0, 0.0}, {-1.0, 0.0}, {0.0, 1.0}, {0.0, -1.0}, {1.0, 1.0}, {-1.0, 1.0}, {1.0, -1.0}, {-1.0, -1.0}};
        for (int i = 0; i < locs.length; ++i) {
            Location newLoc = new Location(world, base.getX() + locs[i][0], base.getY() + secondsInJail, base.getZ() + locs[i][1]);
            world.getBlockAt(newLoc).setType(Material.IRON_BARS);
        }
    }

    @Override
    public void onStop() {
        removeJail();
        main.playersJailed.remove(p);
    }

    private void removeJail() {
        int[][] locs = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}, {1, 1}, {-1, 1}, {1, -1}, {-1, -1}};
        for (int i = 0; i < 4; ++i) {
            for (int[] loc : locs) {
                Location newLoc = new Location(world, base.getX() + loc[0], base.getY() + i, base.getZ() + loc[1]);
                world.getBlockAt(newLoc).setType(Material.AIR);
            }
        }
        world.getBlockAt(base).setType(Material.AIR);
        Location finalLoc = new Location(world, base.getX(), base.getY() + 3, base.getZ());
        world.getBlockAt(finalLoc).setType(Material.AIR);
    }
}