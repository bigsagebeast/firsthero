package com.bigsagebeast.hero.roguelike.world.ai;

import com.bigsagebeast.hero.roguelike.world.Entity;
import com.bigsagebeast.hero.roguelike.world.proc.ProcMover;

public abstract class Tactic {
	public abstract boolean execute(Entity e, ProcMover pm);
}
