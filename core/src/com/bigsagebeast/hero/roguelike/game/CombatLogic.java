package com.bigsagebeast.hero.roguelike.game;

import com.bigsagebeast.hero.GameLoop;
import com.bigsagebeast.hero.roguelike.spells.Spell;
import com.bigsagebeast.hero.roguelike.world.Bestiary;
import com.bigsagebeast.hero.roguelike.world.Entity;
import com.bigsagebeast.hero.roguelike.world.Phenotype;
import com.bigsagebeast.hero.roguelike.world.proc.item.ProcWeaponAmmo;
import com.bigsagebeast.hero.roguelike.world.proc.item.ProcWeaponMelee;
import com.bigsagebeast.hero.roguelike.world.proc.item.ProcWeaponRanged;

import java.util.concurrent.atomic.AtomicReference;

public class CombatLogic {

	// TODO split into trySwing, doHit, doMiss
	public static SwingResult swing(Entity actor, Entity target, Entity tool) {
		SwingResult result = new SwingResult();
		result.tool = tool;

		float damage, accuracy, penetration;
		float randomFactor = generateWeaponRandomness();
		ProcWeaponMelee pwm = null;
		if (tool != null) {
			pwm = (ProcWeaponMelee)tool.getProcByType(ProcWeaponMelee.class);
		}
		// TODO should do this stuff across all procs, not just PWMs
		if (pwm != null) {
			damage = pwm.getDamage(tool, actor) * randomFactor * getDamageReceivedMultiplier(target);
			accuracy = pwm.getToHit(tool, actor) + Game.random.nextInt(20);
			penetration = pwm.getPenetration(tool, actor);
		} else {
			damage = actor.getNaturalWeaponDamage() * randomFactor * getDamageReceivedMultiplier(target);
			accuracy = actor.getNaturalWeaponToHit() + Game.random.nextInt(20);
			penetration = actor.getNaturalWeaponPenetration();
		}

		accuracy += actor.getToHitBonus();
		damage += actor.getDamageBonus();
		result.damage = (int)Math.floor(damage);
		if (damage < 0) {
			damage = 0;
		}

		int dodge = target.getArmorClass();
		result.critical = Game.random.nextInt(20) == 0;

		if (accuracy >= dodge) {
			// Rely on these to generate their own messages
			// Is something preventing the attacker from hitting?
			boolean canHit = actor.forEachProcIncludingEquipmentFailOnFalse((e, p) -> p.preDoHit(e, target, tool, result));

			// Is something preventing the defender from being hit?
			if (canHit) {
				canHit = target.forEachProcIncludingEquipmentFailOnFalse((e, p) -> p.preBeHit(e, actor, tool, result));
			}
			if (!canHit) {
				result.cancelled = true;
			}
			result.hit = canHit;

			// Does the attack fail to penetrate?
			if (canHit) {
				int penetrationShortfall = (int) (target.getArmorThickness() - penetration);
				for (int i = 0; i < penetrationShortfall; i++) {
					if (Game.random.nextInt(20) < 2) {
						canHit = false;
					}
				}
				result.penetrationFailed = !canHit;
			}
		} else {
			result.hit = false;
		}
		return result;
	}

