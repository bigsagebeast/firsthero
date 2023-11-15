package com.churchofcoyote.hero.roguelike.game;

import com.churchofcoyote.hero.roguelike.world.Entity;

public class Player {
	public Entity entity;
	
	public boolean isEntity(Entity e) {
		return entity == e;
	}
}
