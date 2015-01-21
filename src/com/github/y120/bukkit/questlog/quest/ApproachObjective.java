package com.github.y120.bukkit.questlog.quest;

import org.bukkit.Location;

public class ApproachObjective extends Objective {
	public int x, y, z; // location
	public int r; // radius squared

	public ApproachObjective(int x, int y, int z, int r, String s) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.r = r * r;
		this.flavourText = s;
	}

	public boolean check(Location l) {
		Location t = new Location(l.getWorld(), this.x, this.y, this.z);
		if (this.y == -1)
			t.setY(l.getY());
		return this.r >= t.distanceSquared(l);
	}
}
