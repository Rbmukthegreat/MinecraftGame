package com.rohanluke.repeatingtasks;

import com.rohanluke.game.Main;
import com.rohanluke.utils.Utils;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class RoundTask extends GameTask{
    public RoundTask(Main main, long delay, long period) {
        super(main, delay, period);
        main.cooldownTask.start();
        changePlayersToStartingPosition();
    }

    @Override
    public void run() {

    }

    public void changePlayersToStartingPosition() {
        for (Player p : main.playersJoined.keySet()) {
            readyPlayerForGame(p);
        }
    }

    public void readyPlayerForGame(Player p) {
        GameMode newGameMode = GameMode.SURVIVAL;
        Location newLocation = new Location(Bukkit.getWorlds().get(0), 57, 146, -163, 0, 0);
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
}
