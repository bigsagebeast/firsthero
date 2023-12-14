package com.churchofcoyote.hero.roguelike.world;

import com.churchofcoyote.hero.util.Point;

public class LevelTransition {
	public String direction;
	public Point loc;
	public String fromMap;
	public String toMap;
	public Point arrival;
	
	public LevelTransition(String direction, Point pos, String toMap, Point arrival) {
		this.direction = direction;
		this.loc = pos;
		this.toMap = toMap;
		this.arrival = arrival;
	}

	public LevelTransition(String direction, Point fromPoint, String fromMap, String toMap) {
		this.direction = direction;
		this.loc = fromPoint;
		this.fromMap = fromMap;
		this.toMap = toMap;
	}
}
