package com.churchofcoyote.hero.roguelike.game;

import com.churchofcoyote.hero.GameLoop;
import com.churchofcoyote.hero.SetupException;
import com.churchofcoyote.hero.module.RoguelikeModule;
import com.churchofcoyote.hero.module.TargetingModule;
import com.churchofcoyote.hero.persistence.Persistence;
import com.churchofcoyote.hero.persistence.PersistentProfile;
import com.churchofcoyote.hero.roguelike.world.*;
import com.churchofcoyote.hero.roguelike.world.dungeon.Level;
import com.churchofcoyote.hero.roguelike.world.proc.*;
import com.churchofcoyote.hero.roguelike.world.proc.environment.ProcDoor;
import com.churchofcoyote.hero.roguelike.world.proc.item.ProcItem;
import com.churchofcoyote.hero.roguelike.world.proc.item.ProcWeaponAmmo;
import com.churchofcoyote.hero.roguelike.world.proc.item.ProcWeaponRanged;
import com.churchofcoyote.hero.roguelike.world.proc.monster.ProcShooter;
import com.churchofcoyote.hero.util.Compass;
import com.churchofcoyote.hero.util.Point;

import java.util.List;
import java.util.Random;

public class Game {
	// current level
	private static Level level;
	private static Player player = new Player();
	public static RoguelikeModule roguelikeModule;
	public static DungeonGenerator dungeon = new DungeonGenerator();
	public static Bestiary bestiary = new Bestiary();
	public static Itempedia itempedia = new Itempedia();
	public static UnidMapping unidMapping = new UnidMapping();
	public static long time = 0;
	public static long lastTurnProc = 0;
	public static Random random = new Random();
	public static int ONE_TURN = 1000;

	private Inventory inventory = new Inventory();
	
	public Game(RoguelikeModule module) {
		try {
			BodyPlanpedia.initialize();
			unidMapping.scan();
			unidMapping.randomize();
			unidMapping.apply();
		} catch (SetupException e) {
			throw new RuntimeException(e);
		}
		Game.roguelikeModule = module;
	}

	public void startIntro() {
		Entity pc = bestiary.create("player");
		Entity pitchfork = itempedia.create("pitchfork");
		Entity shortsword = itempedia.create("short sword");
		Entity longsword = itempedia.create("longsword");
		Entity dagger = itempedia.create("dagger");
		Entity buckler = itempedia.create("buckler");
		player.setEntityId(pc.entityId);
		player.getEntity().receiveItem(shortsword);
		player.getEntity().receiveItem(longsword);
		player.getEntity().receiveItem(buckler);
		player.getEntity().receiveItem(dagger);
		dungeon.generateFromFile("start", "start.fhm");
		dungeon.generateFromFile("cave-entry", "cave-entry.fhm");
		dungeon.generateFromFile("cave", "cave.fhm");
		changeLevel(dungeon.getLevel("start"), new Point(31, 46));
		level.addEntityWithStacking(pitchfork, new Point(35, 43));
		level = dungeon.getLevel("start");
	}

	public void startCaves() {
		Entity pc = bestiary.create("player");
		Entity dagger = itempedia.create("dagger");
		pc.equip(dagger, BodyPart.PRIMARY_HAND);
		Entity shortbow = itempedia.create("shortbow");
		pc.equip(shortbow, BodyPart.RANGED_WEAPON);
		Entity arrow = itempedia.create("arrow", 100);
		pc.equip(arrow, BodyPart.RANGED_AMMO);
		player.setEntityId(pc.entityId);
		dungeon.generateBrogue("dungeon1", 0);
		changeLevel(dungeon.getLevel("dungeon1"), dungeon.getLevel("dungeon1").findOpenTile());
		level = dungeon.getLevel("dungeon1");
		dungeon.generateBrogue("dungeon2", 1);
		Level level2 = dungeon.getLevel("dungeon2");
		dungeon.generateBrogue("dungeon3", 2);
		Level level3 = dungeon.getLevel("dungeon3");
		Point downStairs1 = level.findOpenTile();
		level.cell(downStairs1).terrain = Terrain.get("downstair");
		Point upStairs2 = level2.findOpenTile();
		level2.cell(upStairs2).terrain = Terrain.get("upstair");
		Point downStairs2 = level2.findOpenTile();
		level2.cell(downStairs2).terrain = Terrain.get("downstair");
		Point upStairs3 = level3.findOpenTile();
		level3.cell(upStairs3).terrain = Terrain.get("upstair");
		level.addTransition(new LevelTransition("down", downStairs1, "dungeon2", upStairs2));
		level2.addTransition(new LevelTransition("up", upStairs2, "dungeon1", downStairs1));
		level2.addTransition(new LevelTransition("down", downStairs2, "dungeon3", upStairs3));
		level3.addTransition(new LevelTransition("up", upStairs3, "dungeon2", downStairs2));
	}

