package com.github.y120.bukkit.questlog.quest;

import org.bukkit.Material;

public class CollectObjective extends Objective {
	public Material m;	// material ID
	public int n;		// amount to kill
	
	public CollectObjective(Material m, int n, String s) {
		this.m = m;
		this.n = n;
		this.flavourText = s;
	}
}
