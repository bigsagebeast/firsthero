package com.bigsagebeast.hero.roguelike.world.ai;

import com.bigsagebeast.hero.roguelike.world.Entity;
import com.bigsagebeast.hero.roguelike.world.proc.ProcMover;
import com.bigsagebeast.hero.util.AStar;
import com.bigsagebeast.hero.util.Compass;
import com.bigsagebeast.hero.util.Point;
import com.bigsagebeast.hero.roguelike.game.Game;
import com.bigsagebeast.hero.roguelike.world.EntityTracker;

import java.util.List;

public class RangedAmmoThenMeleeTactic extends Tactic {

	Point lastSeen = null;
	int ammo;

	public RangedAmmoThenMeleeTactic(int ammo) {
		this.ammo = ammo;
	}

	@Override
	public boolean execute(Entity e, ProcMover pm) {
		Entity target = null;
		if (ammo > 0 && pm.targetEntityId != EntityTracker.NONE) {
			target = EntityTracker.get(pm.targetEntityId);
		}
		// TODO range for equipped weapons, too
		float distance = -1;
		if (target != null) {
			distance = e.pos.distance(target.pos);
		}
		if (target != null && e.canSee(target) && distance < e.naturalRangedWeaponRange) {
			// maybe stand there instead of shooting
			if (Math.random() < 0.5) {
				pm.setDelay(e, e.getMoveCost());
				return true;
			}
			ammo--;
			Game.npcShoot(e, target.pos);
			pm.setDelay(e, e.getMoveCost());
			return true;
		}
		return executeChaseAndMelee(e, pm);
	}

	public boolean executeChaseAndMelee(Entity e, ProcMover pm) {
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
			} else {
				if (Math.random() < 0.5) {
					Compass direction = Compass.randomDirection();
					if (Game.canMoveBy(e, direction)) {
						Game.npcMoveBy(e, pm, direction.getX(), direction.getY());
					}
				} else {
					pm.setDelay(e, Game.ONE_TURN);
				}
			}
		} else {
			Entity target = EntityTracker.get(pm.targetEntityId);
			lastSeen = target.pos;
			List<Point> path = AStar.path(Game.getLevel(), e, e.pos, target.pos);
			if (path == null || path.size() == 0 || path.get(0) == null) {
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
			Point first = path.get(0);
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
		return false;
	}
}
