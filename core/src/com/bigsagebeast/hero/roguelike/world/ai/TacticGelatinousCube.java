package com.bigsagebeast.hero.roguelike.world.ai;

import com.bigsagebeast.hero.roguelike.game.Game;
import com.bigsagebeast.hero.roguelike.world.Entity;
import com.bigsagebeast.hero.roguelike.world.EntityTracker;
import com.bigsagebeast.hero.roguelike.world.proc.ProcMover;
import com.bigsagebeast.hero.roguelike.world.proc.item.ProcCorpse;
import com.bigsagebeast.hero.roguelike.world.proc.item.ProcFood;
import com.bigsagebeast.hero.util.AStar;
import com.bigsagebeast.hero.util.Compass;
import com.bigsagebeast.hero.util.Point;
import com.bigsagebeast.hero.util.Util;

import java.util.List;

public class TacticGelatinousCube extends Tactic {
	public boolean execute(Entity e, ProcMover pm) {
		// TODO: This belongs in the phenotype...
		e.visionRange = 30;
		Entity closestCorpse = null;
		for (Entity search : Game.getLevel().getEntities()) {
			if ((search.containsProc(ProcCorpse.class) || search.containsProc(ProcFood.class)) && e.canSee(search)) {
				if (closestCorpse == null || e.pos.distance(search.pos) < e.pos.distance(closestCorpse.pos)) {
					closestCorpse = search;
				}
			}
		}
		if (closestCorpse != null) {
			List<Point> path = AStar.path(Game.getLevel(), e, e.pos, closestCorpse.pos);
			if (!path.isEmpty() && path.get(0) != null) {
				Compass dir = Compass.to(e.pos, path.get(0));
				Game.npcMoveBy(e, pm, dir);
				return true;
			} else {
				return false;
			}
		}

		if (pm.targetEntityId != EntityTracker.NONE) {
			Entity target = EntityTracker.get(pm.targetEntityId);
			List<Point> path = AStar.path(Game.getLevel(), e, e.pos, target.pos);
			if (!path.isEmpty() && path.size() < 3) {
				return chaseSeenPlayer(e, pm);
			}
		}
		if (wanderPoint == null) {
			wanderPoint = Game.getLevel().findOpenTileWithinRange(e.pos, 10, 30);
		}
		List<Point> path = AStar.path(Game.getLevel(), e, e.pos, wanderPoint);
		if (path.size() < 2 || path.get(0) == null) {
			wanderPoint = null;
			return guard(e, pm);
		}
		Compass dir = Compass.to(e.pos, path.get(0));
		Game.npcMoveBy(e, pm, dir);
		return true;
	}
}
