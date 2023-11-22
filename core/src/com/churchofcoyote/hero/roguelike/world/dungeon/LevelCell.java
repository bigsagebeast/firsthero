package com.churchofcoyote.hero.roguelike.world.dungeon;

import com.churchofcoyote.hero.roguelike.world.Terrain;

public class LevelCell {
	public Terrain terrain;
	public float light; // current light level
	public boolean explored;
	public Object temp; // temporary storage for algorithms in progress
	public Object astar; // temporary storage for astar calcs
	public Room room;
	
	public LevelCell() {
		light = 0;
		explored = false;
		terrain = null;
	}
	
	public LevelCell(Terrain t) {
		this();
		this.terrain = t;
	}
	
	public static final LevelCell NONE = new LevelCell(Terrain.BLANK);
	
	public boolean visible() {
		return (light > 0);
	}
}
