package com.churchofcoyote.hero.roguelike.chart;

import com.churchofcoyote.hero.roguelike.game.Dice;
import com.churchofcoyote.hero.roguelike.game.Rank;

public class DamageChart {

	public static Dice weaponDamage(Rank rank) {
		switch (rank) {
		case E:
			return new Dice(1, 1);
		case D_MINUS:
			return new Dice(1, 2);
		case D:
			return new Dice(1, 3);
		case D_PLUS:
			return new Dice(1, 4);
		case C_MINUS:
			return new Dice(1, 6);
		case C:
			return new Dice(1, 8);
		case C_PLUS:
			return new Dice(1, 10);
		case B_MINUS:
			return new Dice(2, 6);
		case B:
			return new Dice(2, 8);
		case B_PLUS:
			return new Dice(2, 10);
		case A_MINUS:
			return new Dice(3, 6, 2);
		case A:
			return new Dice(3, 8, 1);
		case A_PLUS:
			return new Dice(3, 10);
		case S:
			return new Dice(4, 8);
		default:
			throw new IllegalArgumentException();
		}
	}
}
