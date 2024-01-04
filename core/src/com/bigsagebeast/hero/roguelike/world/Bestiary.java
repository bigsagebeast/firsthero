package com.bigsagebeast.hero.roguelike.world;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import com.bigsagebeast.hero.enums.Ambulation;
import com.bigsagebeast.hero.enums.Gender;
import com.bigsagebeast.hero.glyphtile.Palette;
import com.bigsagebeast.hero.glyphtile.PaletteEntry;
import com.bigsagebeast.hero.roguelike.world.ai.ChaseAndMeleeTactic;
import com.bigsagebeast.hero.roguelike.world.ai.RangedAmmoThenMeleeTactic;
import com.bigsagebeast.hero.roguelike.world.proc.ProcBurningTouch;
import com.bigsagebeast.hero.roguelike.world.proc.ProcMover;
import com.bigsagebeast.hero.roguelike.world.proc.ProcPlayer;
import com.bigsagebeast.hero.roguelike.world.proc.ProcTimedEffects;
import com.bigsagebeast.hero.roguelike.world.proc.monster.ProcMonster;
import com.bigsagebeast.hero.roguelike.world.proc.monster.ProcShooter;
import com.bigsagebeast.hero.roguelike.game.Game;
import com.bigsagebeast.hero.roguelike.game.Rank;

public class Bestiary {
	public static Map<String, Phenotype> map = new HashMap<String, Phenotype>();

