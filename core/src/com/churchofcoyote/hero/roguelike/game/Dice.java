package com.churchofcoyote.hero.roguelike.game;

import java.util.Random;

public class Dice {

	public static final Random rand = new Random();

	public static int roll(int number, int sides, int modifier) {
		if (number < 1 || number > 256) {
			// let's not hang the game, shall we?
			throw new IllegalArgumentException();
		}

		int accum = 0;
		for (int i=0; i<number; i++) {
			accum += rand.nextInt(sides) + 1;
		}
		return accum + modifier;
	}

	public final int number;
	public final int sides;
	public final int modifier;
	
	public Dice(int number, int sides, int modifier) {
		this.number = number;
		this.sides = sides;
		this.modifier = modifier;
		
		if (number < 1 || number > 256) {
			// let's not hang the game, shall we?
			throw new IllegalArgumentException();
		}
	}
	
	public Dice(int number, int sides) {
		this(number, sides, 0);
	}
	
	public int roll() {
		int accum = 0;
		for (int i=0; i<number; i++) {
			accum += rand.nextInt(sides) + 1;
		}
		return accum + modifier;
	}
}
