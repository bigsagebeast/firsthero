package com.churchofcoyote.hero.roguelike.world.dungeon.generation;

import com.churchofcoyote.hero.roguelike.game.Game;
import com.churchofcoyote.hero.roguelike.world.Entity;
import com.churchofcoyote.hero.roguelike.world.Terrain;
import com.churchofcoyote.hero.roguelike.world.dungeon.Level;
import com.churchofcoyote.hero.util.Compass;
import com.churchofcoyote.hero.util.Point;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

public class AStarBrogue {

	private static PriorityQueue<AStarData> queue = new PriorityQueue<AStarData>();
	private static AStarBrogue instance = new AStarBrogue();

	public static List<Point> path(BrogueGrid grid, Point origin, Point destination, boolean pathThroughWalls) {
		Terrain uncarveable = Terrain.get("uncarveable");
		queue.clear();
		for (int i=0; i<grid.width; i++) {
			for (int j=0; j<grid.height; j++) {
				grid.cell[i][j].astar = null;
			}
		}

		check(grid, instance.new AStarData(origin, 0, null));
		List<Point> retval = new ArrayList<Point>();

		while (!queue.isEmpty()) {
			AStarData next = queue.poll();
			if (next.location.equals(destination)) {
				AStarData last = next;
				while (last.from != null) {
					retval.add(0, last.location);
					last = (AStarData) grid.cell[last.from.x][last.from.y].astar;
				}
				break;
			}

			//for (Compass dir : Compass.points) {
			for (Compass dir : Compass.orthogonal) {
				Point newloc = dir.from(next.location);
				float moveCost = 0f;
				if (grid.contains(newloc)) {
					if (grid.cell(newloc).terrain == uncarveable) {
						moveCost = 100000f;
					} else if (grid.cell(newloc).terrain.isPassable()) {
						moveCost = 1.0f;
					} else if (Compass.isOrthogonal(dir) && pathThroughWalls) {
						if (grid.cell(newloc).temp == Boolean.TRUE) {
							moveCost = 10.0f;
						} else if (!grid.cell(newloc).terrain.isPassable()) {
							moveCost = 10.0f;
						}
					}
				}
				if (moveCost > 0f) {
					check(grid, instance.new AStarData(newloc, next.cost + moveCost, next.location));
				}
			}
		}
		return retval;
	}

	private static void check(BrogueGrid grid, AStarData next) {
		AStarData existing = (AStarData)grid.cell(next.location).astar;
		if (existing == null) {
			queue.add(next);
			grid.cell(next.location).astar = next;
			return;
		}
		if (existing.cost <= next.cost) {
			return;
		}
		queue.remove(existing);
		queue.add(next);
		grid.cell(next.location).astar = next;
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
