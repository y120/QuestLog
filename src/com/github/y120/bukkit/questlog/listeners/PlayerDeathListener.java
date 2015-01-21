package com.github.y120.bukkit.questlog.listeners;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;

import com.github.y120.bukkit.questlog.QuestLog;

public class PlayerDeathListener implements Listener {
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent pde) {
        List<ItemStack> a = pde.getDrops();
        ArrayList<ItemStack> rmv = new ArrayList<ItemStack>();
        ItemStack questLog = null;
        for (ItemStack is : a)
            if (QuestLog.isArchive(is))
                rmv.add(is);
            else if (QuestLog.is(is))
                questLog = is;
        a.removeAll(rmv);
        
        PlayerRespawnListener.deadLogs.put(pde.getEntity().getName(), questLog);
        a.remove(questLog);
    }
}