	public void changeLevel(Level nextLevel, Point playerPos) {
		if (level != null) {
			for (EntityProc tuple : level.getProcEntities()) {
				if (tuple.proc.hasAction()) {
					tuple.proc.nextAction = tuple.proc.nextAction - Game.time;
				}
			}
			level.removeEntity(player.getEntity());
		}

		level = nextLevel;
		Game.time = 0;
		Game.lastTurnProc = 0;
		level.addEntityWithStacking(player.getEntity(), playerPos);

		GameLoop.glyphEngine.initializeLevel(level);
		passTime(0);
	}

	public void changeLevel(Level nextLevel) {
		level = nextLevel;
		GameLoop.glyphEngine.initializeLevel(level);
		passTime(0);
	}

	// TODO should specify a profile name or slot or something
	public void load() {
		PersistentProfile profile = Persistence.loadProfile();
		profile.load();
		Level loadedlevel = Persistence.loadLevel(profile.levelName);
		changeLevel(loadedlevel);
	}
	
	public static Level getLevel() {
		return level;
	}
	
	public static Player getPlayer() {
		return player;
	}

	// TODO this should be cached...
	public static Entity getPlayerEntity() {
		if (level == null) {
			return null;
		}
		for (Entity c : level.getEntities()) {
			if (player.isEntity(c)) {
				return c;
			}
		}
		return null;
	}

	public static void feelMsg(Entity entity, String message) {
		if (player.isEntity(entity)) {
			//emitMessage(message);
		}
	}
	
	public void turn() {
		while (true) {
			roguelikeModule.setDirty();

			level.timePassed(time);

			// TODO getProcEntities needs a variant that gets ALL procs including on inventory,
			// maybe filtered by ones that have an 'act' or a 'turnPassed'
			while (lastTurnProc + ONE_TURN < time) {
				lastTurnProc += ONE_TURN;
				for (EntityProc tuple : level.getProcEntities()) {
					tuple.proc.turnPassed(tuple.entity);
				}
			}

			long lowestTurn = -1;
			long secondLowestTurn = -1;
			EntityProc lowestProc = null;
			for (EntityProc tuple : level.getProcEntities()) {
				if (tuple.proc.nextAction != -1 && (lowestTurn == -1 || tuple.proc.nextAction < lowestTurn)) {
					lowestTurn = tuple.proc.nextAction;
					lowestProc = tuple;
				} else if (tuple.proc.nextAction != -1 && (secondLowestTurn == -1 || tuple.proc.nextAction < secondLowestTurn)) {
					secondLowestTurn = tuple.proc.nextAction;
				}
			}
			time = lowestTurn;
			if (lowestProc.proc == player.getEntity().getMover()) {
				break;
			}
			lowestProc.proc.act(lowestProc.entity);
		}
	}

	public void passTime(int delay) {
		getPlayerEntity().getMover().setDelay(getPlayerEntity(), delay);
		turn();
	}

	public void cmdMoveLeft() {
		playerCmdMoveBy(-1, 0);
	}
	
	public void cmdMoveRight() {
		playerCmdMoveBy(+1, 0);
	}
	
	public void cmdMoveUp() {
		playerCmdMoveBy(0, -1);
	}
	
	public void cmdMoveDown() {
		playerCmdMoveBy(0, +1);
	}
	
	public void cmdMoveUpLeft() {
		playerCmdMoveBy(-1, -1);
	}
	
	public void cmdMoveDownLeft() {
		playerCmdMoveBy(-1, +1);
	}
	
	public void cmdMoveDownRight() {
		playerCmdMoveBy(+1, +1);
	}
	
	public void cmdMoveUpRight() {
		playerCmdMoveBy(+1, -1);
	}
	
	public void cmdStairsUp() {
		LevelTransition transition = level.findTransition("up", player.getEntity().pos);
		if (transition == null) {
			announce("You can't go up here.");
		} else {
			changeLevel(Game.dungeon.getLevel(transition.destination), transition.arrival);
		}
	}
	
	public void cmdStairsDown() {
		LevelTransition transition = level.findTransition("down", player.getEntity().pos);
		if (transition == null) {
			announce("You can't go down here.");
		} else {
			changeLevel(Game.dungeon.getLevel(transition.destination), transition.arrival);
		}
	}

