package com.bigsagebeast.hero.roguelike.world;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import com.bigsagebeast.hero.enums.Gender;
import com.bigsagebeast.hero.roguelike.world.ai.TacticChaseAndMelee;
import com.bigsagebeast.hero.roguelike.world.proc.ProcEffectHunger;
import com.bigsagebeast.hero.roguelike.world.proc.ProcMover;
import com.bigsagebeast.hero.roguelike.world.proc.ProcPlayer;
import com.bigsagebeast.hero.roguelike.world.proc.ProcEveryTurn;
import com.bigsagebeast.hero.roguelike.world.proc.monster.ProcMonster;
import com.bigsagebeast.hero.roguelike.game.Game;

public class Bestiary {
	public static Map<String, Phenotype> map = new HashMap<String, Phenotype>();

	static {

	}

	public static Entity create(String key) {
		return create(key, null);
	}

	public static Entity create(String key, String name) {
		Entity e = EntityTracker.create();

		Phenotype p = map.get(key);

		e.phenotypeName = key;
		if (name != null) {
			e.name = name;
		} else {
			e.name = p.name;
		}
		e.hitPoints = p.hitPoints;
		e.maxHitPoints = p.hitPoints;
		e.spellPoints = p.spellPoints;
		e.maxSpellPoints = p.spellPoints;
		e.divinePoints = p.divinePoints;
		e.maxDivinePoints = p.divinePoints;
		e.peaceful = p.peaceful;
		e.moveCost = p.moveCost;
		e.body = new Body(p.bodyPlan);
		if (p.glyphNames.size() > 0) {
			e.glyphNames = p.glyphNames.toArray(new String[0]);
		} else if (p.glyphName != null) {
			e.glyphNames = new String[] { p.glyphName };
		}
		e.palette = p.palette;
		if (p.gender == Gender.RANDOM) {
			e.gender = Game.random.nextInt(2) == 0 ? Gender.MALE : Gender.FEMALE;
		} else {
			e.gender = p.gender;
		}
		e.isManipulator = p.isManipulator;
		e.ambulation = p.ambulation;
		e.incorporeal = p.incorporeal;
		e.experienceAwarded = p.experienceAwarded;
		e.naturalWeaponDamage = p.naturalWeaponDamage;
		e.naturalWeaponToHit = p.naturalWeaponToHit;
		e.naturalArmorClass = p.naturalArmorClass;
		e.naturalArmorThickness = p.naturalArmorThickness;
		e.naturalRangedWeaponDamage = p.naturalRangedWeaponDamage;
		e.naturalRangedWeaponToHit = p.naturalRangedWeaponToHit;
		e.naturalRangedWeaponRange = p.naturalRangedWeaponRange;

		if (e.incorporeal) {
			// TODO: Should have its own flag for omniscient?
			e.visionRange = 9999;
		}

		if (p.moveCost == 0) throw new RuntimeException("Bad move cost");
		for (Consumer<Entity> consumer : p.setup) {
			consumer.accept(e);
		}
		e.addProc(new ProcEveryTurn());

		if (p.procLoaders != null) {
			for (LoadProc loader : p.procLoaders) {
				loader.apply(e);
			}
		}

		if (key.startsWith("pc.")) {
			e.addProc(new ProcPlayer());
			e.addProc(new ProcEffectHunger());
		}
		else if (p.isMonster && e.getProcByType(ProcMonster.class) == null) {
			//e.addProc(new ProcMonster(new ChaseAndMeleeTactic()));
			ProcMonster pm = new ProcMonster();
			e.addProc(pm);
			if (p.tacticLoader != null) {
				p.tacticLoader.apply(e);
			} else {
				pm.tactic = new TacticChaseAndMelee();
			}
		} else {
			ProcMover pm = new ProcMover();
			pm.setDelay(e, Game.random.nextInt(e.moveCost));
			e.addProc(pm);
		}

		return e;
	}

	public static Phenotype get(String name) {
		return map.get(name);
	}
}
