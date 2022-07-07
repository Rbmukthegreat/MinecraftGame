package com.rohanluke.game;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.Vector;

import java.util.ArrayList;

public class EventManager implements Listener {
    boolean flag = false;
    ArrayList<Player> playersDead = new ArrayList<>();

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
        if (e.getState() == PlayerFishEvent.State.REEL_IN || e.getState() == PlayerFishEvent.State.IN_GROUND) {
            Player p = e.getPlayer();
            sendPlayer(p, p.getLocation(), e.getHook().getLocation());
        }
        if (e.getState() == PlayerFishEvent.State.CAUGHT_ENTITY) {
            if (e.getHook().getHookedEntity() instanceof Player) {
                Player p = (Player)e.getHook().getHookedEntity();
                sendPlayer(p, p.getLocation(), e.getPlayer().getLocation());
            } else {
                Player p = e.getPlayer();
                sendPlayer(p, p.getLocation(), e.getHook().getLocation());
            }
        }
    }

    public void sendPlayer(Player p, Location from, Location to) {
        Vector dir = new Vector(to.getX() - from.getX(), to.getY() - from.getY(), to.getZ() - from.getZ());
        p.setVelocity(dir.multiply(0.25));
    }

    @EventHandler
    public void onPlayerSnowball(ProjectileHitEvent e) {
        if (e.getEntity() instanceof Snowball) {
            Snowball s = (Snowball) e.getEntity();
            if (s.getShooter() instanceof Player && e.getHitEntity() instanceof Player) {
                Player p1 = (Player)s.getShooter();
                Player p2 = (Player)e.getHitEntity();
                Location l1 = p1.getLocation();
                p1.teleport(p2.getLocation());
                p2.teleport(l1);
            }
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e) {
        if (!flag) return;
        Player p = e.getPlayer();
        if (!playersDead.contains(p) && p.getLocation().getY() < 85) {
            p.setGameMode(GameMode.SPECTATOR);
            p.sendMessage("You died!");
            playersDead.add(p);
        }
    }
}
