package com.churchofcoyote.hero;

import java.util.Random;

public class GameState {
	private static Random rand = new Random();
	
	private long tick;
	private float seconds;
	
	public GameState(long tick, float seconds) {
		this.tick = tick;
		this.seconds = seconds;
	}
	public long getTick() {
		return tick;
	}
	public float getSeconds() {
		return seconds;
	}
	
	public int randInt(int bound) {
		return rand.nextInt(bound);
	}
	
	public float randFloat(float min, float max) {
		return min + (rand.nextFloat() * (max-min)); 
	}
}
