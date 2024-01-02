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

	Point lastSeen = null;

	@Override
	public boolean execute(Entity e, ProcMover pm) {
		if (pm.targetEntityId == EntityTracker.NONE) {
			if (lastSeen != null) {
				List<Point> path = AStar.path(Game.getLevel(), e, e.pos, lastSeen);
				if (path == null || path.size() == 0) {
					lastSeen = null;
					return false;
				}
				Point first = path.get(0);
				if (first == null) {
					lastSeen = null;
					// wait around
					return false;
				} else {
					Compass dir = Compass.to(e.pos, first);
					if (Game.random.nextInt(8) == 0) {
						dir = Compass.neighbors(dir).get(Game.random.nextInt(2));
					}
					Game.npcMoveBy(e, pm, dir.getX(), dir.getY());
				}
			}
			else {
				if (Math.random() < 0.5) {
					Compass direction = Compass.randomDirection();
					if (Game.canMoveTo(e, direction.getX(), direction.getY())) {
						Game.npcMoveBy(e, pm, direction.getX(), direction.getY());
					}
				}
			}
		} else {
			Entity target = EntityTracker.get(pm.targetEntityId);
			lastSeen = target.pos;
			List<Point> path = AStar.path(Game.getLevel(), e, e.pos, target.pos);
			if (path == null || path.size() == 0) {
				return false;
			}
			Point first = path.get(0);
			if (first == null) {
				// wait around
				return false;
			} else {
				Compass dir = Compass.to(e.pos, first);
				if (target.pos.equals(first)) {
					Game.npcAttack(e, pm, dir.getX(), dir.getY());
				} else {
					if (Game.random.nextInt(8) == 0) {
						dir = Compass.neighbors(dir).get(Game.random.nextInt(2));
					}
					Game.npcMoveBy(e, pm, dir.getX(), dir.getY());
					return true;
				}
			}
		}
		return false;
	}
}
