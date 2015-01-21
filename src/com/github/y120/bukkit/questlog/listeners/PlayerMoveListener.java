package com.github.y120.bukkit.questlog.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import com.github.y120.bukkit.questlog.QuestLog;
import com.github.y120.bukkit.questlog.quest.ApproachObjective;
import com.github.y120.bukkit.questlog.quest.Objective;

public class PlayerMoveListener implements Listener {
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerMove(PlayerMoveEvent pme) {
		if (pme.isCancelled())
			return;
		Player p = pme.getPlayer();
		Objective o = QuestLog.getCurrentObjective(p);
		if (!(o instanceof ApproachObjective))
			return;
		ApproachObjective ao = (ApproachObjective) o;
		if (ao.check(pme.getTo()))
			QuestLog.completeCurrentObjective(p);
	}
}
