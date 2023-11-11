package com.churchofcoyote.hero.roguelike.world.ai;

import java.util.List;

import com.churchofcoyote.hero.roguelike.game.Game;
import com.churchofcoyote.hero.roguelike.world.Creature;
import com.churchofcoyote.hero.util.AStar;
import com.churchofcoyote.hero.util.Compass;
import com.churchofcoyote.hero.util.Point;

public class ChaseAndMeleeTactic extends Tactic {

	@Override
	public void execute(Creature c) {
//		long start = System.currentTimeMillis();
		Creature pc = Game.getPlayerCreature();
		List<Point> path = AStar.path(Game.getLevel(), c, c.pos, pc.pos);
		if (path == null || path.size() == 0) {
			c.tookTurn(1000);
			return;
		}
		Point first = path.get(0);
		if (first == null) {
			// wait around
			c.tookTurn(1000);
		} else {
			Compass dir = Compass.to(c.pos, first);
			Game.npcMoveBy(c, dir.getX(), dir.getY());
		}
//		Game.announce("Time: " + (System.currentTimeMillis() - start));
	}

}
