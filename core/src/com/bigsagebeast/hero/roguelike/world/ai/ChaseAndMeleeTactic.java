package com.bigsagebeast.hero.roguelike.world.ai;

import java.util.List;

import com.bigsagebeast.hero.roguelike.world.Entity;
import com.bigsagebeast.hero.roguelike.world.proc.ProcMover;
import com.bigsagebeast.hero.util.AStar;
import com.bigsagebeast.hero.util.Compass;
import com.bigsagebeast.hero.util.Point;
import com.bigsagebeast.hero.roguelike.game.Game;
import com.bigsagebeast.hero.roguelike.world.EntityTracker;

public class ChaseAndMeleeTactic extends Tactic {
	@Override
	public boolean execute(Entity e, ProcMover pm) {
		if (pm.targetEntityId == EntityTracker.NONE) {
			if (lastSeen != null) {
				if (!huntLastSeen(e, pm)) {
					return wander(e, pm);
				}
			}
			else {
				return wander(e, pm);
			}
		} else {
			return chaseSeenPlayer(e, pm);
		}
		return false;
	}
}
