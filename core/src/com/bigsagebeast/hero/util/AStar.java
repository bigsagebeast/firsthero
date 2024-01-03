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
	private static final float MAX_DISTANCE_FROM_TARGET = 15f;

	public static List<Point> path(Level level, Entity e, Point origin, Point destination) {
		Long milliStart = System.currentTimeMillis();
		level.clearTemp();
		queue.clear();

		check(level, instance.new AStarData(origin, 0, null, origin.distance(destination)));
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
				if (distance > MAX_DISTANCE_FROM_TARGET) {
					continue;
				}
				
				if (level.contains(newloc)) {
					float moveCost = dir.isDiagonal() ? 1.0001f : 1.0f;
					if (!Game.isBlockedByTerrain(e, newloc.x, newloc.y) || destination.equals(newloc)) {
						if (Game.isBlockedByEntity(e, newloc.x, newloc.y)) {
							moveCost += 10;
						}
						check(level, instance.new AStarData(newloc, next.cost + moveCost, next.location, distance));
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

		public AStarData(Point location, float cost, Point from, float estimate) {
			this.location = location;
			this.cost = cost;
			this.from = from;
			this.estimate = estimate;
		}

		@Override
		public int compareTo(AStarData other) {
			if (estimate < other.estimate) {
				return -1;
			}
			if (estimate == other.estimate) {
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
