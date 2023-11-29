package com.churchofcoyote.hero.roguelike.game;

import com.churchofcoyote.hero.roguelike.chart.DamageChart;
import com.churchofcoyote.hero.roguelike.world.Entity;

public class CombatLogic {

	public static void swing(Entity actor, Entity target) {
		
		Visibility vis = Game.getLevel().checkVis(Game.getPlayerEntity(), actor, target);
		
		float randomFactor = Game.random.nextFloat() + 0.5f;
		int damage = (int)(actor.getNaturalWeaponDamage() * randomFactor);
		int accuracy = actor.getNaturalWeaponToHit() + Game.random.nextInt(20);
		int dodge = target.getArmorClass();

		if (accuracy >= dodge) {
			// TODO stop on pre failure
			actor.forEachProc(p -> p.preDoHit(target, null));
			target.forEachProc(p -> p.preBeHit(actor, null));

			if (damage <= 0) {
				damage = 0;
			} else {
				hurt(target, damage);
			}

			// TODO make use of the flag...
			if (target.hitPoints <= 0) {
				// TODO should pass the entity you killed them with as 'tool'
				// TODO does pre kill make sense?

				Game.announceVis(vis,
					"You kill " + target.getVisibleName() + ".",
					actor.getVisibleName() + " kills you.",
					actor.getVisibleName() + " kills " + target.getVisibleName() + ".",
					null);
				actor.forEachProc(p -> p.postDoHit(target, null));
				target.forEachProc(p -> p.postBeHit(actor, null));

				actor.forEachProc(p -> p.postDoKill(target, null));
				target.forEachProc(p -> p.postBeKilled(actor, null));

			} else {
				if (damage == 0 && actor.naturalWeaponDamage == 0) {
					Game.announceVis(vis,
							"You touch " + target.getVisibleName() + ".",
							actor.getVisibleName() + " touches you.",
							actor.getVisibleName() + " touches " + target.getVisibleName() + ".",
							null);
					actor.forEachProc(p -> p.postDoHit(target, null));
					target.forEachProc(p -> p.postBeHit(actor, null));
				} else {
					Game.announceVis(vis,
							"You hit " + target.getVisibleName() + ".",
							actor.getVisibleName() + " hits you.",
							actor.getVisibleName() + " hits " + target.getVisibleName() + ".",
							null);
					actor.forEachProc(p -> p.postDoHit(target, null));
					target.forEachProc(p -> p.postBeHit(actor, null));
				}
			}
			//Game.announce("(" + damage + " damage.)");
		} else {
			Game.announceVis(vis,
					"You miss " + target.getVisibleName() + ".",
					actor.getVisibleName() + " misses you.",
					actor.getVisibleName() + " misses " + target.getVisibleName() + ".",
					null);

			actor.forEachProc(p -> p.postDoMiss(target, null));
			target.forEachProc(p -> p.postBeMissed(actor, null));

			/*
			Game.feelMsg(target, "The " + actor.getVisibleName(Game.getPlayer()) + " misses you.");
			Game.feelMsg(actor, "You miss the " + target.getVisibleName(Game.getPlayer()) + ".");
			*/
		}
	}
	
	public static void hurt(Entity target, int damage) {
		if (damage < 1) {
			// TODO should be a debug log
			System.out.println("Less than 1 damage dealt");
		}
		target.hurt(damage);
		if (target.hitPoints <= 0) {
			target.dead = true;
		}
	}
}
