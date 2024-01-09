package com.bigsagebeast.hero.roguelike.world.ai;

import com.bigsagebeast.hero.roguelike.game.Game;
import com.bigsagebeast.hero.roguelike.world.Entity;
import com.bigsagebeast.hero.roguelike.world.EntityTracker;
import com.bigsagebeast.hero.roguelike.world.proc.ProcMover;
import com.bigsagebeast.hero.util.AStar;
import com.bigsagebeast.hero.util.Compass;
import com.bigsagebeast.hero.util.Point;
import com.bigsagebeast.hero.util.Util;

import java.util.List;

public abstract class Tactic {
	public abstract boolean execute(Entity e, ProcMover pm);

	public boolean canWander = false;
	public boolean isWandering = true;
	public int waitTimer = 0;
	public int minWaitTimer = 10;
	public int maxWaitTimer = 30;

	Point wanderPoint = null;
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

	boolean idle(Entity e, ProcMover pm) {
		if (canWander) {
			return wander(e, pm);
		} else {
			return guard(e, pm);
		}
	}

	boolean wander(Entity e, ProcMover pm) {
		if (isWandering) {
			if (--waitTimer <= 0) {
				wanderPoint = null;
				waitTimer = Util.randomBetween(minWaitTimer, maxWaitTimer);
				isWandering = false;
				return guard(e, pm);
			}
			if (wanderPoint == null) {
				wanderPoint = Game.getLevel().findOpenTileWithinRange(e.pos, 10, 30);
				if (wanderPoint == null) {
					// couldn't find a valid wander point
					waitTimer = Util.randomBetween(minWaitTimer, maxWaitTimer);
					isWandering = false;
					return guard(e, pm);
				}
			}
			List<Point> path = AStar.path(Game.getLevel(), e, e.pos, wanderPoint);
			if (path.size() <= 1) {
				wanderPoint = null;
				waitTimer = Util.randomBetween(minWaitTimer, maxWaitTimer);
				isWandering = false;
				return guard(e, pm);
			}
			Point first = path.get(0);
			if (first == null) {
				return guard(e, pm);
			} else {
				Compass dir = Compass.to(e.pos, first);
				Game.npcMoveBy(e, pm, dir);
				return true;
			}
		} else {
			if (--waitTimer <= 0) {
				waitTimer = Util.randomBetween(minWaitTimer, maxWaitTimer);
				isWandering = true;
				return guard(e, pm);
			}
			return guard(e, pm);
		}
	}

	boolean guard(Entity e, ProcMover pm) {
		if (Math.random() < 0.5) {
			Compass direction = Compass.randomDirection();
			// semi-hack to prevent pathing through doors while on guard duty
			if (Game.canMoveBy(e, direction) && !Game.getLevel().obstructiveBesidesMovers(direction.from(e.pos))) {
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
