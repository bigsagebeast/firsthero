package com.bigsagebeast.hero.roguelike.world.dungeon.generation;

import com.bigsagebeast.hero.enums.Ambulation;
import com.bigsagebeast.hero.roguelike.world.Bestiary;
import com.bigsagebeast.hero.roguelike.world.Entity;
import com.bigsagebeast.hero.util.Compass;
import com.bigsagebeast.hero.util.Point;
import com.bigsagebeast.hero.roguelike.world.Terrain;
import com.bigsagebeast.hero.roguelike.world.dungeon.Level;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

public class AStarLevel {

	private static PriorityQueue<AStarData> queue = new PriorityQueue<AStarData>();
	private static AStarLevel instance = new AStarLevel();

	public static List<Point> path(Level level, Point origin, Point destination, float wallCost) {
		Terrain uncarveable = Terrain.get("uncarveable");

		queue.clear();
		for (int i=0; i<level.getWidth(); i++) {
			for (int j=0; j<level.getHeight(); j++) {
				level.cell(i, j).astar = null;
			}
		}

		check(level, instance.new AStarData(origin, 0, null));
		List<Point> retval = new ArrayList<Point>();

		while (!queue.isEmpty()) {
			AStarData next = queue.poll();
			if (next.location.equals(destination)) {
				AStarData last = next;
				while (last.from != null) {
					retval.add(0, last.location);
					last = (AStarData) level.cell(last.from.x, last.from.y).astar;
				}
				break;
			}

			for (Compass dir : Compass.orthogonal) {
				Point newloc = dir.from(next.location);
				float moveCost = 0f;
				if (level.contains(newloc) && (level.cell(newloc).terrain.isPassable())) {
					moveCost = 1.0f;
				}
				else if (level.contains(newloc) && Compass.isOrthogonal(dir)) {
					if (level.cell(newloc).terrain == uncarveable) {
						moveCost = 1000000.0f;
					} else if (!level.cell(newloc).terrain.isPassable()) {
						moveCost = 1000.0f;
					}
				}
				if (moveCost > 0f) {
					check(level, instance.new AStarData(newloc, next.cost + moveCost, next.location));
				}
			}
		}
		return retval;
	}

	private static void check(Level level, AStarData next) {
		AStarData existing = (AStarData)level.cell(next.location).astar;
		if (existing == null) {
			queue.add(next);
			level.cell(next.location).astar = next;
			return;
		}
		if (existing.cost <= next.cost) {
			return;
		}
		queue.remove(existing);
		queue.add(next);
		level.cell(next.location).astar = next;
	}

	public class AStarData implements Comparable<AStarData> {
		Point location;
		float cost;
		Point from;

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
