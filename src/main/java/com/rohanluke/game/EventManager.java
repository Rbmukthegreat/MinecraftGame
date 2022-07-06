package com.rohanluke.game;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerFishEvent;

public class EventManager implements Listener {
    @EventHandler
    public void onPlayerTakeDamage(EntityDamageEvent e) {
        if (e.getEntity() instanceof Player)
            e.setCancelled(true);
    }

    @EventHandler
    public void onPlayerFoodLevelChange(FoodLevelChangeEvent e) {
        if (e.getEntity() instanceof Player)
            e.setCancelled(true);
    }

    @EventHandler
    public void onPlayerFish(PlayerFishEvent e) {
        e.getPlayer().sendMessage("You just used a fishing hook!");
    }
}
