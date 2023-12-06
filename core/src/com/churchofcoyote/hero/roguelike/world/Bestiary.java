package com.churchofcoyote.hero.roguelike.world;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import com.churchofcoyote.hero.glyphtile.Palette;
import com.churchofcoyote.hero.glyphtile.PaletteEntry;
import com.churchofcoyote.hero.roguelike.game.Game;
import com.churchofcoyote.hero.roguelike.game.Rank;
import com.churchofcoyote.hero.roguelike.world.ai.ChaseAndMeleeTactic;
import com.churchofcoyote.hero.roguelike.world.ai.RangedAmmoThenMeleeTactic;
import com.churchofcoyote.hero.roguelike.world.proc.*;
import com.churchofcoyote.hero.roguelike.world.proc.monster.ProcMonster;
import com.churchofcoyote.hero.roguelike.world.proc.monster.ProcShooter;

public class Bestiary {
	public static Map<String, Phenotype> map = new HashMap<String, Phenotype>();
	
	public Bestiary() {
		Phenotype door = new Phenotype();

		Phenotype pc = new Phenotype();
		Phenotype goblinLackey = new Phenotype();
		Phenotype goblinArcher = new Phenotype();
		Phenotype goblinWarrior = new Phenotype();
		Phenotype farmer = new Phenotype();
		Phenotype jackalTrained = new Phenotype();
		Phenotype wolf = new Phenotype();
		Phenotype skeleton = new Phenotype();
		Phenotype zombie = new Phenotype();
		Phenotype fungusRed = new Phenotype();
		Phenotype fungusGreenFunglet = new Phenotype();

		pc.name = "yourself";
		pc.hitPoints = 20;
		pc.spellPoints = 20;
		pc.stats = Rank.B_PLUS;
		pc.isMonster = false;
		pc.bodyPlan = "humanoid";
		pc.glyphName = "player.farmer";
		pc.isManipulator = true;
		pc.paletteEntry = new PaletteEntry(Palette.COLOR_WHITE, Palette.COLOR_BROWN, Palette.COLOR_YELLOW);
		pc.threat = -1;
		pc.naturalWeaponDamage = 3;
		pc.naturalWeaponToHit = 0;
		pc.naturalArmorClass = 7;

		goblinLackey.name = "sea-withered goblin lackey";
		goblinLackey.peaceful = false;
		goblinLackey.hitPoints = 8;
		goblinLackey.stats = Rank.C_MINUS;
		goblinLackey.isMonster = true;
		goblinLackey.bodyPlan = "humanoid";
		goblinLackey.glyphName = "humanoid.goblin.grunt";
		goblinLackey.isManipulator = true;
		goblinLackey.paletteEntry = new PaletteEntry(Palette.COLOR_DARKGREEN, Palette.COLOR_RED, Palette.COLOR_BROWN);
		goblinLackey.experienceAwarded = 10;
		goblinLackey.threat = 1;
		goblinLackey.naturalWeaponDamage = 4;
		goblinLackey.naturalWeaponToHit = -2;
		goblinLackey.naturalArmorClass = 2;

		goblinWarrior.name = "sea-withered goblin warrior";
		goblinWarrior.peaceful = false;
		goblinWarrior.hitPoints = 16;
		goblinWarrior.stats = Rank.C_MINUS;
		goblinWarrior.isMonster = true;
		goblinWarrior.bodyPlan = "humanoid";
		goblinWarrior.glyphName = "humanoid.goblin.warrior";
		goblinWarrior.isManipulator = true;
		goblinWarrior.paletteEntry = new PaletteEntry(Palette.COLOR_DARKGREEN, Palette.COLOR_RED, Palette.COLOR_BROWN);
		goblinWarrior.experienceAwarded = 20;
		goblinWarrior.threat = 2;
		goblinWarrior.naturalWeaponDamage = 6;
		goblinWarrior.naturalWeaponToHit = 1;
		goblinWarrior.naturalArmorClass = 5;

		goblinArcher.name = "sea-withered goblin archer";
		goblinArcher.peaceful = false;
		goblinArcher.hitPoints = 8;
		goblinArcher.stats = Rank.C_MINUS;
		goblinArcher.isMonster = true;
		goblinArcher.bodyPlan = "humanoid";
		goblinArcher.glyphName = "humanoid.goblin.archer";
		goblinArcher.isManipulator = true;
		goblinArcher.paletteEntry = new PaletteEntry(Palette.COLOR_DARKGREEN, Palette.COLOR_RED, Palette.COLOR_BROWN);
		goblinArcher.experienceAwarded = 15;
		goblinArcher.threat = 1;
		goblinArcher.naturalWeaponDamage = 3;
		goblinArcher.naturalWeaponToHit = -2;
		goblinArcher.naturalRangedWeaponDamage = 0;
		goblinArcher.naturalRangedWeaponToHit = -1;
		goblinArcher.naturalArmorClass = 0;
		goblinArcher.setup.add(e -> {
			e.addProc(new ProcShooter(e, "arrow"));
			e.addProc(new ProcMonster(e, new RangedAmmoThenMeleeTactic(10)));
		});

		jackalTrained.name = "trained jackal";
		jackalTrained.peaceful = false;
		jackalTrained.hitPoints = 6;
		jackalTrained.stats = Rank.C_MINUS;
		jackalTrained.isMonster = true;
		jackalTrained.bodyPlan = "humanoid";
		jackalTrained.glyphName = "animal.canine";
		jackalTrained.isManipulator = false;
		jackalTrained.paletteEntry = new PaletteEntry(Palette.COLOR_TAN, Palette.COLOR_BROWN, Palette.COLOR_RED);
		jackalTrained.experienceAwarded = 5;
		jackalTrained.threat = 1;
		jackalTrained.naturalWeaponDamage = 3;
		jackalTrained.naturalWeaponToHit = -2;
		jackalTrained.naturalArmorClass = 5;
		jackalTrained.packSize = 4;
		jackalTrained.packSpawnArea = 3;

		wolf.name = "wolf";
		wolf.peaceful = false;
		wolf.hitPoints = 20;
		wolf.stats = Rank.C_MINUS;
		wolf.isMonster = true;
		wolf.bodyPlan = "humanoid";
		wolf.glyphName = "animal.canine";
		wolf.isManipulator = false;
		wolf.paletteEntry = new PaletteEntry(Palette.COLOR_GRAY, Palette.COLOR_WHITE, Palette.COLOR_RED);
		wolf.experienceAwarded = 40;
		wolf.threat = 3;
		wolf.naturalWeaponDamage = 8;
		wolf.naturalWeaponToHit = 1;
		wolf.naturalArmorClass = 5;


		skeleton.name = "skeleton";
		skeleton.peaceful = false;
		skeleton.hitPoints = 8;
		skeleton.stats = Rank.C_MINUS;
		skeleton.isMonster = true;
		skeleton.bodyPlan = "humanoid";
		skeleton.glyphName = "undead.skeleton";
		skeleton.isManipulator = true;
		skeleton.paletteEntry = new PaletteEntry(Palette.COLOR_WHITE, Palette.COLOR_RED, Palette.COLOR_BROWN);
		skeleton.experienceAwarded = 15;
		skeleton.threat = 1;
		skeleton.naturalWeaponDamage = 6;
		skeleton.naturalWeaponToHit = 0;
		skeleton.naturalArmorClass = 4;

		zombie.name = "zombie";
		zombie.peaceful = false;
		zombie.hitPoints = 20;
		zombie.stats = Rank.C_MINUS;
		zombie.isMonster = true;
		zombie.bodyPlan = "humanoid";
		zombie.glyphName = "undead.zombie";
		zombie.isManipulator = true;
		zombie.paletteEntry = new PaletteEntry(Palette.COLOR_DARKGREEN, Palette.COLOR_BROWN, Palette.COLOR_TAN);
		zombie.experienceAwarded = 20;
		zombie.moveCost = 2000;
		zombie.threat = 2;
		zombie.naturalWeaponDamage = 12;
		zombie.naturalWeaponToHit = -4;
		zombie.naturalArmorClass = 0;

		fungusRed.name = "red fungus";
		fungusRed.peaceful = false;
		fungusRed.hitPoints = 25;
		fungusRed.stats = Rank.C_MINUS;
		fungusRed.isMonster = true;
		fungusRed.bodyPlan = "humanoid";
		fungusRed.glyphName = "plant.fungus";
		fungusRed.isManipulator = false;
		fungusRed.paletteEntry = new PaletteEntry(Palette.COLOR_RED, Palette.COLOR_TAN, Palette.COLOR_BROWN);
		fungusRed.experienceAwarded = 15;
		fungusRed.moveCost = 20000;
		fungusRed.threat = 2;
		fungusRed.naturalWeaponDamage = 0;
		fungusRed.naturalWeaponToHit = -5;
		fungusRed.naturalArmorClass = -5;
		fungusRed.setup.add((e) -> {e.addProc(new ProcBurningTouch(e, 3, 3, 0));});

		fungusGreenFunglet.name = "green funglet";
		fungusGreenFunglet.peaceful = false;
		fungusGreenFunglet.hitPoints = 10;
		fungusGreenFunglet.stats = Rank.C_MINUS;
		fungusGreenFunglet.isMonster = true;
		fungusGreenFunglet.bodyPlan = "humanoid";
		fungusGreenFunglet.glyphName = "plant.fungus";
		fungusGreenFunglet.isManipulator = false;
		fungusGreenFunglet.paletteEntry = new PaletteEntry(Palette.COLOR_DARKGREEN, Palette.COLOR_LIGHTGREEN, Palette.COLOR_BROWN);
		fungusGreenFunglet.experienceAwarded = 5;
		fungusGreenFunglet.moveCost = 20000;
		fungusGreenFunglet.threat = 0;
		fungusGreenFunglet.naturalWeaponDamage = 0;
		fungusGreenFunglet.naturalWeaponToHit = -5;
		fungusGreenFunglet.naturalArmorClass = -10;
		fungusGreenFunglet.packSize = 2;
		fungusGreenFunglet.packSpawnArea = 9;

		farmer.name = "Farmer";
		farmer.peaceful = true;
		farmer.hitPoints = 10;
		farmer.stats = Rank.D;
		farmer.isMonster = false;
		farmer.bodyPlan = "humanoid";
		farmer.glyphName = "creature.humanoid";
		farmer.isManipulator = true;
		farmer.paletteEntry = new PaletteEntry(Palette.COLOR_YELLOW, Palette.COLOR_TAN, Palette.COLOR_BROWN);
		farmer.experienceAwarded = 10;
		farmer.threat = -1;

		map.put("player", pc);
		map.put("goblin.lackey", goblinLackey);
		map.put("goblin.warrior", goblinWarrior);
		map.put("goblin.archer", goblinArcher);
		map.put("jackal.trained", jackalTrained);
		map.put("wolf", wolf);
		map.put("skeleton", skeleton);
		map.put("zombie", zombie);
		map.put("fungus.red", fungusRed);
		map.put("fungus.greenfunglet", fungusGreenFunglet);
		map.put("farmer", farmer);
	}

