package com.rohanluke.events;

import com.rohanluke.commands.StopGameCommand;
import com.rohanluke.game.GameState;
import com.rohanluke.game.Main;
import com.rohanluke.repeatingtasks.SnowballAimbotTask;
import com.rohanluke.repeatingtasks.StartRoundTask;
import com.rohanluke.utils.Pair;
import com.rohanluke.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class EventManager implements Listener {

    Main main;

    public EventManager(Main main) {
        this.main = main;
    }

    @EventHandler
    public void onPlayerTakeDamage(EntityDamageEvent e) {
        if (!(main.gameState == GameState.OFF) && e.getEntity() instanceof Player) {
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
        if (main.gameState == GameState.OFF || main.gameState == GameState.ROUND_STARTING) return;
        if ((e.getState() == PlayerFishEvent.State.REEL_IN || e.getState() == PlayerFishEvent.State.IN_GROUND || e.getState() == PlayerFishEvent.State.CAUGHT_ENTITY) && main.playersOnFishingCD.asMap().containsKey(e.getPlayer())) {
            Utils.alertPlayerOfCooldown(main.playersOnFishingCD.asMap(), e.getPlayer());
            return;
        }
        if (e.getState() == PlayerFishEvent.State.REEL_IN || e.getState() == PlayerFishEvent.State.IN_GROUND) {
            Player p = e.getPlayer();
            sendPlayer(p, p.getLocation(), e.getHook().getLocation(), false);
            main.playersOnFishingCD.put(e.getPlayer(), System.currentTimeMillis() + 4000);
        }
        else if (e.getState() == PlayerFishEvent.State.CAUGHT_ENTITY) {
            if (e.getHook().getHookedEntity() instanceof Player) {
                Player p = (Player)e.getHook().getHookedEntity();
                sendPlayer(p, e.getPlayer().getLocation(), p.getLocation(), true);
            } else {
                Player p = e.getPlayer();
                sendPlayer(p, p.getLocation(), e.getHook().getLocation(), false);
            }
            main.playersOnFishingCD.put(e.getPlayer(), System.currentTimeMillis() + 4000);
        }
    }

    public void sendPlayer(Player p, Location from, Location to, boolean flag) {
//        Location newLoc = p.getLocation();
//        newLoc.setY(newLoc.getY() + 1);
//        p.teleport(newLoc);
        Vector dir = new Vector(to.getX() - from.getX(), 0.25, to.getZ() - from.getZ());
        p.setVelocity(dir.multiply(flag ? -4 : 2));
    }

    @EventHandler
    public void snowballThrow(ProjectileLaunchEvent e) {
        if (main.gameState == GameState.OFF || main.gameState == GameState.ROUND_STARTING || !(e.getEntity() instanceof Snowball)) return;
        Snowball s = (Snowball) e.getEntity();
        if (!(s.getShooter() instanceof Player)) return;
        Player shooter = (Player) s.getShooter();
        if (!allSnowballsDead(shooter)) {
            e.setCancelled(true);
            return;
        } else {
            if (main.playersThrownSnowball.containsKey(shooter)) {
                for (Snowball s1 : main.playersThrownSnowball.get(shooter)) {
                    s1.remove();
                }
                main.playersThrownSnowball.remove(shooter);
            }
        }
        if (!main.playersThrownSnowball.containsKey(shooter)) {
            main.playersThrownSnowball.put(shooter, new ArrayList<>(Collections.singletonList(s)));
        } else {
            main.playersThrownSnowball.get(shooter).add(s);
        }
        new SnowballAimbotTask(main, 0L, 1L, s, shooter).start();
    }

    private boolean allSnowballsDead(Player p) {
        if (!main.playersThrownSnowball.containsKey(p)) return true;
        for (Snowball s : main.playersThrownSnowball.get(p)) {
            if (!s.isDead()) return false;
        }
        return true;
    }

    @EventHandler
    public void onHit(ProjectileHitEvent e) {
        if (main.gameState == GameState.OFF || main.gameState == GameState.ROUND_STARTING) return;
        if (e.getEntity() instanceof Snowball) {
            Snowball s = (Snowball) e.getEntity();
            if (s.getShooter() instanceof Player && e.getHitEntity() instanceof Player) {
                Player p1 = (Player)s.getShooter();
                if (main.playersOnSnowballCD.asMap().containsKey(p1)) {
                    e.setCancelled(true);
                    if (main.playersThrownSnowball.containsKey(p1)) {
                        for (Snowball s1 : main.playersThrownSnowball.get(p1)) {
                            s1.remove();
                        }
                    }
                    Utils.alertPlayerOfCooldown(main.playersOnSnowballCD.asMap(), p1);
                    return;
                }
                Player p2 = (Player)e.getHitEntity();
                Location l1 = p1.getLocation();
                p1.teleport(p2.getLocation());
                p2.teleport(l1);
                main.playersOnSnowballCD.put(p1, System.currentTimeMillis() + 8000);
                main.playersThrownSnowball.remove(p1);
            }
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e) {
        if (main.playersJailed.contains(e.getPlayer())) {
            if ((e.getFrom().getBlockX() == e.getTo().getBlockX()) && (e.getFrom().getBlockZ() == e.getTo().getBlockZ()) && (e.getFrom().getBlockY() == e.getTo().getBlockY()))
                return;
            e.setCancelled(true);
            return;
        }
        if (main.gameState == GameState.OFF) return;
        if (main.gameState == GameState.ROUND_STARTING) {
            if ((e.getFrom().getBlockX() == e.getTo().getBlockX()) && (e.getFrom().getBlockZ() == e.getTo().getBlockZ()) && (e.getFrom().getBlockY() == e.getTo().getBlockY()))
                return;
            if (main.playersJoined.containsKey(e.getPlayer())) {
                e.setCancelled(true);
                return;
            }
        }
        Player p = e.getPlayer();
        if (!main.playersDead.contains(p) && p.getLocation().getY() < 85) {
            Bukkit.getPluginManager().callEvent(new PlayerDeadEvent(p));
        }
    }

    @EventHandler
    public void enderPearlLand(PlayerTeleportEvent e) {
        if (e.getCause() != PlayerTeleportEvent.TeleportCause.ENDER_PEARL) return;
        if (main.gameState == GameState.ROUND_STARTING) e.setCancelled(true);
    }

    @EventHandler
    public void playerDead(PlayerDeadEvent e) {
        Player p = e.getPlayer();
        main.playersDead.add(p);
        if (main.playersJoined.size() - main.playersDead.size() <= 1) {
            Bukkit.getPluginManager().callEvent(new RoundEndEvent(p));
            return;
        }
        p.setGameMode(GameMode.SPECTATOR);
        p.sendMessage("You died!");
        p.getInventory().clear();
    }

    @EventHandler
    public void roundEndEvent(RoundEndEvent e) {
        Player winner = null;
        if (main.playersDead.size() == main.playersJoined.size()) {
            winner = e.getPlayer();
        } else {
            for (Player p : main.playersJoined.keySet()) {
                if (!main.playersDead.contains(p)) winner = p;
            }
        }
        assert winner != null;
        Integer oldScore = main.roundsWonByPlayers.get(winner);
        main.roundsWonByPlayers.replace(winner, oldScore + 1);
        main.roundNumber++;
        Pair<Player, Integer> maxWins = new Pair<>(null, -1);
        for (HashMap.Entry<Player, Integer> entry : main.roundsWonByPlayers.entrySet()) {
            if (entry.getValue() > maxWins.second) {
                maxWins.second = entry.getValue();
                maxWins.first = entry.getKey();
            }
        }
        if (maxWins.second == 3) {
            Bukkit.broadcastMessage(ChatColor.GREEN + "" + ChatColor.BOLD + maxWins.first.getDisplayName() + " won the game!");
            StopGameCommand.stopAll(main);
            return;
        }
        Bukkit.broadcastMessage(ChatColor.GREEN + winner.getDisplayName() + " won the round!");
        new StartRoundTask(main, 0L, 20L).start();
    }

    @EventHandler
    public void blockBreakEvent(BlockBreakEvent e) {
        if ( main.gameState != GameState.OFF || main.playersJailed.contains(e.getPlayer())) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerPearl(ProjectileLaunchEvent e) {
        if (main.gameState == GameState.OFF) return;
        if (!(e.getEntity() instanceof EnderPearl) || !(e.getEntity().getShooter() instanceof Player)) return;
        Player shooter = (Player)e.getEntity().getShooter();
        if (main.playersOnPearlCD.asMap().containsKey(shooter)) {
            e.setCancelled(true);
            Utils.alertPlayerOfCooldown(main.playersOnPearlCD.asMap(), shooter);
            return;
        }
        main.playersOnPearlCD.put((Player)e.getEntity().getShooter(), System.currentTimeMillis() + 10000);
    }
}