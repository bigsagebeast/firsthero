package com.churchofcoyote.hero.roguelike.game;

import com.churchofcoyote.hero.roguelike.chart.DamageChart;
import com.churchofcoyote.hero.roguelike.world.Entity;

public class CombatLogic {

	public static void swing(Entity actor, Entity target) {
		
		Visibility vis = Game.getLevel().checkVis(Game.getPlayer().entity, actor, target);
		
		// auto-hit
		boolean hit = true;
		
		if (hit) {
			Dice damageDice = DamageChart.weaponDamage(actor.stats);
			int damage = damageDice.roll();
			if (damage < 1) {
				damage = 1;
			}
			
			hurt(target, damage);
			
			if (target.dead) {
				Game.announceVis(vis,
					"You kill " + target.getVisibleName(Game.getPlayer()) + ".",
					actor.getVisibleName(Game.getPlayer()) + " kills you.",
					actor.getVisibleName(Game.getPlayer()) + " kills " + target.getVisibleName(Game.getPlayer()) + ".",
					null);
			} else {
				Game.announceVis(vis,
						"You hit " + target.getVisibleName(Game.getPlayer()) + ".",
						actor.getVisibleName(Game.getPlayer()) + " hits you.",
						actor.getVisibleName(Game.getPlayer()) + " hits " + target.getVisibleName(Game.getPlayer()) + ".",
						null);
			}
			//Game.announce("(" + damage + " damage.)");
		} else {
			Game.announceVis(vis,
					"You miss " + target.getVisibleName(Game.getPlayer()) + ".",
					actor.getVisibleName(Game.getPlayer()) + " misses you.",
					actor.getVisibleName(Game.getPlayer()) + " misses " + target.getVisibleName(Game.getPlayer()) + ".",
					null);
			/*
			Game.feelMsg(target, "The " + actor.getVisibleName(Game.getPlayer()) + " misses you.");
			Game.feelMsg(actor, "You miss the " + target.getVisibleName(Game.getPlayer()) + ".");
			*/
		}
	}
	
	public static void hurt(Entity target, int damage) {
		if (damage < 1) {
			throw new IllegalArgumentException("Less than 1 damage dealt");
		}
		target.hitPoints -= damage;
		if (target.hitPoints <= 0) {
			target.dead = true;
		}
	}
}