	public static void doHit(Entity actor, Entity target, SwingResult result) {
		Entity tool = result.tool;
		Phenotype actorPhenotype = Bestiary.get(actor.phenotypeName);
		String withWeaponString = getWeaponString(actor, tool);

		// TODO What does critting even do?

		if (result.damage > 0) {
			hurt(target, result.damage, actor.getVisibleNameIndefiniteOrSpecific());
		}

		if (result.damage == 0 && actor.naturalWeaponDamage == 0) {
			Game.announceVis(actor, target,
					"You touch " + target.getVisibleNameDefinite() + withWeaponString + ".",
					actor.getVisibleNameDefinite() + " touches you" + withWeaponString + ".",
					actor.getVisibleNameDefinite() + " touches " + target.getVisibleNameDefinite() + withWeaponString + ".",
					null);
			actor.forEachProcIncludingEquipment((e, p) -> p.postDoHit(e, target, tool, result));
			target.forEachProcIncludingEquipment((e, p) -> p.postBeHit(e, actor, tool, result));
		} else {
			// TODO: Damage type indicators instead of 'hit', for example 'slash' and 'crush'
			// TODO: Resistance and weakly modifiers, like "you stab the skeleton moderately" or "you crush the skeleton powerfully"
			if (result.critical) {
				Game.announceVis(actor, target,
						"You critically hit " + target.getVisibleNameDefinite() + withWeaponString + "!",
						actor.getVisibleNameDefinite() + " critically hits you" + withWeaponString + "!",
						actor.getVisibleNameDefinite() + " critically hits " + target.getVisibleNameDefinite() + withWeaponString + "!",
						null);
			} else {
				if (actorPhenotype.naturalWeaponText != null && withWeaponString.isEmpty()) {
					Game.announceVis(actor, target,
							"You " + actorPhenotype.naturalWeaponText + " " + target.getVisibleNameDefinite() + ".",
							actor.getVisibleNameDefinite() + " " + actorPhenotype.naturalWeaponText + "s you.",
							actor.getVisibleNameDefinite() + " " + actorPhenotype.naturalWeaponText + "s " + target.getVisibleNameDefinite() + ".",
							null);
				} else {
					Game.announceVis(actor, target,
							"You hit " + target.getVisibleNameDefinite() + withWeaponString + ".",
							actor.getVisibleNameDefinite() + " hits you" + withWeaponString + ".",
							actor.getVisibleNameDefinite() + " hits " + target.getVisibleNameDefinite() + withWeaponString + ".",
							null);
				}
			}
			actor.forEachProcIncludingEquipment((e, p) -> p.postDoHit(e, target, tool, result));
			target.forEachProcIncludingEquipment((e, p) -> p.postBeHit(e, actor, tool, result));
		}

		if (target.dead && target.hitPoints <= 0) {
			// TODO should pass the entity you killed them with as 'tool'
			// TODO does pre kill make sense?

			Game.announceVis(actor, target,
					"You slay " + target.getVisibleNameDefinite() + ".",
					actor.getVisibleNameDefinite() + " kills you.",
					actor.getVisibleNameDefinite() + " kills " + target.getVisibleNameDefinite() + ".",
					null);
			if (GameLoop.roguelikeModule.isRunning()) {
				// test to make sure we're not in a duel
				// note: postDoKill might be called multiple times, and this is likely the SECOND time
				actor.forEachProcIncludingEquipment((e, p) -> p.postDoKill(e, target, null));
				target.forEachProcIncludingEquipment((e, p) -> p.postBeKilled(e, actor, null));
			}
		}
	}

	public static void doMiss(Entity actor, Entity target, SwingResult result) {
		Entity tool = result.tool;
		String withWeaponString = getWeaponString(actor, tool);

		Game.announceVis(actor, target,
				"You miss " + target.getVisibleNameDefinite() + withWeaponString + ".",
				actor.getVisibleNameDefinite() + " misses you" + withWeaponString + ".",
				actor.getVisibleNameDefinite() + " misses " + target.getVisibleNameDefinite() + withWeaponString + ".",
				null);

		actor.forEachProcIncludingEquipment((e, p) -> p.postDoMiss(e, target, tool));
		target.forEachProcIncludingEquipment((e, p) -> p.postBeMissed(e, actor, tool));

	}

