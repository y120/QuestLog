package com.github.y120.bukkit.questlog.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import com.github.y120.bukkit.Common.CC;
import com.github.y120.bukkit.questlog.Config;
import com.github.y120.bukkit.questlog.PlayerProgress;
import com.github.y120.bukkit.questlog.QuestLog;

// if they don't have
public class PlayerJoinListener implements Listener {
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent pje) {
        Player p = pje.getPlayer();
        if (!QuestLog.isInInventory(p.getInventory())) {
            p.getInventory().addItem(QuestLog.make());
            p.sendMessage(CC.Y + "You have been given a brand new " + Config.QUEST_LOG_DISPLAY_NAME + CC.Y + "!");
        }
        if (!PlayerProgress.archives.containsKey(p.getName()))
            PlayerProgress.loadArchives(p.getName());
    }
}
