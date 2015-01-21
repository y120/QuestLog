package com.github.y120.bukkit.questlog.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

import com.github.y120.bukkit.questlog.QuestLog;
import com.github.y120.bukkit.questlog.quest.KillObjective;
import com.github.y120.bukkit.questlog.quest.Objective;

public class EntityDeathListener implements Listener {
	@EventHandler
	public void onEntityDeath(EntityDeathEvent ede) {
		if (ede.getEntity().getKiller() == null)
			return;
		Player p = ede.getEntity().getKiller();
		Objective o = QuestLog.getCurrentObjective(p);
		if (!(o instanceof KillObjective))
			return;
		KillObjective ko = (KillObjective) o;
		if (ede.getEntity().getType() != ko.e)
			return;
		
		QuestLog.addProgress(p, 1);
		QuestLog.checkCurrentObjective(p);
	}
}
