package com.churchofcoyote.hero.roguelike.game;

import com.churchofcoyote.hero.roguelike.spells.Spell;
import com.churchofcoyote.hero.roguelike.world.Entity;
import com.churchofcoyote.hero.roguelike.world.proc.item.ProcWeaponAmmo;
import com.churchofcoyote.hero.roguelike.world.proc.item.ProcWeaponMelee;
import com.churchofcoyote.hero.roguelike.world.proc.item.ProcWeaponRanged;

public class CombatLogic {

	// TODO split into trySwing, doHit, doMiss
	public static void swing(Entity actor, Entity target, Entity tool) {
		
		Visibility vis = Game.getLevel().checkVis(Game.getPlayerEntity(), actor, target);

		int damage, accuracy;
		float randomFactor = Game.random.nextFloat() + 0.5f;
		String withWeaponString = "";
		ProcWeaponMelee pwm = null;
		if (tool != null) {
			pwm = (ProcWeaponMelee)tool.getProcByType(ProcWeaponMelee.class);
		}
		// TODO should do this stuff across all procs, not just PWMs
		if (pwm != null) {
			damage = (int)(pwm.averageDamage() * randomFactor);
			accuracy = pwm.toHitBonus(actor) + Game.random.nextInt(20);
			// TODO should be a TextBlock
			if (actor == Game.getPlayerEntity()) {
				withWeaponString = " with your " + tool.getVisibleNameWithQuantity();
			} else {
				// TODO pronouns...
				withWeaponString = " with their " + tool.getVisibleNameWithQuantity();
			}
		} else {
			damage = (int)(actor.getNaturalWeaponDamage() * randomFactor);
			accuracy = actor.getNaturalWeaponToHit() + Game.random.nextInt(20);
		}

		int dodge = target.getArmorClass();

		if (accuracy >= dodge) {
			// TODO stop on pre failure
			actor.forEachProc((e, p) -> p.preDoHit(e, target, tool));
			target.forEachProc((e, p) -> p.preBeHit(e, actor, tool));

			if (damage <= 0) {
				damage = 0;
			} else {
				hurt(target, damage);
			}

			if (damage == 0 && actor.naturalWeaponDamage == 0) {
				Game.announceVis(vis,
						"You touch " + target.getVisibleNameWithQuantity() + withWeaponString + ".",
						actor.getVisibleNameWithQuantity() + " touches you" + withWeaponString + ".",
						actor.getVisibleNameWithQuantity() + " touches " + target.getVisibleNameWithQuantity() + withWeaponString + ".",
						null);
				actor.forEachProc((e, p) -> p.postDoHit(e, target, tool));
				target.forEachProc((e, p) -> p.postBeHit(e, actor, tool));
			} else {
				// TODO: Damage type indicators instead of 'hit', for example 'slash' and 'crush'
				// TODO: Resistance and weakly modifiers, like "you stab the skeleton moderately" or "you crush the skeleton powerfully"
				Game.announceVis(vis,
						"You hit " + target.getVisibleNameWithQuantity() + withWeaponString + ".",
						actor.getVisibleNameWithQuantity() + " hits you" + withWeaponString + ".",
						actor.getVisibleNameWithQuantity() + " hits " + target.getVisibleNameWithQuantity() + withWeaponString + ".",
						null);
				actor.forEachProc((e, p) -> p.postDoHit(e, target, tool));
				target.forEachProc((e, p) -> p.postBeHit(e, actor, tool));
			}
			//Game.announce("(" + damage + " damage.)");
		} else {
			Game.announceVis(vis,
					"You miss " + target.getVisibleNameWithQuantity() + withWeaponString + ".",
					actor.getVisibleNameWithQuantity() + " misses you" + withWeaponString + ".",
					actor.getVisibleNameWithQuantity() + " misses " + target.getVisibleNameWithQuantity() + withWeaponString + ".",
					null);

			actor.forEachProc((e, p) -> p.postDoMiss(e, target, tool));
			target.forEachProc((e, p) -> p.postBeMissed(e, actor, tool));

			/*
			Game.feelMsg(target, "The " + actor.getVisibleName(Game.getPlayer()) + " misses you.");
			Game.feelMsg(actor, "You miss the " + target.getVisibleName(Game.getPlayer()) + ".");
			*/
		}

		// TODO make use of the flag...
		if (target.hitPoints <= 0) {
			// TODO should pass the entity you killed them with as 'tool'
			// TODO does pre kill make sense?

			Game.announceVis(vis,
					"You kill " + target.getVisibleNameWithQuantity() + ".",
					actor.getVisibleNameWithQuantity() + " kills you.",
					actor.getVisibleNameWithQuantity() + " kills " + target.getVisibleNameWithQuantity() + ".",
					null);
			actor.forEachProc((e, p) -> p.postDoKill(e, target, null));
			target.forEachProc((e, p) -> p.postBeKilled(e, actor, null));
		}
	}

