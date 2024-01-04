package com.bigsagebeast.hero.roguelike.game;

import com.bigsagebeast.hero.GameLoop;
import com.bigsagebeast.hero.roguelike.spells.Spell;
import com.bigsagebeast.hero.roguelike.world.Entity;
import com.bigsagebeast.hero.roguelike.world.proc.item.ProcWeaponAmmo;
import com.bigsagebeast.hero.roguelike.world.proc.item.ProcWeaponMelee;
import com.bigsagebeast.hero.roguelike.world.proc.item.ProcWeaponRanged;

public class CombatLogic {

	// TODO split into trySwing, doHit, doMiss
	public static void swing(Entity actor, Entity target, Entity tool) {

		int damage, accuracy;
		float randomFactor = generateWeaponRandomness();
		String withWeaponString = "";
		ProcWeaponMelee pwm = null;
		if (tool != null) {
			pwm = (ProcWeaponMelee)tool.getProcByType(ProcWeaponMelee.class);
		}
		// TODO should do this stuff across all procs, not just PWMs
		if (pwm != null) {
			damage = Math.round(pwm.averageDamage() * randomFactor);
			accuracy = pwm.toHitBonus(actor) + Game.random.nextInt(20);
			// TODO should be a TextBlock
			if (actor == Game.getPlayerEntity()) {
				withWeaponString = " with your " + tool.getVisibleNameWithQuantity();
			} else {
				// TODO pronouns...
				withWeaponString = " with their " + tool.getVisibleNameWithQuantity();
			}
		} else {
			damage = Math.round(actor.getNaturalWeaponDamage() * randomFactor);
			accuracy = actor.getNaturalWeaponToHit() + Game.random.nextInt(20);
			if (actor == Game.getPlayerEntity()) {
				// alert the player that they're unarmed - disable for monks, etc
				withWeaponString = " with your bare hands";
			}
		}

		accuracy += actor.getToHitBonus();
		damage += actor.getDamageBonus();

		int dodge = target.getArmorClass();

		boolean critical = Game.random.nextInt(20) == 0;
		if (accuracy >= dodge) {
			// TODO stop on pre failure
			actor.forEachProc((e, p) -> p.preDoHit(e, target, tool));
			target.forEachProc((e, p) -> p.preBeHit(e, actor, tool));

			if (!critical && damage > 0) {
				// TODO this isn't how thickness should work
				damage -= target.getArmorThickness();
				if (damage <= 0) {
					damage = 1;
				}
			}

			if (damage <= 0) {
				damage = 0;
			} else {
				hurt(target, damage);
			}

			if (damage == 0 && actor.naturalWeaponDamage == 0) {
				Game.announceVis(actor, target,
						"You touch " + target.getVisibleNameDefinite() + withWeaponString + ".",
						actor.getVisibleNameDefinite() + " touches you" + withWeaponString + ".",
						actor.getVisibleNameDefinite() + " touches " + target.getVisibleNameDefinite() + withWeaponString + ".",
						null);
				actor.forEachProc((e, p) -> p.postDoHit(e, target, tool));
				target.forEachProc((e, p) -> p.postBeHit(e, actor, tool));
			} else if (damage == 0) {
				// actually, can this happen?
				Game.announceVis(actor, target,
						"You hit " + target.getVisibleNameDefinite() + withWeaponString + ", but don't penetrate their armor.",
						actor.getVisibleNameDefinite() + "'s blow doesn't penetrate your armor.",
						actor.getVisibleNameDefinite() + "'s blow doesn't penetrate " + target.getVisibleNameDefinite() + "'s armor.",
						null);
				actor.forEachProc((e, p) -> p.postDoHit(e, target, tool));
				target.forEachProc((e, p) -> p.postBeHit(e, actor, tool));
			} else {
				// TODO: Damage type indicators instead of 'hit', for example 'slash' and 'crush'
				// TODO: Resistance and weakly modifiers, like "you stab the skeleton moderately" or "you crush the skeleton powerfully"
				if (critical) {
					Game.announceVis(actor, target,
							"You critically hit " + target.getVisibleNameDefinite() + withWeaponString + "!",
							actor.getVisibleNameDefinite() + " critically hits you" + withWeaponString + "!",
							actor.getVisibleNameDefinite() + " critically hits " + target.getVisibleNameDefinite() + withWeaponString + "!",
							null);
				} else {
					Game.announceVis(actor, target,
							"You hit " + target.getVisibleNameDefinite() + withWeaponString + ".",
							actor.getVisibleNameDefinite() + " hits you" + withWeaponString + ".",
							actor.getVisibleNameDefinite() + " hits " + target.getVisibleNameDefinite() + withWeaponString + ".",
							null);
				}
				actor.forEachProc((e, p) -> p.postDoHit(e, target, tool));
				target.forEachProc((e, p) -> p.postBeHit(e, actor, tool));
			}
		} else {
			Game.announceVis(actor, target,
					"You miss " + target.getVisibleNameDefinite() + withWeaponString + ".",
					actor.getVisibleNameDefinite() + " misses you" + withWeaponString + ".",
					actor.getVisibleNameDefinite() + " misses " + target.getVisibleNameDefinite() + withWeaponString + ".",
					null);

			actor.forEachProc((e, p) -> p.postDoMiss(e, target, tool));
			target.forEachProc((e, p) -> p.postBeMissed(e, actor, tool));
		}

		// TODO make use of the flag...
		if (target.hitPoints <= 0) {
			// TODO should pass the entity you killed them with as 'tool'
			// TODO does pre kill make sense?

			Game.announceVis(actor, target,
					"You slay " + target.getVisibleNameDefinite() + ".",
					actor.getVisibleNameDefinite() + " kills you.",
					actor.getVisibleNameDefinite() + " kills " + target.getVisibleNameDefinite() + ".",
					null);
			if (GameLoop.roguelikeModule.isRunning()) {
				// test to make sure we're not in a duel
				actor.forEachProc((e, p) -> p.postDoKill(e, target, null));
				target.forEachProc((e, p) -> p.postBeKilled(e, actor, null));
			}
		}
	}

