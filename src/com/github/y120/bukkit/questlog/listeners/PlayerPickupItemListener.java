package com.github.y120.bukkit.questlog.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;

import com.github.y120.bukkit.questlog.QuestLog;
import com.github.y120.bukkit.questlog.quest.CollectObjective;
import com.github.y120.bukkit.questlog.quest.Objective;

// QuestLogs should only be on the ground upon drop from a player's death
public class PlayerPickupItemListener implements Listener {
	@EventHandler
	public void onPlayerPickupItem(PlayerPickupItemEvent ppie) {
		if (ppie.isCancelled())
			return;
		if (QuestLog.is(ppie.getItem().getItemStack())) {
			ppie.setCancelled(true);
			return;
		}
		Player p = ppie.getPlayer();
		Objective o = QuestLog.getCurrentObjective(p);
		if (!(o instanceof CollectObjective))
			return;
		CollectObjective co = (CollectObjective) o;
		if (p.getInventory().containsAtLeast(new ItemStack(co.m), co.n))
			QuestLog.completeCurrentObjective(p);
	}
}
