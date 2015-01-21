package com.github.y120.bukkit.questlog.listeners;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;

public class PlayerRespawnListener implements Listener {
    public static Map<String, ItemStack> deadLogs = new HashMap<String, ItemStack>();
    
    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent pre) {
        Player p = pre.getPlayer();
        if (PlayerRespawnListener.deadLogs.containsKey(p.getName())) {
            p.getInventory().addItem(PlayerRespawnListener.deadLogs.get(p.getName()));
            PlayerRespawnListener.deadLogs.remove(p.getName());
        }
    }
}