	public void cmdPickUp() {
		List<Entity> itemsHere = level.getItemsOnTile(player.getEntity().pos);
		if (itemsHere.isEmpty()) {
			return;
		}
		if (itemsHere.size() == 1) {
			if (!player.getEntity().pickup(itemsHere.get(0))) {
				announce("You can't pick up " + itemsHere.get(0).getVisibleNameThe() + ".");
			} else {
				GameLoop.roguelikeModule.game.passTime(Game.ONE_TURN);
			}
		} else {
			inventory.openFloorToGet();
		}
	}

	public void cmdWait() {
		player.getEntity().getMover().setDelay(getPlayerEntity(), ONE_TURN);
	}

	public void cmdWield() {
		inventory.doWield();
	}

	public void cmdQuaff() {
		inventory.doQuaff();
	}

	public void cmdRead() {
		inventory.doRead();
	}

	public void cmdInventory() {
		inventory.openInventory();
	}

	public void cmdDrop() {
		inventory.openInventoryToDrop();
	}

	public void cmdRegenerate() { startCaves(); }

	public void cmdOpen() {
		boolean somethingToHandle = false;
		for (Compass dir : Compass.points()) {
			Point targetPoint = dir.from(player.getEntity().pos);
			for (Entity target : level.getEntitiesOnTile(targetPoint)) {
				if (target.tryOpen(player.getEntity())) {
					somethingToHandle = true;
				}
			}
		}
		if (somethingToHandle) {
			player.getEntity().getMover().setDelay(getPlayerEntity(), player.getEntity().moveCost);
		} else {
			announce("There's nothing to open.");
		}

	}

	public void cmdClose() {
		boolean somethingToHandle = false;
		for (Compass dir : Compass.points()) {
			Point targetPoint = dir.from(player.getEntity().pos);
			for (Entity target : level.getEntitiesOnTile(targetPoint)) {
				if (target.tryClose(player.getEntity())) {
					somethingToHandle = true;
				}
			}
		}
		if (somethingToHandle) {
			player.getEntity().getMover().setDelay(getPlayerEntity(), player.getEntity().moveCost);
		} else {
			announce("There's nothing to close.");
		}
	}

	public static void cmdSave() {
		Persistence.saveLevel(level);
		Persistence.saveProfile();
	}

	public static void cmdLoad() {

	}

	public void cmdTarget() {
		Entity rangedWeapon = getPlayerEntity().body.getEquipment(BodyPart.RANGED_WEAPON);
		Entity rangedAmmo = getPlayerEntity().body.getEquipment(BodyPart.RANGED_AMMO);
		if (rangedWeapon == null || rangedAmmo == null) {
			announce("You need a ranged weapon and ammo equipped!");
			return;
		}
		ProcWeaponRanged pwr = (ProcWeaponRanged)(rangedWeapon.procs.stream().filter(p -> p.getClass() == ProcWeaponRanged.class).findFirst().orElse(null));
		ProcWeaponAmmo pwa = (ProcWeaponAmmo)(rangedAmmo.procs.stream().filter(p -> p.getClass() == ProcWeaponAmmo.class).findFirst().orElse(null));
		if (pwr == null || pwa == null) {
			throw new RuntimeException("Invalid ranged or ammo equipped");
		}

		int range = pwr.range;
		TargetingModule.TargetMode tm = GameLoop.targetingModule.new TargetMode(true, true, true, range);
		GameLoop.targetingModule.begin(tm, this::handleTarget);
	}

	public void handleTarget(Point targetPoint) {
		if (targetPoint == null) {
			announce("Cancelled.");
			return;
		}
		Entity target = level.moverAt(targetPoint.x, targetPoint.y);
		// Targeting yourself is like canceling your shot
		if (target == getPlayerEntity()) {
			announce("No target.");
			return;
		}

		Entity rangedWeapon = getPlayerEntity().body.getEquipment(BodyPart.RANGED_WEAPON);
		Entity rangedAmmo = getPlayerEntity().body.getEquipment(BodyPart.RANGED_AMMO);

		ProcItem pia = rangedAmmo.getItem();
		Entity shotEntity;
		boolean lastShot = false;
		if (pia.quantity == 1) {
			shotEntity = rangedAmmo;
			getPlayerEntity().body.putEquipment(BodyPart.RANGED_AMMO, null);
			lastShot = true;
		} else {
			shotEntity = rangedAmmo.split(1);
		}

		if (target != null && target != getPlayerEntity()) {
			CombatLogic.shoot(player.getEntity(), target, rangedWeapon, shotEntity);
			passTime(player.getEntity().moveCost);
		}
		if (lastShot) {
			announce("You're out of " + shotEntity.getVisiblePluralName() + ".");
		}

		level.addEntityWithStacking(shotEntity, targetPoint);
		GameLoop.targetingModule.animate(getPlayerEntity().pos, targetPoint);
	}

