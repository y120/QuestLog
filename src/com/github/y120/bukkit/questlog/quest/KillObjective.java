package com.github.y120.bukkit.questlog.quest;

import org.bukkit.entity.EntityType;

public class KillObjective extends Objective {
	public EntityType e;	// entity type ID
	public int n;			// amount to kill
	
	public KillObjective(EntityType e, int n, String s) {
		this.e = e;
		this.n = n;
		this.flavourText = s;
	}
}
