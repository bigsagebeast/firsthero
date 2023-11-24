package com.churchofcoyote.hero.roguelike.world.ai;

import java.util.List;

import com.churchofcoyote.hero.roguelike.game.Game;
import com.churchofcoyote.hero.roguelike.world.Entity;
import com.churchofcoyote.hero.roguelike.world.EntityTracker;
import com.churchofcoyote.hero.roguelike.world.proc.ProcMover;
import com.churchofcoyote.hero.util.AStar;
import com.churchofcoyote.hero.util.Compass;
import com.churchofcoyote.hero.util.Point;

public class ChaseAndMeleeTactic extends Tactic {

	Point lastSeen = null;

	@Override
	public void execute(Entity e, ProcMover pm) {
//		long start = System.currentTimeMillis();

		if (pm.targetEntityId == EntityTracker.NONE) {
			if (lastSeen != null) {
				List<Point> path = AStar.path(Game.getLevel(), e, e.pos, lastSeen);
				if (path == null || path.size() == 0) {
					lastSeen = null;
					pm.setDelay(1000);
					return;
				}
				Point first = path.get(0);
				if (first == null) {
					lastSeen = null;
					// wait around
					pm.setDelay(1000);
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
				pm.setDelay(1000);
				return;
			}
			Point first = path.get(0);
			if (first == null) {
				// wait around
				pm.setDelay(1000);
			} else {
				Compass dir = Compass.to(e.pos, first);
				if (target.pos.equals(first)) {
					Game.npcAttack(e, pm, dir.getX(), dir.getY());
				} else {
					if (Game.random.nextInt(8) == 0) {
						dir = Compass.neighbors(dir).get(Game.random.nextInt(2));
					}
					Game.npcMoveBy(e, pm, dir.getX(), dir.getY());
				}
			}
		}
	}

}
