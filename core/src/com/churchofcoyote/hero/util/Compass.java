package com.churchofcoyote.hero.util;

import java.util.Arrays;

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
	private static final Iterable<Compass> points =
			Arrays.asList(new Compass[] {NORTH, NORTH_EAST, EAST, SOUTH_EAST, SOUTH, SOUTH_WEST, WEST, NORTH_WEST});
	private static final Iterable<Compass> diagonals =
			Arrays.asList(new Compass[] {NORTH_EAST, SOUTH_EAST, SOUTH_WEST, NORTH_WEST});
	
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
	
	public static Iterable<Compass> points() {
		return points;
	}
	
	public static Iterable<Compass> diagonals() {
		return diagonals;
	}
}