	// T if hits, F if dodged/resisted/etc
	public static boolean castAttempt(Entity actor, Entity target, Spell spell) {
		// No pre/post for being cast on, that's handled in the spell
		if (Game.random.nextInt(5) == 0) {
			spell.announceDodged(actor, target);
			return false;
		}
		if (Game.random.nextInt(5) == 0) {
			spell.announceResisted(actor, target);
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
			if (GameLoop.roguelikeModule.isRunning()) {
				actor.forEachProc((e, p) -> p.postDoKill(e, target, null));
				target.forEachProc((e, p) -> p.postBeKilled(e, actor, null));
			}
			spell.announceHitWithKill(actor, target);
		}
	}

	public static void shoot(Entity actor, Entity target, Entity tool, Entity ammo) {
		String withWeaponString = "";

		int damage, accuracy;
		float randomFactor = generateWeaponRandomness();
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
		damage = Math.round(averageDamage * randomFactor);
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

			Game.announceVis(actor, target,
					"You hit " + target.getVisibleNameDefinite() + withWeaponString + ".",
					actor.getVisibleNameDefinite() + " hits you" + withWeaponString + ".",
					actor.getVisibleNameDefinite() + " hits " + target.getVisibleNameDefinite() + withWeaponString + ".",
					null);
			actor.forEachProc((e, p) -> p.postDoShoot(e, target, null));
			target.forEachProc((e, p) -> p.postBeShot(e, actor, null));
		} else {
			Game.announceVis(actor, target,
					"You miss " + target.getVisibleNameDefinite() + withWeaponString + ".",
					actor.getVisibleNameDefinite() + " misses you" + withWeaponString + ".",
					actor.getVisibleNameDefinite() + " misses " + target.getVisibleNameDefinite() + withWeaponString + ".",
					null);

			actor.forEachProc((e, p) -> p.postDoMiss(e, target, null));
			target.forEachProc((e, p) -> p.postBeMissed(e, actor, null));
		}

		// TODO make use of the flag...
		if (target.hitPoints <= 0) {
			// TODO should pass the entity you killed them with as 'tool'
			// TODO does pre kill make sense?

			Game.announceVis(actor, target,
					"You kill " + target.getVisibleNameDefinite() + ".",
					actor.getVisibleNameDefinite() + " kills you.",
					actor.getVisibleNameDefinite() + " kills " + target.getVisibleNameDefinite() + ".",
					null);
			if (GameLoop.roguelikeModule.isRunning()) {
				actor.forEachProc((e, p) -> p.postDoKill(e, target, ammo));
				target.forEachProc((e, p) -> p.postBeKilled(e, actor, ammo));
			}
		}
	}

	public static void hurt(Entity target, int damage) {
		if (damage < 1) {
			// TODO should be a debug log
			System.out.println("Less than 1 damage dealt");
		}
		target.hurt(damage);
	}

	public static boolean tryResist(Entity target, int difficulty, int statValue) {
		int statModifier = (statValue - 20) / 2;
		int roll = Dice.roll(1, 20, 0);
		if (statModifier > 0) {
			roll += Dice.roll(1, statModifier, 0);
		} else if (statModifier < 0) {
			roll -= Dice.roll(1, -statModifier, 0);
		}
		return (roll > difficulty);
	}

	// range is 0.25..1.75
	// one standard deviation is 0.75..1.25
	public static float generateWeaponRandomness() {
		float mean = 1.0f;
		float standardDeviation = 0.25f; // One standard deviation is 0.25
		float u1 = Game.random.nextFloat();
		float u2 = Game.random.nextFloat();

		// Use the Box-Muller transform to generate two independent normally distributed numbers
		float z = (float)Math.sqrt(-2.0 * Math.log(u1));
		float z0 = (float)(z * Math.cos(2.0f * Math.PI * u2));
		float z1 = (float)(z * Math.sin(2.0f * Math.PI * u2));

		// Scale and shift the numbers to match the desired mean and standard deviation
		float x0 = mean + z0 * standardDeviation;

		// Ensure the generated value is within the desired range [0, 2]
		return Math.min(Math.max(x0, 0.25f), 1.75f);
	}
}