	// T if hits, F if dodged/resisted/etc
	public static boolean castAttempt(Entity actor, Entity target, Spell spell) {
		// No pre/post for being cast on, that's handled in the spell
		if (Game.random.nextInt(5) == 0) {
			spell.announceDodged(target, actor);
			return false;
		}
		if (Game.random.nextInt(5) == 0) {
			spell.announceResisted(target, actor);
			return false;
		}
		return true;
	}

	public static void castDamage(Entity actor, Entity target, Spell spell, int rawDamage) {
		// No pre/post for being cast on, that's handled in the spell
		target.hurt(rawDamage);
		if (target.hitPoints > 0) {
			spell.announceHitWithoutKill(actor, target);
		} else {
			actor.forEachProc((e, p) -> p.postDoKill(e, target, null));
			target.forEachProc((e, p) -> p.postBeKilled(e, actor, null));
			spell.announceHitWithKill(actor, target);
		}
	}

	public static void shoot(Entity actor, Entity target, Entity tool, Entity ammo) {

		Visibility vis = Game.getLevel().checkVis(Game.getPlayerEntity(), actor, target);
		String withWeaponString = "";

		int damage, accuracy;
		float randomFactor = Game.random.nextFloat() + 0.5f;
		// TODO invoke all procs, not just pwa/pwr
		int averageDamage;
		int accuracyBonus;
		ProcWeaponAmmo pwa = (ProcWeaponAmmo)ammo.getProcByType(ProcWeaponAmmo.class);
		if (tool != null) {
			ProcWeaponRanged pwr = (ProcWeaponRanged)tool.getProcByType(ProcWeaponRanged.class);
			averageDamage = pwr.averageDamage(actor) + pwa.averageDamage(actor);
			accuracyBonus = pwr.toHitBonus(actor) + pwa.toHitBonus(actor);
		} else {
			averageDamage = actor.getNaturalRangedWeaponDamage() + pwa.averageDamage(actor);
			accuracyBonus = actor.getNaturalRangedWeaponToHit() + pwa.toHitBonus(actor);
		}
		damage = (int)(averageDamage * randomFactor);
		accuracy = accuracyBonus + Game.random.nextInt(20);
		// TODO should be a TextBlock
		if (actor == Game.getPlayerEntity()) {
			withWeaponString = " with your " + ammo.getVisibleNameWithQuantity();
		} else {
			// TODO pronouns...
			withWeaponString = " with their " + ammo.getVisibleNameWithQuantity();
		}

		int dodge = target.getArmorClass();

		if (accuracy >= dodge) {
			// TODO stop on pre failure
			actor.forEachProc((e, p) -> p.preDoShoot(e, target, null));
			target.forEachProc((e, p) -> p.preBeShot(e, actor, null));

			if (damage <= 0) {
				damage = 0;
			} else {
				hurt(target, damage);
			}

			Game.announceVis(vis,
					"You hit " + target.getVisibleNameWithQuantity() + withWeaponString + ".",
					actor.getVisibleNameWithQuantity() + " hits you" + withWeaponString + ".",
					actor.getVisibleNameWithQuantity() + " hits " + target.getVisibleNameWithQuantity() + withWeaponString + ".",
					null);
			actor.forEachProc((e, p) -> p.postDoShoot(e, target, null));
			target.forEachProc((e, p) -> p.postBeShot(e, actor, null));
		} else {
			Game.announceVis(vis,
					"You miss " + target.getVisibleNameWithQuantity() + withWeaponString + ".",
					actor.getVisibleNameWithQuantity() + " misses you" + withWeaponString + ".",
					actor.getVisibleNameWithQuantity() + " misses " + target.getVisibleNameWithQuantity() + withWeaponString + ".",
					null);

			actor.forEachProc((e, p) -> p.postDoMiss(e, target, null));
			target.forEachProc((e, p) -> p.postBeMissed(e, actor, null));

			/*
			Game.feelMsg(target, "The " + actor.getVisibleName(Game.getPlayer()) + " misses you.");
			Game.feelMsg(actor, "You miss the " + target.getVisibleName(Game.getPlayer()) + ".");
			*/
		}

		// TODO make use of the flag...
		if (target.hitPoints <= 0) {
			// TODO should pass the entity you killed them with as 'tool'
			// TODO does pre kill make sense?

			Game.announceVis(vis,
					"You kill " + target.getVisibleNameWithQuantity() + ".",
					actor.getVisibleNameWithQuantity() + " kills you.",
					actor.getVisibleNameWithQuantity() + " kills " + target.getVisibleNameWithQuantity() + ".",
					null);
			actor.forEachProc((e, p) -> p.postDoKill(e, target, ammo));
			target.forEachProc((e, p) -> p.postBeKilled(e, actor, ammo));
		}
	}

	public static void hurt(Entity target, int damage) {
		if (damage < 1) {
			// TODO should be a debug log
			System.out.println("Less than 1 damage dealt");
		}
		target.hurt(damage);
	}
}
