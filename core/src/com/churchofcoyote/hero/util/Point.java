package com.churchofcoyote.hero.util;


public class Point {
	public int x, y;
	public Point(int x, int y) {
		this.x = x;
		this.y = y;
	}
	public Point() {
		this(0, 0);
	}
	
	@Override
	public String toString() {
		return "(" + x + "," + y + ")";
	}
	@Override
	public boolean equals(Object arg0) {
		if (!(arg0 instanceof Point)) {
			throw new ClassCastException();
		}
		Point other = (Point) arg0;
		return (x == other.x && y == other.y);
	}

	@Override
	public int hashCode() {
		return x*101 + y;
	}
	
	
}
