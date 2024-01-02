package com.bigsagebeast.hero.roguelike.world.ai;

import com.bigsagebeast.hero.roguelike.game.Game;
import com.bigsagebeast.hero.roguelike.world.Entity;
import com.bigsagebeast.hero.roguelike.world.EntityTracker;
import com.bigsagebeast.hero.roguelike.world.proc.ProcMover;
import com.bigsagebeast.hero.util.AStar;
import com.bigsagebeast.hero.util.Compass;
import com.bigsagebeast.hero.util.Point;

import java.util.List;

public class TacticBat extends Tactic {

	@Override
	public boolean execute(Entity e, ProcMover pm) {
		if (pm.targetEntityId == EntityTracker.NONE) {
			return moveRandomly(e, pm);
		} else {
			Entity target = EntityTracker.get(pm.targetEntityId);
			if (Game.random.nextInt(100) < 66) {
				return moveRandomly(e, pm);
			}
			List<Point> path = AStar.path(Game.getLevel(), e, e.pos, target.pos);
			if (path == null || path.size() == 0 || path.get(0) == null) {
				return moveRandomly(e, pm);
			}
			Point first = path.get(0);
			Compass dir = Compass.to(e.pos, first);
			if (target.pos.equals(first)) {
				Game.npcAttack(e, pm, dir.getX(), dir.getY());
				return false;
			} else {
				if (Game.random.nextInt(8) == 0) {
					dir = Compass.neighbors(dir).get(Game.random.nextInt(2));
				}
				Game.npcMoveBy(e, pm, dir.getX(), dir.getY());
				return true;
			}
		}
	}

	public boolean moveRandomly(Entity e, ProcMover pm) {
		Compass direction = Compass.randomDirection();
		for (int i=0; i<3 && Game.canMoveBy(e, direction); i++) {
			// Make a few attempts to move in a good direction
			direction = Compass.randomDirection();
		}
		if (Game.canMoveBy(e, direction)) {
			Game.npcMoveBy(e, pm, direction.getX(), direction.getY());
			return true;
		}
		return false;
	}
}
