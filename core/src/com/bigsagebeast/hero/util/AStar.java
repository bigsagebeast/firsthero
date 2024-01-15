package com.bigsagebeast.hero.util;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

import com.bigsagebeast.hero.HeroGame;
import com.bigsagebeast.hero.roguelike.game.Game;
import com.bigsagebeast.hero.roguelike.world.Entity;
import com.bigsagebeast.hero.roguelike.world.dungeon.Level;

public class AStar {

	private static PriorityQueue<AStarData> queue = new PriorityQueue<AStarData>();
	private static AStar instance = new AStar();

	public static List<Point> path(Level level, Entity e, Point origin, Point destination) {
		long milliStart = System.currentTimeMillis();
		level.clearTemp();
		queue.clear();
		//int maxDistance = (int)e.visionRange;
		int maxDistance = e.incorporeal ? 999 : Game.getLevel().ambientLight; // TODO: haaack

		check(level, instance.new AStarData(origin, 0, null));
		List<Point> retval = new ArrayList<Point>();

		while (!queue.isEmpty()) {
			AStarData next = queue.poll();
			if (next.location.equals(destination)) {
				AStarData last = next;
				while (last.from != null) {
					retval.add(0, last.location);
					last = (AStarData) level.cell(last.from).temp;
				}
				break;
			}

			for (Compass dir : Compass.points()) {
				Point newloc = dir.from(next.location);
				float distance = newloc.distance(destination);
				if (distance > maxDistance) {
					continue;
				}
				
				if (level.contains(newloc)) {
					float moveCost = dir.isDiagonal() ? 1.0001f : 1.0f;
					if ((!Game.isBlockedByTerrain(e, newloc.x, newloc.y) && !Game.isBlockedByNonManipulable(e, newloc.x, newloc.y)) || destination.equals(newloc)) {
						if (!destination.equals(newloc) && Game.isBlockedByEntity(e, newloc.x, newloc.y)) {
							if (e.incorporeal) {
								// incorporeals step around doors and monsters
								continue;
							}
							moveCost += 10;
						}
						check(level, instance.new AStarData(newloc, e.incorporeal ? distance : (next.cost + moveCost), next.location));
					}
				}
			}
		}
		HeroGame.addTimer("astar", System.currentTimeMillis() - milliStart);
		return retval;
	}

	private static void check(Level level, AStarData next) {
		AStarData existing = (AStarData)level.cell(next.location).temp;
		if (existing == null) {
			queue.add(next);
			level.cell(next.location).temp = next;
			return;
		}
		if (existing.cost <= next.cost) {
			return;
		}
		queue.remove(existing);
		queue.add(next);
		level.cell(next.location).temp = next;
	}

	public class AStarData implements Comparable<AStarData> {
		Point location;
		float cost;
		Point from;
		float estimate;

		public AStarData(Point location, float cost, Point from) {
			this.location = location;
			this.cost = cost;
			this.from = from;
		}

		@Override
		public int compareTo(AStarData other) {
			if (cost < other.cost) {
				return -1;
			}
			if (cost == other.cost) {
				return 0;
			}
			return 1;
		}

		@Override
		public boolean equals(Object arg0) {
			if (!(arg0 instanceof AStarData)) {
				throw new ClassCastException();
			}
			AStarData other = (AStarData) arg0;
			return (this.location.equals(other.location));
		}

		@Override
		public int hashCode() {
			return location.hashCode();
		}
		
		@Override
		public String toString() {
			return "(" + location + ":" + cost + ")";
		}
	}
}
