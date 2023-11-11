package com.churchofcoyote.hero.roguelike.game;

import java.util.Comparator;

import com.churchofcoyote.hero.roguelike.world.Creature;

public class InitiativeOrderComparator implements Comparator<Creature> {

	@Override
	public int compare(Creature arg0, Creature arg1) {
		if (arg0 == null || arg1 == null) {
			return 0;
		}
		if (arg0.delay == arg1.delay) {
			if (Game.getPlayer().isCreature(arg0)) {
				return -1;
			} else if (Game.getPlayer().isCreature(arg1)) {
				return 1;
			}
			return 0;
		}
		return Integer.compare(arg0.delay, arg1.delay);
	}

}
