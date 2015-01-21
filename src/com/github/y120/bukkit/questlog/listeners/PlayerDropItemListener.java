package com.github.y120.bukkit.questlog.listeners;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;

import com.github.y120.bukkit.Common.CC;
import com.github.y120.bukkit.questlog.QuestLog;

// Do not allow the user to drop the quest log
public class PlayerDropItemListener implements Listener {
    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent pdie) {
        if (pdie.isCancelled())
            return;
        if (QuestLog.is(pdie.getItemDrop().getItemStack())) {
            pdie.setCancelled(true);
            pdie.getPlayer().sendMessage(CC.R + "You cannot drop your Quest Log!");
        } else if (QuestLog.isArchive(pdie.getItemDrop().getItemStack()))
            pdie.getItemDrop().setItemStack(new ItemStack(Material.DIRT)); // archived QuestLogs are destroyed upon drop
        // for some reason there's NPE on Material.AIR so workaround w/ 1 dirt (effectively useless)
    }
}
