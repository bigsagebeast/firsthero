package com.bigsagebeast.hero.roguelike.world.ai;

import com.bigsagebeast.hero.roguelike.game.Game;
import com.bigsagebeast.hero.roguelike.world.Entity;
import com.bigsagebeast.hero.roguelike.world.EntityTracker;
import com.bigsagebeast.hero.roguelike.world.proc.ProcMover;
import com.bigsagebeast.hero.util.AStar;
import com.bigsagebeast.hero.util.Compass;
import com.bigsagebeast.hero.util.Point;

import java.util.List;

public abstract class Tactic {
	public abstract boolean execute(Entity e, ProcMover pm);

	Point lastSeen = null;

	boolean chaseSeenPlayer(Entity e, ProcMover pm) {
		Entity target = EntityTracker.get(pm.targetEntityId);
		lastSeen = target.pos;
		List<Point> path = AStar.path(Game.getLevel(), e, e.pos, target.pos);
		if (path == null || path.size() == 0 || path.get(0) == null) {
			return false;
		}
		Point first = path.get(0);
		Compass dir = Compass.to(e.pos, first);
		if (target.pos.equals(first)) {
			Game.npcAttack(e, pm, dir.getX(), dir.getY());
		} else {
			if (Game.random.nextInt(8) == 0) {
				dir = Compass.neighbors(dir).get(Game.random.nextInt(2));
			}
			// TODO what happens if this moves into a wall?
			Game.npcMoveBy(e, pm, dir.getX(), dir.getY());
			return true;
		}
		return false;
	}

	boolean wander(Entity e, ProcMover pm) {
		if (Math.random() < 0.5) {
			Compass direction = Compass.randomDirection();
			if (Game.canMoveBy(e, direction)) {
				Game.npcMoveBy(e, pm, direction.getX(), direction.getY());
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	boolean huntLastSeen(Entity e, ProcMover pm) {
		List<Point> path = AStar.path(Game.getLevel(), e, e.pos, lastSeen);
		if (path == null || path.isEmpty()) {
			lastSeen = null;
			return false;
		}
		Point first = path.get(0);
		if (first == null) {
			lastSeen = null;
			return false;
		} else {
			Compass dir = Compass.to(e.pos, first);
			if (Game.random.nextInt(8) == 0) {
				// TODO: this might run into a wall
				dir = Compass.neighbors(dir).get(Game.random.nextInt(2));
			}
			Game.npcMoveBy(e, pm, dir.getX(), dir.getY());
			return true;
		}
	}
}