	public Entity create(String key) {
		return create(key, null);
	}

	public Entity create(String key, String name) {
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
		//e.glyph = p.glyph;
		e.stats = p.stats;
		e.moveCost = p.moveCost;
		e.body = new Body(p.bodyPlan);
		e.glyphName = p.glyphName;
		e.palette = p.paletteEntry;
		e.isManipulator = p.isManipulator;
		e.experienceAwarded = p.experienceAwarded;
		e.naturalWeaponDamage = p.naturalWeaponDamage;
		e.naturalWeaponToHit = p.naturalWeaponToHit;
		e.naturalArmorClass = p.naturalArmorClass;
		e.naturalArmorThickness = p.naturalArmorThickness;

		if (p.moveCost == 0) throw new RuntimeException("Bad move cost");
		for (Consumer<Entity> consumer : p.setup) {
			consumer.accept(e);
		}
		e.addProc(new ProcTimedEffects(e));

		if (key.equals("player")) {
			e.addProc(new ProcPlayer(e));
		}
		else if (p.isMonster && e.getProcByType(ProcMonster.class) == null) {
			e.addProc(new ProcMonster(e, new ChaseAndMeleeTactic()));
			//e.addProc(new PropPopupOnSeen(e, "It's a monster!"));
		} else {
			ProcMover pm = new ProcMover(e);
			pm.setDelay(Game.random.nextInt(e.moveCost));
			e.addProc(pm);
			//e.addProc(new PropPopupOnSeen(e, "It's a creature!"));
		}

		return e;
	}

	public static Phenotype get(String name) {
		return map.get(name);
	}
}
