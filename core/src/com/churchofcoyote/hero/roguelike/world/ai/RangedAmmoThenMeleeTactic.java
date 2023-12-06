package com.churchofcoyote.hero.roguelike.world.ai;

import com.churchofcoyote.hero.roguelike.game.Game;
import com.churchofcoyote.hero.roguelike.world.Entity;
import com.churchofcoyote.hero.roguelike.world.EntityTracker;
import com.churchofcoyote.hero.roguelike.world.proc.ProcMover;
import com.churchofcoyote.hero.util.AStar;
import com.churchofcoyote.hero.util.Compass;
import com.churchofcoyote.hero.util.Point;
import org.w3c.dom.ranges.Range;

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
		if (target != null && e.canSee(target)) {
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
			}
			else {
				if (Math.random() < 0.5) {
					Compass direction = Compass.randomDirection();
					if (Game.canMoveTo(e, direction.getX(), direction.getY())) {
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
