package com.churchofcoyote.hero.roguelike.world.ai;

import com.churchofcoyote.hero.roguelike.world.Entity;
import com.churchofcoyote.hero.roguelike.world.proc.ProcMover;

public abstract class Tactic {
	public abstract void execute(Entity e, ProcMover pm);
}