	public static void npcShoot(Entity actor, Point targetPoint) {
		Proc procShooter = actor.getProcByType(ProcShooter.class);
		String itemKeyAmmo = procShooter.provideProjectile();
		if (itemKeyAmmo == null) {
			// TODO debug message and abort safely?
			throw new RuntimeException("No ammo type for shooter: " + actor.name);
		}
		Entity oneAmmo = itempedia.create(itemKeyAmmo, 1);
		GameLoop.targetingModule.animate(actor.pos, targetPoint);
		npcShootConsequence(actor, targetPoint, oneAmmo);
	}

	// later support for passing this method to 'animate', so it gets called afterwards
	public static void npcShootConsequence(Entity actor, Point targetPoint, Entity ammo) {
		Entity rangedWeapon = actor.body.getEquipment(BodyPart.RANGED_WEAPON);
		Entity targetEntity = level.moverAt(targetPoint.x, targetPoint.y);

		if (targetPoint != null) {
			CombatLogic.shoot(actor, targetEntity, rangedWeapon, ammo);

		}
		actor.getMover().setDelay(actor, actor.moveCost);
		level.addEntityWithStacking(ammo, targetPoint);
	}

	public void cmdLook() {
		TargetingModule.TargetMode tm = GameLoop.targetingModule.new TargetMode(false, false, false, -1);
		GameLoop.targetingModule.begin(tm, null);
	}

	public static void playerCmdMoveBy(int dx, int dy) {
		int tx = player.getEntity().pos.x + dx;
		int ty = player.getEntity().pos.y + dy;

		Entity targetCreature = level.moverAt(tx, ty);
		if (targetCreature != null) {
			ProcMover targetMover = targetCreature.getMover();
			if (targetMover.isPeacefulToPlayer()) {
				announce("Moved into a " +
						(targetMover.isPeacefulToPlayer() ? "peaceful" : "hostile") +
						" creature (" + targetCreature.getVisibleNameWithQuantity() + ").");
			} else {
				Entity weaponPrimary = player.getEntity().body.getEquipment(BodyPart.PRIMARY_HAND);
				// TODO 2-weapon fighting: split into trySwing, doHit, doMiss
				CombatLogic.swing(player.getEntity(), targetCreature, weaponPrimary);
				player.getEntity().getMover().setDelay(getPlayerEntity(), player.getEntity().moveCost);
			}
			return;
		}

		if (!level.cell(tx, ty).terrain.isPassable()) {
			if (level.cell(tx, ty).terrain == Terrain.BLANK) {
				announce("You can't go that way.");
			} else {
				announce("You bump into " + level.cell(tx, ty).terrain.getDescription() + ".");
			}
			return;
		}

		for (Entity e : level.getEntitiesOnTile(new Point(tx, ty))) {
			if (e.isObstructive()) {
				announce("You bump into " + e.getVisibleNameThe() + ".");
				return;
			}
		}

		//announce("Walked one square.");
		player.getEntity().getMover().setDelay(getPlayerEntity(), player.getEntity().moveCost);
		movePlayer(tx, ty);
	}

	public static void npcMoveBy(Entity actor, ProcMover pm, int dx, int dy) {
		int tx = actor.pos.x + dx;
		int ty = actor.pos.y + dy;
		
		pm.setDelay(actor, actor.moveCost);
		
		//Entity targetCreature = level.moverAt(tx, ty);

		for (Entity target : level.getEntitiesOnTile(new Point(tx, ty))) {
			ProcDoor door = (ProcDoor)target.getProcByType(ProcDoor.class);
			if (door != null && !door.isOpen) {
				// TODO some creatures can destroy doors?
				if (target.isManipulator) {
					target.tryOpen(actor);
				} else {
					/*
					// TODO should depend on creature type; floating eyes shouldn't scratch at doors
					Game.announceVis(actor, actor, null, null,
							actor.getVisibleName() + " scratches at " + door.entity.getVisibleName() + ".",
							"You hear something scratching at a door.");
					 */
				}
				return;
			}
		}

		if (Game.tryMoveTo(actor, tx, ty)) {
			moveNpc(actor, tx, ty);
		}
	}

