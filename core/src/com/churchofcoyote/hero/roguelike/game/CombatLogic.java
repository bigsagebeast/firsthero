package com.churchofcoyote.hero.roguelike.game;

import com.churchofcoyote.hero.roguelike.chart.DamageChart;
import com.churchofcoyote.hero.roguelike.world.Entity;

public class CombatLogic {

	public static void swing(Entity actor, Entity target) {
		
		Visibility vis = Game.getLevel().checkVis(Game.getPlayerEntity(), actor, target);
		
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
				// TODO should pass the entity you killed them with as 'tool'
				actor.forEachProc(p -> p.postKillAnother(target, null));

				Game.announceVis(vis,
					"You kill " + target.getVisibleName() + ".",
					actor.getVisibleName() + " kills you.",
					actor.getVisibleName() + " kills " + target.getVisibleName() + ".",
					null);
			} else {
				Game.announceVis(vis,
						"You hit " + target.getVisibleName() + ".",
						actor.getVisibleName() + " hits you.",
						actor.getVisibleName() + " hits " + target.getVisibleName() + ".",
						null);
			}
			//Game.announce("(" + damage + " damage.)");
		} else {
			Game.announceVis(vis,
					"You miss " + target.getVisibleName() + ".",
					actor.getVisibleName() + " misses you.",
					actor.getVisibleName() + " misses " + target.getVisibleName() + ".",
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
		target.hurt(damage);
		if (target.hitPoints <= 0) {
			target.dead = true;
		}
	}
}
