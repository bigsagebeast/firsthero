package com.churchofcoyote.hero.roguelike.world;

import com.churchofcoyote.hero.util.Point;

public class LevelTransition {
	public String direction;
	public Point loc;
	public String destination;
	public Point arrival;
	
	public LevelTransition(String direction, Point pos, String destination, Point arrival) {
		this.direction = direction;
		this.loc = pos;
		this.destination = destination;
		this.arrival = arrival;
	}
}
