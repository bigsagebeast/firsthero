package com.bigsagebeast.hero.roguelike.world.ai;

import com.bigsagebeast.hero.roguelike.world.Entity;
import com.bigsagebeast.hero.roguelike.world.proc.ProcMover;
import com.bigsagebeast.hero.roguelike.world.EntityTracker;

public class TacticChaseAndMelee extends Tactic {
	@Override
	public boolean execute(Entity e, ProcMover pm) {
		if (pm.targetEntityId == EntityTracker.NONE) {
			if (lastSeen != null) {
				if (!huntLastSeen(e, pm)) {
					return idle(e, pm);
				}
				return true;
			}
			else {
				return idle(e, pm);
			}
		} else {
			return chaseSeenPlayer(e, pm);
		}
	}
}
