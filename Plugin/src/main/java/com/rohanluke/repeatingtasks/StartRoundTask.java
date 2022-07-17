package com.rohanluke.repeatingtasks;

import com.rohanluke.game.GameState;
import com.rohanluke.game.Main;
import com.rohanluke.utils.Utils;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Random;

public class StartRoundTask extends GameTask {

    private int secondsPassed;

    public StartRoundTask(Main main, long delay, long period) {
        super(main, delay, period);
        main.round = new RoundTask(main, 0, 20L);
        changePlayersToStartingPosition();
        this.secondsPassed = 0;
        main.playersDead.clear();
    }

    @Override
    public void toRun() {
        if (main.playersJoined.size() == 0) { // TODO: change this to 1
            stop();
            Bukkit.broadcastMessage(ChatColor.RED + "Nobody joined!");
            return;
        }
        main.gameState = GameState.ROUND_STARTING;
        for (Player p : Bukkit.getOnlinePlayers()) {
            p.sendTitle("Round " + (main.roundNumber + 1) + " staring in... " + ChatColor.RED + (5 - secondsPassed) + "...", "");
        }
        secondsPassed++;
        if (secondsPassed > 5) {
            for (Player p : Bukkit.getOnlinePlayers()) {
                p.sendTitle("Game start!", "");
                removeSpawnCage(Bukkit.getWorlds().get(0));
                main.gameState = GameState.ROUND;
                main.round.start();
            }
            stop();
        }
    }

    public void changePlayersToStartingPosition() {
        for (Player p : main.playersJoined.keySet()) {
            readyPlayerForGame(p);
        }
    }

    public void readyPlayerForGame(Player p) {
        GameMode newGameMode = GameMode.SURVIVAL;
        int x, y, z;
        Random random = new Random();
        x = random.nextInt(53, 104);
        y = random.nextInt(176, 215);
        z = random.nextInt(-194, -157);
        World world = Bukkit.getWorlds().get(0);
        Location l = new Location(world, x, y, z, 0, 0);
        main.spawnLocs.add(l);
        buildSpawnCage(world, l);
        Location newLocation = new Location(world, x+0.5, y+1, z+0.5, 0, 0);
        ItemStack fishingRod = new ItemStack(Material.FISHING_ROD);
        fishingRod.addUnsafeEnchantment(Enchantment.DURABILITY, 6969);
        fishingRod.addUnsafeEnchantment(Enchantment.KNOCKBACK, 6969);
        Utils.setItemName(fishingRod, ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "Eva");
        ItemStack pearl = new ItemStack(Material.ENDER_PEARL, 64);
        Utils.setItemName(pearl, ChatColor.RED + "" + ChatColor.BOLD + "Weiner");
        pearl.addUnsafeEnchantment(Enchantment.DURABILITY, 6969);
        pearl.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 6969);
        ItemStack snowball = new ItemStack(Material.SNOWBALL, 64);
        Utils.setItemName(snowball, ChatColor.GREEN + "" + ChatColor.BOLD + "Ayla");
        snowball.addUnsafeEnchantment(Enchantment.LUCK, 420);
        snowball.addUnsafeEnchantment(Enchantment.BINDING_CURSE, 80085);
        ItemStack[] newInventory = new ItemStack[] {fishingRod, pearl, snowball, pearl, snowball, pearl, snowball, pearl};
        p.setGameMode(newGameMode);
        p.teleport(newLocation);
        p.getInventory().setContents(newInventory);
    }

    public void buildSpawnCage(World w, Location l) {
        w.getBlockAt(l).setType(Material.GLASS);
        int[][] locs = {{1, 0}, {0, 1}, {-1, 0}, {0, -1}, {1, 1}, {-1, 1}, {1, -1}, {-1, -1}};
        for (int i = 0; i < 4; ++i) {
            for (int[] loc : locs) {
                Location newLoc = new Location(l.getWorld(), l.getX() + loc[0], l.getY() + i, l.getZ() + loc[1]);
                w.getBlockAt(newLoc).setType(Material.GLASS);
            }
        }
        Location finalLoc = new Location(l.getWorld(), l.getX(), l.getY() + 3, l.getZ());
        w.getBlockAt(finalLoc).setType(Material.GLASS);
        w.loadChunk((int)l.getX(), (int)l.getZ());
    }

    public void removeSpawnCage(World w) {
        for (Location l : main.spawnLocs) {
            w.getBlockAt(l).setType(Material.AIR);
            int[][] locs = {{1, 0}, {0, 1}, {-1, 0}, {0, -1}, {1, 1}, {-1, 1}, {1, -1}, {-1, -1}};
            for (int i = 0; i < 4; ++i) {
                for (int[] loc : locs) {
                    Location newLoc = new Location(l.getWorld(), l.getX() + loc[0], l.getY() + i, l.getZ() + loc[1]);
                    w.getBlockAt(newLoc).setType(Material.AIR);
                }
            }
            Location finalLoc = new Location(l.getWorld(), l.getX(), l.getY() + 3, l.getZ());
            w.getBlockAt(finalLoc).setType(Material.AIR);
        }
    }
}
