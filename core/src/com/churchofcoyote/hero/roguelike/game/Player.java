package com.churchofcoyote.hero.roguelike.game;

import com.churchofcoyote.hero.roguelike.world.Creature;

public class Player {
	public Creature creature;
	
	public boolean isCreature(Creature c) {
		return creature == c;
	}
}
