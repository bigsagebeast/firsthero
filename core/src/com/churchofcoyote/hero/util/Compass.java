package com.churchofcoyote.hero.util;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public enum Compass {
	NORTH(0, -1),
	NORTH_EAST(1, -1),
	EAST(1, 0),
	SOUTH_EAST(1, 1),
	SOUTH(0, 1),
	SOUTH_WEST(-1, 1),
	WEST(-1, 0),
	NORTH_WEST(-1, -1),
	OTHER(0, 0);
	
	private int x, y;
	private boolean diagonal;
	private Compass(int x, int y) {
		this.x = x;
		this.y = y;
		diagonal = (x != 0 && y != 0);
	}
	private static final List<Compass> points =
			Arrays.asList(new Compass[] {NORTH, NORTH_EAST, EAST, SOUTH_EAST, SOUTH, SOUTH_WEST, WEST, NORTH_WEST});
	private static final List<Compass> diagonals =
			Arrays.asList(new Compass[] {NORTH_EAST, SOUTH_EAST, SOUTH_WEST, NORTH_WEST});
	public static final List<Compass> orthogonal =
			Arrays.asList(new Compass[] {NORTH, EAST, SOUTH, WEST});

	private static Random random = new Random();

	public int getX() {
		return x;
	}
	public int getY() {
		return y;
	}
	public boolean isDiagonal() {
		return diagonal;
	}
	public Point from(Point from) {
		if (this == OTHER) {
			return null;
		}
		return new Point(from.x + x, from.y + y);
	}
	
	public static Compass to(Point from, Point to) {
		for (Compass c : values()) {
			if (from.x + c.x == to.x && from.y + c.y == to.y) {
				return c;
			}
		}
		return OTHER;
	}

	public static List<Compass> neighbors(Compass c) {
		if (c == NORTH) return Arrays.asList(new Compass[] {NORTH_WEST, NORTH_EAST});
		if (c == NORTH_EAST) return Arrays.asList(new Compass[] {NORTH, EAST});
		if (c == EAST) return Arrays.asList(new Compass[] {NORTH_EAST, SOUTH_EAST});
		if (c == SOUTH_EAST) return Arrays.asList(new Compass[] {EAST, SOUTH});
		if (c == SOUTH) return Arrays.asList(new Compass[] {SOUTH_EAST, SOUTH_WEST});
		if (c == SOUTH_WEST) return Arrays.asList(new Compass[] {SOUTH, WEST});
		if (c == WEST) return Arrays.asList(new Compass[] {SOUTH_WEST, NORTH_WEST});
		/*if (c == NORTH_WEST)*/ return Arrays.asList(new Compass[] {WEST, NORTH});
	}
	
	public static List<Compass> points() {
		return points;
	}
	
	public static List<Compass> diagonals() {
		return diagonals;
	}

	public static boolean isOrthogonal(Compass dir) {
		for (Compass oDir : orthogonal) {
			if (dir == oDir) {
				return true;
			}
		}
		return false;
	}

	public static Compass randomDirection() {
		return points.get(random.nextInt(8));
	}
}
