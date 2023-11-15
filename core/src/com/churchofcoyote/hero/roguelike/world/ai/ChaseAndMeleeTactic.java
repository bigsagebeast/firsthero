package com.churchofcoyote.hero.roguelike.world.ai;

import java.util.List;

import com.churchofcoyote.hero.roguelike.game.Game;
import com.churchofcoyote.hero.roguelike.world.Entity;
import com.churchofcoyote.hero.roguelike.world.proc.ProcMover;
import com.churchofcoyote.hero.util.AStar;
import com.churchofcoyote.hero.util.Compass;
import com.churchofcoyote.hero.util.Point;

public class ChaseAndMeleeTactic extends Tactic {


	@Override
	public void execute(Entity e, ProcMover pm) {
//		long start = System.currentTimeMillis();
		Entity pc = Game.getPlayerEntity();
		List<Point> path = AStar.path(Game.getLevel(), e, e.pos, pc.pos);
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
			Game.npcMoveBy(e, pm, dir.getX(), dir.getY());
		}
//		Game.announce("Time: " + (System.currentTimeMillis() - start));
	}

}
