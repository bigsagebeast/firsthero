package com.churchofcoyote.hero.roguelike.world.ai;

import com.churchofcoyote.hero.roguelike.world.Creature;

public abstract class Tactic {
	public abstract void execute(Creature c);
}