	public static void npcAttack(Entity actor, ProcMover pm, int dx, int dy) {
		int tx = actor.pos.x + dx;
		int ty = actor.pos.y + dy;

		Entity targetCreature = level.moverAt(tx, ty);
		if (targetCreature != null) {
			if (targetCreature.getMover().isPeacefulToPlayer()) {
				announce("Was moved into by a " +
						(targetCreature.getMover().isPeacefulToPlayer() ? "peaceful" : "hostile") +
						" creature (" + actor.getVisibleNameWithQuantity() + ").");
			} else {
				Entity weaponPrimary = actor.body.getEquipment(BodyPart.PRIMARY_HAND);
				// TODO 2-weapon fighting: split into trySwing, doHit, doMiss
				CombatLogic.swing(actor, targetCreature, weaponPrimary);

				// check if player is dead
			}
			return;
		}
	}
	
	private static void movePlayer(int tx, int ty) {
		player.getEntity().pos = new Point(tx, ty);

		List<Entity> items = level.getItemsOnTile(player.getEntity().pos);
		if (items.isEmpty()) {
			return;
		}
		StringBuilder listString = new StringBuilder();
		if (items.size() == 1 && items.get(0).getItem().quantity == 1) {
			listString.append("There is ");
		} else {
			listString.append("There are ");
		}
		for (int i=0; i<items.size(); i++) {
			listString.append(items.get(i).getVisibleNameSingularOrSpecific());
			if (items.size() > 1 && i < items.size() - 2) {
				listString.append(", ");
			}
			if (i == items.size() - 2) {
				listString.append(" and ");
			}
		}
		listString.append(" here.");
		announce(listString.toString());

		for (Entity item : items) {
			for (Proc p : item.procs) {
				p.postBeSteppedOn(item, player.getEntity());
			}
		}
	}
	
	private static void moveNpc(Entity actor, int tx, int ty) {
		actor.pos = new Point(tx, ty);
		for (Entity item : level.getItemsOnTile(actor.pos)) {
			for (Proc p : item.procs) {
				p.postBeSteppedOn(item, actor);
			}
		}
	}
	
	public static boolean canMoveTo(Entity actor, int tx, int ty) {
		if (level.moverAt(tx, ty) != null) {
			return false;
		}
		if (!level.cell(tx, ty).terrain.isPassable()) {
			return false;
		}

		for (Entity target : level.getEntitiesOnTile(new Point(tx, ty))) {
			if (target != player.getEntity() && target.isManipulator) {
				if (target.isObstructiveToManipulators()) {
					return false;
				}
			}
			else if (target.isObstructive()) {
				return false;
			}
		}

		return true;
	}

	public static boolean tryMoveTo(Entity e, int tx, int ty) {
		if (level.moverAt(tx, ty) != null) {
			return false;
		}
		if (!level.cell(tx, ty).terrain.isPassable()) {
			return false;
		}

		for (Entity target : level.getEntitiesOnTile(new Point(tx, ty))) {
			if (target.isObstructive()) {
				announceVis(e, target, "You bump into " + target.getVisibleNameThe() + ".",
						e.getVisibleNameThe() + " bumps into you.",
						e.getVisibleNameThe() + " bumps into " + target.getVisibleNameThe() + ".",
						null);
				return false;
			}
		}

		return true;
	}

	public static void announce(String s) {
		if (s == null) {
			return;
		}
		roguelikeModule.announce(s);
	}
	
	public static void announceVis(Visibility vis, String actor, String target, String visible, String audible) {
		switch (vis) {
		case ACTOR:
			announce(actor);
			return;
		case TARGET:
			announce(target);
			return;
		case VISIBLE:
			announce(visible);
			return;
		case AUDIBLE:
			announce(audible);
			return;
		case NONE:
			return;
		}
	}

	public static void announceVis(Entity actorEntity, Entity targetEntity, String actor, String target, String visible, String audible) {
		Entity playerEntity = player.getEntity();
		if (playerEntity == actorEntity) {
			announce(actor);
		} else if (playerEntity == targetEntity) {
			announce(target);
		} else if ((actorEntity != null && playerEntity.canSee(actorEntity)) || (targetEntity != null && playerEntity.canSee(targetEntity))) {
			// TODO this might be a problem if you can't see the other actor or target?
			announce(visible);
		} else if (actorEntity != null && playerEntity.canHear(actorEntity)){
			announce(audible);
		}
	}
}
