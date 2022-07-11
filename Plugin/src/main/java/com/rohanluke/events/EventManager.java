package com.rohanluke.events;

import com.rohanluke.commands.StopGameCommand;
import com.rohanluke.game.GameState;
import com.rohanluke.game.Main;
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
        if (main.gameState == GameState.OFF || main.gameState == GameState.ROUND_STARTING) return;
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
        if (main.roundNumber == 2) {
            assert main.roundsWonByPlayers.peek() != null;
            Player winner = main.roundsWonByPlayers.peek().first;
            Bukkit.broadcastMessage(ChatColor.GREEN + "" + ChatColor.BOLD + winner.getDisplayName() + " won the game!");
            StopGameCommand.stopAll(main);
            return;
        }
        Player winner = e.getWinner();
        Integer oldScore = main.roundsWonByPlayers.get(winner);
        main.roundsWonByPlayers.remove(new Pair<>(winner, oldScore));
        main.roundsWonByPlayers.add(new Pair<>(winner, oldScore+1));
        main.roundNumber++;
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
        if (main.playersOnPearlCD.containsKey(shooter)) {
            e.setCancelled(true);
            Utils.alertPlayerOfCooldown(main.playersOnPearlCD, shooter);
            return;
        }
        main.playersOnPearlCD.put((Player)e.getEntity().getShooter(), 16.0);
    }
}