	public static void doPenetrationFailed(Entity actor, Entity target, SwingResult result) {
		Entity tool = result.tool;
		String withWeaponString = getWeaponString(actor, tool);

		Game.announceVis(actor, target,
				"You hit " + target.getVisibleNameDefinite() + withWeaponString + ", but don't penetrate their armor.",
				actor.getVisibleNameDefinite() + "'s blow doesn't penetrate your armor.",
				actor.getVisibleNameDefinite() + "'s blow doesn't penetrate " + target.getVisibleNameDefinite() + "'s armor.",
				null);
		actor.forEachProcIncludingEquipment((e, p) -> p.postDoHit(e, target, tool, result));
		target.forEachProcIncludingEquipment((e, p) -> p.postBeHit(e, actor, tool, result));

		if (target.dead && target.hitPoints <= 0) {
			// TODO should pass the entity you killed them with as 'tool'
			// TODO does pre kill make sense?

			Game.announceVis(actor, target,
					"You slay " + target.getVisibleNameDefinite() + ".",
					actor.getVisibleNameDefinite() + " kills you.",
					actor.getVisibleNameDefinite() + " kills " + target.getVisibleNameDefinite() + ".",
					null);
			if (GameLoop.roguelikeModule.isRunning()) {
				// test to make sure we're not in a duel
				actor.forEachProcIncludingEquipment((e, p) -> p.postDoKill(e, target, null));
				target.forEachProcIncludingEquipment((e, p) -> p.postBeKilled(e, actor, null));
			}
		}
	}

	public static String getWeaponString(Entity actor, Entity tool) {
		ProcWeaponMelee pwm = tool == null ? null : (ProcWeaponMelee)tool.getProcByType(ProcWeaponMelee.class);
		// TODO should do this stuff across all procs, not just PWMs
		if (pwm != null) {
			// TODO should be a TextBlock
			if (actor == Game.getPlayerEntity()) {
				return " with your " + tool.getVisibleNameWithQuantity();
			} else {
				String pronoun = actor.gender.possessive;
				return " with " + pronoun + " " + tool.getVisibleNameWithQuantity();
			}
		} else {
			if (actor == Game.getPlayerEntity()) {
				// alert the player that they're unarmed - disable for monks, etc
				return " with your bare hands";
			}
		}
		return "";
	}

	// T if hits, F if dodged/resisted/etc
	public static boolean castAttempt(Entity actor, Entity target, Spell spell) {
		// No pre/post for being cast on, that's handled in the spell
		if (spell.isDodgeable() && Game.random.nextInt(15) == 0) {
			// SUPER hacky - slow things can't dodge
			if (target.getPhenotype().moveCost <= 1000) {
				spell.announceDodged(actor, target);
			}
			return false;
		}
		if (spell.isResistable() && Game.random.nextInt(15) == 0) {
			spell.announceResisted(actor, target);
			return false;
		}
		return true;
	}

	public static void castDamage(Entity actor, Entity target, Spell spell, float rawDamage) {
		// No pre/post for being cast on, that's handled in the spell
		target.hurt((int)rawDamage, false, actor.getVisibleNameIndefiniteOrSpecific());
		if (target.hitPoints > 0) {
			spell.announceHitWithoutKill(actor, target);
		} else {
			if (GameLoop.roguelikeModule.isRunning()) {
				actor.forEachProcIncludingEquipment((e, p) -> p.postDoKill(e, target, null));
				target.forEachProcIncludingEquipment((e, p) -> p.postBeKilled(e, actor, null));
			}
			spell.announceHitWithKill(actor, target);
		}
	}