	static {
		Phenotype goblinArcher = new Phenotype();
		Phenotype goblinWarrior = new Phenotype();
		Phenotype farmer = new Phenotype();
		Phenotype wolf = new Phenotype();
		Phenotype fungusRed = new Phenotype();
		Phenotype fungusGreenFunglet = new Phenotype();

		goblinWarrior.name = "sea-withered goblin warrior";
		goblinWarrior.description = "From across the acid sea, these goblins come ready to fight. Their flesh and their weapons are scarred and pitted from exposure, but their training makes them dangerous foes.";
		goblinWarrior.peaceful = false;
		goblinWarrior.hitPoints = 16;
		goblinWarrior.isMonster = true;
		goblinWarrior.bodyPlan = "humanoid";
		goblinWarrior.glyphName = "humanoid.goblin.warrior";
		goblinWarrior.isManipulator = true;
		goblinWarrior.palette = new PaletteEntry(Palette.COLOR_DARKGREEN, Palette.COLOR_RED, Palette.COLOR_BROWN);
		goblinWarrior.gender = Gender.RANDOM;
		goblinWarrior.experienceAwarded = 20;
		goblinWarrior.threat = 2;
		goblinWarrior.naturalWeaponDamage = 6;
		goblinWarrior.naturalWeaponToHit = 1;
		goblinWarrior.naturalArmorClass = 5;
		goblinWarrior.tags.add("goblin");
		goblinWarrior.tags.add("generic-fantasy");

		goblinArcher.name = "sea-withered goblin archer";
		goblinArcher.description = "Their bows have been crafted on the mainland; such wood and sinew would have scarcely survived the trip. Their arrows are meant not for hunting, but for war.";
		goblinArcher.peaceful = false;
		goblinArcher.hitPoints = 8;
		goblinArcher.isMonster = true;
		goblinArcher.bodyPlan = "humanoid";
		goblinArcher.glyphName = "humanoid.goblin.archer";
		goblinArcher.isManipulator = true;
		goblinArcher.palette = new PaletteEntry(Palette.COLOR_DARKGREEN, Palette.COLOR_RED, Palette.COLOR_BROWN);
		goblinArcher.gender = Gender.RANDOM;
		goblinArcher.experienceAwarded = 15;
		goblinArcher.threat = 2;
		goblinArcher.naturalWeaponDamage = 3;
		goblinArcher.naturalWeaponToHit = -2;
		goblinArcher.naturalRangedWeaponDamage = 0;
		goblinArcher.naturalRangedWeaponToHit = -1;
		goblinArcher.naturalRangedWeaponRange = 6;
		goblinArcher.naturalArmorClass = 0;
		goblinArcher.setup.add(e -> {
			e.addProc(new ProcShooter("weapon.ammo.arrow"));
			e.addProc(new ProcMonster(new RangedAmmoThenMeleeTactic(4)));
		});
		goblinArcher.tags.add("goblin");
		goblinArcher.tags.add("generic-fantasy");

		Phenotype goblinSlinger = new Phenotype();
		goblinSlinger.name = "sea-withered goblin slinger";
		goblinSlinger.description = "Even half-blinded by the acid sea, these goblins with their leather straps can hurl projectiles with forcefully enough to break bones.";
		goblinSlinger.peaceful = false;
		goblinSlinger.hitPoints = 8;
		goblinSlinger.isMonster = true;
		goblinSlinger.bodyPlan = "humanoid";
		goblinSlinger.glyphName = "humanoid.goblin.slinger";
		goblinSlinger.isManipulator = true;
		goblinSlinger.palette = new PaletteEntry(Palette.COLOR_DARKGREEN, Palette.COLOR_RED, Palette.COLOR_BROWN);
		goblinSlinger.gender = Gender.RANDOM;
		goblinSlinger.experienceAwarded = 15;
		goblinSlinger.threat = 1;
		goblinSlinger.naturalWeaponDamage = 3;
		goblinSlinger.naturalWeaponToHit = -2;
		goblinSlinger.naturalRangedWeaponDamage = 0;
		goblinSlinger.naturalRangedWeaponToHit = -4;
		goblinSlinger.naturalRangedWeaponRange = 4;
		goblinSlinger.naturalArmorClass = 0;
		goblinSlinger.setup.add(e -> {
			e.addProc(new ProcShooter("weapon.ammo.rock"));
			e.addProc(new ProcMonster(new RangedAmmoThenMeleeTactic(6)));
		});
		goblinSlinger.tags.add("goblin");
		goblinSlinger.tags.add("generic-fantasy");
		map.put("goblin.slinger", goblinSlinger);

		wolf.name = "dungeon wolf";
		wolf.description = "Prey runs barely thick enough in the strange dungeon ecosystem to support a pack of these hungry predators. Any meat they can find is a meal. Dungeon wolves are heavy beasts with bone-crushing teeth.";
		wolf.peaceful = false;
		wolf.hitPoints = 20;
		wolf.isMonster = true;
		wolf.bodyPlan = "humanoid";
		wolf.glyphName = "animal.canine";
		wolf.isManipulator = false;
		wolf.palette = new PaletteEntry(Palette.COLOR_GRAY, Palette.COLOR_WHITE, Palette.COLOR_RED);
		wolf.experienceAwarded = 40;
		wolf.threat = 3;
		wolf.naturalWeaponDamage = 8;
		wolf.naturalWeaponToHit = 1;
		wolf.naturalArmorClass = 5;
		wolf.tags.add("generic-fantasy");
		wolf.tags.add("animal");

		fungusRed.name = "red fungus";
		fungusRed.description = "An overgrowth of globules and stalks that somehow finds the motility to slowly pursue and consume dead or slow prey. The air around it shimmers with dangerous heat.";
		fungusRed.peaceful = false;
		fungusRed.hitPoints = 25;
		fungusRed.isMonster = true;
		fungusRed.bodyPlan = "humanoid";
		fungusRed.glyphName = "plant.fungus";
		fungusRed.isManipulator = false;
		fungusRed.palette = new PaletteEntry(Palette.COLOR_RED, Palette.COLOR_TAN, Palette.COLOR_BROWN);
		fungusRed.experienceAwarded = 15;
		fungusRed.moveCost = 20000;
		fungusRed.threat = 2;
		fungusRed.naturalWeaponDamage = 0;
		fungusRed.naturalWeaponToHit = -5;
		fungusRed.naturalArmorClass = -5;
		fungusRed.setup.add((e) -> {e.addProc(new ProcBurningTouch(3, 3, 0));});
		fungusRed.tags.add("generic-fantasy");
		fungusRed.tags.add("plant");

		fungusGreenFunglet.name = "green funglet";
		fungusGreenFunglet.description = "A small overgrowth of green globules, the fruiting bodies of this slow-moving fungal creature have not yet emerged.";
		fungusGreenFunglet.peaceful = false;
		fungusGreenFunglet.hitPoints = 10;
		fungusGreenFunglet.isMonster = true;
		fungusGreenFunglet.bodyPlan = "humanoid";
		fungusGreenFunglet.glyphName = "plant.fungus";
		fungusGreenFunglet.isManipulator = false;
		fungusGreenFunglet.palette = new PaletteEntry(Palette.COLOR_DARKGREEN, Palette.COLOR_LIGHTGREEN, Palette.COLOR_BROWN);
		fungusGreenFunglet.experienceAwarded = 5;
		fungusGreenFunglet.moveCost = 20000;
		fungusGreenFunglet.threat = 0;
		fungusGreenFunglet.naturalWeaponDamage = 0;
		fungusGreenFunglet.naturalWeaponToHit = -5;
		fungusGreenFunglet.naturalArmorClass = -10;
		fungusGreenFunglet.packSize = 2;
		fungusGreenFunglet.packSpawnArea = 9;
		fungusGreenFunglet.tags.add("generic-fantasy");
		fungusGreenFunglet.tags.add("plant");

		farmer.name = "Farmer";
		farmer.description = "Your parents are hard-working farmers who keep to themselves. When the goblins landed from the acid sea, their farm was in the way, and now they are in danger of losing their land and their lives.";
		farmer.peaceful = true;
		farmer.hitPoints = 10;
		farmer.isMonster = false;
		farmer.bodyPlan = "humanoid";
		farmer.glyphName = "creature.humanoid";
		farmer.isManipulator = true;
		farmer.palette = new PaletteEntry(Palette.COLOR_YELLOW, Palette.COLOR_TAN, Palette.COLOR_BROWN);
		farmer.experienceAwarded = 10;
		farmer.threat = -1;
		farmer.chatPage = "intro.farmer.landing";

		map.put("goblin.warrior", goblinWarrior);
		map.put("goblin.archer", goblinArcher);
		map.put("wolf", wolf);
		map.put("fungus.red", fungusRed);
		map.put("fungus.greenfunglet", fungusGreenFunglet);
		map.put("farmer", farmer);
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
		e.glyphName = p.glyphName;
		e.palette = p.palette;
		if (p.gender == Gender.RANDOM) {
			e.gender = Game.random.nextInt(2) == 0 ? Gender.MALE : Gender.FEMALE;
		} else {
			e.gender = p.gender;
		}
		e.isManipulator = p.isManipulator;
		e.ambulation = p.ambulation;
		e.experienceAwarded = p.experienceAwarded;
		e.naturalWeaponDamage = p.naturalWeaponDamage;
		e.naturalWeaponToHit = p.naturalWeaponToHit;
		e.naturalArmorClass = p.naturalArmorClass;
		e.naturalArmorThickness = p.naturalArmorThickness;
		e.naturalRangedWeaponDamage = p.naturalRangedWeaponDamage;
		e.naturalRangedWeaponToHit = p.naturalRangedWeaponToHit;
		e.naturalRangedWeaponRange = p.naturalRangedWeaponRange;

		if (p.moveCost == 0) throw new RuntimeException("Bad move cost");
		for (Consumer<Entity> consumer : p.setup) {
			consumer.accept(e);
		}
		e.addProc(new ProcTimedEffects());

		if (p.procLoaders != null) {
			for (LoadProc loader : p.procLoaders) {
				loader.apply(e);
			}
		}

		if (key.startsWith("pc.")) {
			e.addProc(new ProcPlayer());
		}
		else if (p.isMonster && e.getProcByType(ProcMonster.class) == null) {
			//e.addProc(new ProcMonster(new ChaseAndMeleeTactic()));
			ProcMonster pm = new ProcMonster();
			e.addProc(pm);
			if (p.tacticLoader != null) {
				p.tacticLoader.apply(e);
			} else {
				pm.tactic = new ChaseAndMeleeTactic();
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
