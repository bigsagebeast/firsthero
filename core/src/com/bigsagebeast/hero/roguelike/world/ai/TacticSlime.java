package com.bigsagebeast.hero.roguelike.world.ai;

import com.bigsagebeast.hero.roguelike.game.Game;
import com.bigsagebeast.hero.roguelike.world.Bestiary;
import com.bigsagebeast.hero.roguelike.world.Entity;
import com.bigsagebeast.hero.roguelike.world.EntityTracker;
import com.bigsagebeast.hero.roguelike.world.proc.ProcMover;
import com.bigsagebeast.hero.roguelike.world.proc.item.ProcCorpse;
import com.bigsagebeast.hero.roguelike.world.proc.item.ProcFood;
import com.bigsagebeast.hero.util.AStar;
import com.bigsagebeast.hero.util.Compass;
import com.bigsagebeast.hero.util.Point;
import com.bigsagebeast.hero.util.Util;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class TacticSlime extends Tactic {
	int divideCounter = 0;
	int minDivideCounter = 20;
	int maxDivideCounter = 30;
	public boolean execute(Entity e, ProcMover pm) {
		int countOnLevel = (int) Game.getLevel().getEntities().stream().filter(c -> c.phenotypeName != null && c.phenotypeName.equals(e.phenotypeName)).count();
		if (countOnLevel < e.getPhenotype().maxSplitting && e.hitPoints > 1 && --divideCounter <= 0) {
			split(e);
			return false;
		}

		if (pm.targetEntityId != EntityTracker.NONE) {
			Entity target = EntityTracker.get(pm.targetEntityId);
			List<Point> path = AStar.path(Game.getLevel(), e, e.pos, target.pos);
			if (!path.isEmpty() && path.size() < 3) {
				return chaseSeenPlayer(e, pm);
			}
		}

		if (Math.random() < 0.25) {
			Compass direction = Compass.randomDirection();
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


	public boolean split(Entity e) {
		divideCounter = Util.randomBetween(minDivideCounter, maxDivideCounter);
		List<Point> spawnPoints = Game.getLevel().surroundingTiles(e.pos)
				.stream().filter(p -> Game.getLevel().isSpawnable(p)).collect(Collectors.toList());
		if (spawnPoints.isEmpty()) {
			return false;
		}
		Collections.shuffle(spawnPoints);
		Entity ent = Bestiary.create(e.phenotypeName);
		int newHp = (e.hitPoints + 1) / 2;
		e.hitPoints = newHp;
		ent.hitPoints = newHp;
		((TacticSlime)ent.getTactic()).divideCounter = Util.randomBetween(minDivideCounter, maxDivideCounter);

		Game.getLevel().addEntityWithStacking(ent, spawnPoints.get(0));
		return true;
	}
}
