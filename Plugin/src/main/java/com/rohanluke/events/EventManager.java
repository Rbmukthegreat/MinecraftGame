package com.rohanluke.events;

import com.rohanluke.game.Main;
import com.rohanluke.utils.Triplet;
import com.rohanluke.utils.Utils;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.Arrays;

public class EventManager implements Listener {

    Main main;

    public EventManager(Main main) {
        this.main = main;
    }

    @EventHandler
    public void onPlayerTakeDamage(EntityDamageEvent e) {
        if (e.getEntity() instanceof Player) {
            e.setDamage(0);
        }
    }

    @EventHandler
    public void onPlayerFoodLevelChange(FoodLevelChangeEvent e) {
        if (e.getEntity() instanceof Player)
            e.setCancelled(true);
    }

    @EventHandler
    public void onPlayerFish(PlayerFishEvent e) {
        if (!main.gameState) return;
        if ((e.getState() == PlayerFishEvent.State.REEL_IN || e.getState() == PlayerFishEvent.State.IN_GROUND || e.getState() == PlayerFishEvent.State.CAUGHT_ENTITY) && main.playersOnFishingCD.containsKey(e.getPlayer())) {
            Utils.alertPlayerOfCooldown(main.playersOnFishingCD, e.getPlayer());
            return;
        }
        if (e.getState() == PlayerFishEvent.State.REEL_IN || e.getState() == PlayerFishEvent.State.IN_GROUND) {
            Player p = e.getPlayer();
            sendPlayer(p, p.getLocation(), e.getHook().getLocation(), false);
            main.playersOnFishingCD.put(e.getPlayer(), 5.0);
        }
        else if (e.getState() == PlayerFishEvent.State.CAUGHT_ENTITY) {
            if (e.getHook().getHookedEntity() instanceof Player) {
                Player p = (Player)e.getHook().getHookedEntity();
                Location newLocation = p.getLocation();
                newLocation.setY(newLocation.getY() + 1);
                p.teleport(newLocation);
                sendPlayer(p, e.getPlayer().getLocation(), p.getLocation(), true);
            } else {
                Player p = e.getPlayer();
                sendPlayer(p, p.getLocation(), e.getHook().getLocation(), false);
            }
            main.playersOnFishingCD.put(e.getPlayer(), 5.0);
        }
    }

    public void sendPlayer(Player p, Location from, Location to, boolean flag) {
        Vector dir = new Vector(to.getX() - from.getX(), 0.25, to.getZ() - from.getZ());
        p.setVelocity(dir.multiply(flag ? -4 : 2));
    }

    @EventHandler
    public void onPlayerSnowball(ProjectileHitEvent e) {
        if (!main.gameState) return;
        if (e.getEntity() instanceof Snowball) {
            Snowball s = (Snowball) e.getEntity();
            if (s.getShooter() instanceof Player && e.getHitEntity() instanceof Player) {
                Player p1 = (Player)s.getShooter();
                if (main.playersOnSnowballCD.containsKey(p1)) {
                    e.setCancelled(true);
                    Utils.alertPlayerOfCooldown(main.playersOnSnowballCD, p1);
                    return;
                }
                Player p2 = (Player)e.getHitEntity();
                Location l1 = p1.getLocation();
                p1.teleport(p2.getLocation());
                p2.teleport(l1);
                main.playersOnSnowballCD.put(p1, 16.0);
            }
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e) {
        if (!main.gameState) return;
        Player p = e.getPlayer();
        if (!main.playersDeadList.containsKey(p) && p.getLocation().getY() < 85) {
            ItemStack[] oldInventory = Arrays.copyOf(p.getInventory().getContents(), p.getInventory().getContents().length);
            GameMode oldGameMode = p.getGameMode();
            Location oldLocation = new Location(p.getLocation().getWorld(), p.getLocation().getX(), p.getLocation().getY(), p.getLocation().getZ());
            main.playersDeadList.put(p, new Triplet<>(oldInventory, oldGameMode, oldLocation));
            p.setGameMode(GameMode.SPECTATOR);
            p.sendMessage("You died!");
            p.getInventory().clear();
        }
    }

    @EventHandler
    public void onPlayerPearl(ProjectileLaunchEvent e) {
        if (!main.gameState) return;
        if (!(e.getEntity() instanceof EnderPearl) || !(e.getEntity().getShooter() instanceof Player)) return;
        // TODO: add taking other people's pearls
        Player shooter = (Player)e.getEntity().getShooter();
        if (main.playersOnPearlCD.containsKey(shooter)) {
            e.setCancelled(true);
            Utils.alertPlayerOfCooldown(main.playersOnPearlCD, shooter);
            return;
        }
        main.playersOnPearlCD.put((Player)e.getEntity().getShooter(), 16.0);
    }
}