	public static void shoot(Entity actor, Entity target, Entity tool, Entity ammo) {
		String withWeaponString = "";
		SwingResult result = new SwingResult();

		int accuracy;
		float randomFactor = generateWeaponRandomness();
		// TODO invoke all procs, not just pwa/pwr
		float averageDamage;
		float accuracyBonus;
		float penetration; // TODO more stuff
		ProcWeaponAmmo pwa = (ProcWeaponAmmo)ammo.getProcByType(ProcWeaponAmmo.class);
		if (tool != null) {
			ProcWeaponRanged pwr = (ProcWeaponRanged)tool.getProcByType(ProcWeaponRanged.class);
			averageDamage = pwr.getDamage(tool, actor) + pwa.averageDamage(ammo, actor);
			accuracyBonus = pwr.getToHit(tool, actor) + pwa.toHitBonus(ammo, actor);
			penetration = pwr.getPenetration(tool, actor) + pwa.penetration(ammo, actor);
		} else {
			averageDamage = actor.getNaturalRangedWeaponDamage() + pwa.averageDamage(ammo, actor);
			accuracyBonus = actor.getNaturalRangedWeaponToHit() + pwa.toHitBonus(ammo, actor);
			penetration = 0; // TODO
		}
		Float floatDamage = averageDamage * randomFactor * getDamageReceivedMultiplier(target);
		result.damage = Math.round(floatDamage);
		accuracy = Math.round(accuracyBonus + Game.random.nextInt(20));
		// TODO should be a TextBlock
		if (actor == Game.getPlayerEntity()) {
			withWeaponString = " with your " + ammo.getVisibleNameWithQuantity();
		} else {
			// TODO pronouns...
			withWeaponString = " with their " + ammo.getVisibleNameWithQuantity();
		}

		int dodge = target.getArmorClass();

		if (accuracy >= dodge) {
			// Rely on these to generate their own messages
			result.hit = true;
			boolean canHit = actor.forEachProcIncludingEquipmentFailOnFalse((e, p) -> p.preDoShoot(e, target, null));

			if (canHit) {
				canHit = target.forEachProcIncludingEquipmentFailOnFalse((e, p) -> p.preBeShot(e, actor, null));
			}

			// Does the attack fail to penetrate?
			if (canHit) {
				int penetrationShortfall = (int) (target.getArmorThickness() - penetration);
				for (int i = 0; i < penetrationShortfall; i++) {
					if (Game.random.nextInt(20) < 2) {
						canHit = false;
					}
				}
				result.penetrationFailed = !canHit;
				if (!canHit) {
					Game.announceVis(actor, target,
							"You hit " + target.getVisibleNameDefinite() + withWeaponString + ", but don't penetrate their armor.",
							actor.getVisibleNameDefinite() + "'s shot doesn't penetrate your armor.",
							actor.getVisibleNameDefinite() + "'s shot doesn't penetrate " + target.getVisibleNameDefinite() + "'s armor.",
							null);
					actor.forEachProcIncludingEquipment((e, p) -> p.postDoHit(e, target, ammo, result));
					target.forEachProcIncludingEquipment((e, p) -> p.postBeHit(e, actor, ammo, result));
				}
			}

			if (canHit) {
				if (result.damage <= 0) {
					result.damage = 0;
				} else {
					hurt(target, result.damage, actor.getVisibleNameIndefiniteOrSpecific() + "'s " + ammo.getVisibleName());
				}

				Game.announceVis(actor, target,
						"You hit " + target.getVisibleNameDefinite() + withWeaponString + ".",
						actor.getVisibleNameDefinite() + " hits you" + withWeaponString + ".",
						actor.getVisibleNameDefinite() + " hits " + target.getVisibleNameDefinite() + withWeaponString + ".",
						null);
				actor.forEachProcIncludingEquipment((e, p) -> p.postDoShoot(e, target, null));
				target.forEachProcIncludingEquipment((e, p) -> p.postBeShot(e, actor, null));
			}
		} else {
			Game.announceVis(actor, target,
					"You miss " + target.getVisibleNameDefinite() + withWeaponString + ".",
					actor.getVisibleNameDefinite() + " misses you" + withWeaponString + ".",
					actor.getVisibleNameDefinite() + " misses " + target.getVisibleNameDefinite() + withWeaponString + ".",
					null);

			actor.forEachProcIncludingEquipment((e, p) -> p.postDoMiss(e, target, null));
			target.forEachProcIncludingEquipment((e, p) -> p.postBeMissed(e, actor, null));
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
				actor.forEachProcIncludingEquipment((e, p) -> p.postDoKill(e, target, ammo));
				target.forEachProcIncludingEquipment((e, p) -> p.postBeKilled(e, actor, ammo));
			}
		}
	}

	public static void hurt(Entity target, int damage, String deathMessage) {
		if (damage < 1) {
			// TODO should be a debug log
			System.out.println("Less than 1 damage dealt");
		}
		target.hurt(damage, false, deathMessage);
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

	public static float getDamageReceivedMultiplier(Entity entity) {
		AtomicReference<Float> accumulator = new AtomicReference<>(1.0f);
		entity.forEachProcIncludingEquipment((e, p) -> accumulator.updateAndGet(val -> val *= p.provideDamageReceivedMultiplier(e)));
		return accumulator.get();
	}
}
