package com.github.y120.bukkit.questlog.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import com.github.y120.bukkit.questlog.QuestLog;

public class PlayerInteractListener implements Listener {
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent pie) {
        Player p = pie.getPlayer();
        
        if (pie.getAction() != Action.LEFT_CLICK_AIR && pie.getAction() != Action.LEFT_CLICK_BLOCK || !pie.hasItem())
            return;
        
        if (QuestLog.isArchive(pie.getItem())) {
            p.getInventory().remove(pie.getItem());
            return;
        }
        
        if (QuestLog.hasArchives(p) && QuestLog.is(pie.getItem())) {
            p.openInventory(QuestLog.getArchives(p));
            pie.setCancelled(true);
        }
    }
}
