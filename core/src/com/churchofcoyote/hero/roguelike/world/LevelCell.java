package com.churchofcoyote.hero.roguelike.world;

public class LevelCell {
	public Terrain terrain;
	public float light; // current light level
	public boolean explored;
	public Object temp; // temporary storage for algorithms in progress
	
	public LevelCell() {
		light = 0;
		explored = false;
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
