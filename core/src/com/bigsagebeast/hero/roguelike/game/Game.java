package com.bigsagebeast.hero.roguelike.game;

import com.bigsagebeast.hero.GameLoop;
import com.bigsagebeast.hero.HeroGame;
import com.bigsagebeast.hero.SetupException;
import com.bigsagebeast.hero.enums.Gender;
import com.bigsagebeast.hero.module.RoguelikeModule;
import com.bigsagebeast.hero.module.TargetingModule;
import com.bigsagebeast.hero.persistence.Persistence;
import com.bigsagebeast.hero.persistence.PersistentProfile;
import com.bigsagebeast.hero.roguelike.world.*;
import com.bigsagebeast.hero.roguelike.world.dungeon.Level;
import com.bigsagebeast.hero.roguelike.world.dungeon.Room;
import com.bigsagebeast.hero.enums.Satiation;
import com.bigsagebeast.hero.roguelike.world.proc.Proc;
import com.bigsagebeast.hero.roguelike.world.proc.ProcMover;
import com.bigsagebeast.hero.roguelike.world.proc.environment.ProcDoor;
import com.bigsagebeast.hero.roguelike.world.proc.item.ProcItem;
import com.bigsagebeast.hero.roguelike.world.proc.item.ProcWeaponAmmo;
import com.bigsagebeast.hero.roguelike.world.proc.item.ProcWeaponRanged;
import com.bigsagebeast.hero.roguelike.world.proc.monster.ProcShooter;
import com.bigsagebeast.hero.util.Compass;
import com.bigsagebeast.hero.util.Point;
import com.bigsagebeast.hero.roguelike.world.BodyPart;
import com.bigsagebeast.hero.util.Util;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class Game {
	// current level
	private static Level level;
	private static Player player = new Player();
	public static RoguelikeModule roguelikeModule;
	public static final DungeonGenerator dungeon = new DungeonGenerator();
	public static final Bestiary bestiary = new Bestiary();
	public static final Itempedia itempedia = new Itempedia();
	public static final UnidMapping unidMapping = new UnidMapping();
	public static long time = 0;
	public static long lastTurnProc = 0;
	public static Random random = new Random();
	public static final int ONE_TURN = 1000;
	public static boolean initialized = false;

	public static int restTurns; // if > 0, we're waiting in place

	public static Spellbook spellbook = new Spellbook();

	public Game(RoguelikeModule module) {
		Game.roguelikeModule = module;
		initialize();
	}

	public void initialize() {
		time = 0;
		lastTurnProc = 0;
		if (!initialized) {
			initialized = true;
			try {
				BodyPlanpedia.initialize();
				unidMapping.scan();
				unidMapping.randomize();
				unidMapping.apply();
			} catch (SetupException e) {
				throw new RuntimeException(e);
			}
		}
	}

	public void startIntro() {
		Entity pc = bestiary.create("player");
		pc.recalculateSecondaryStats();
		player.setEntityId(pc.entityId);
		Entity pitchfork = itempedia.create("pitchfork");
		dungeon.generateFromFile("start", "start.fhm");
		dungeon.generateFromFile("cave-entry", "cave-entry.fhm");
		dungeon.generateFromFile("cave", "cave.fhm");
		changeLevel(dungeon.getLevel("start"), new Point(26, 27));
		level.addEntityWithStacking(pitchfork, new Point(29, 24));
		level = dungeon.getLevel("start");
		Level cave = dungeon.getLevel("cave");
		cave.addEntityWithStacking(itempedia.create("feature.intro.altar"), new Point(4, 3));
		level.prepare();
	}

	public void startAurex() {
		Entity pc = bestiary.create("player");
		pc.recalculateSecondaryStats();
		player.setEntityId(pc.entityId);
		dungeon.generateFromFile("aurex", "aurex.fhm");
		changeLevel(dungeon.getLevel("aurex"), new Point(107, 30));
		level.addEntityWithStacking(itempedia.create("feature.worldportal"), new Point(105, 30));
		level.prepare();
		if (!GameLoop.roguelikeModule.isRunning()) {
			GameLoop.roguelikeModule.start();
		}
	}


	public void handleStartCaves(Entity pc) {
		player.setEntityId(pc.entityId);
		dungeon.generateClassic("dungeon.1");
		changeLevel("dungeon.1", "out");
		level.prepare();
		if (!GameLoop.roguelikeModule.isRunning()) {
			GameLoop.roguelikeModule.start();
		}
	}

	public void startCaves() {
		CharacterBuilder cb = new CharacterBuilder(this::handleStartCaves);
		cb.begin();
	}

	public void changeLevel(Level nextLevel, Point playerPos) {
		if (level != null) {
			for (EntityProc tuple : level.getEntityProcs()) {
				if (tuple.proc.hasAction()) {
					tuple.proc.nextAction = tuple.proc.nextAction - Game.time;
				}
			}
			level.removeEntity(player.getEntity());
		}

		level = nextLevel;
		Game.time = 0;
		Game.lastTurnProc = 0;
		level.addEntityWithStacking(player.getEntity(), playerPos, false);

		GameLoop.glyphEngine.initializeLevel(level);
		level.prepare();
		passTime(0);
	}

	public void changeLevel(String toKey, String fromKey) {
		if (level != null) {
			for (EntityProc tuple : level.getEntityProcs()) {
				if (tuple.proc.hasAction()) {
					tuple.proc.nextAction = tuple.proc.nextAction - Game.time;
				}
			}
			level.removeEntity(player.getEntity());
		}

		Game.time = 0;
		Game.lastTurnProc = 0;
		Level nextLevel = dungeon.getLevel(toKey);
		level = nextLevel;
		Point playerPos = nextLevel.findTransitionTo(fromKey).loc;
		level.addEntityWithStacking(player.getEntity(), playerPos, false);
		level.prepare();

		GameLoop.glyphEngine.initializeLevel(level);
		passTime(0);
	}

	public void changeLevel(Level nextLevel) {
		level = nextLevel;
		GameLoop.glyphEngine.initializeLevel(level);
		level.prepare();
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

	public static Entity getPlayerEntity() {
		return player.getEntity();
	}

	public static void feelMsg(Entity entity, String message) {
		if (player.isEntity(entity)) {
			//emitMessage(message);
		}
	}
	
	public void turn() {
		HeroGame.resetTimer("astar");
		// at the start of the turn, the player has just acted
		for (Proc p : getPlayerEntity().procs) {
			p.onAction(getPlayerEntity());
		}

		// TODO maybe check state here?

		while (true) {
			roguelikeModule.setDirty();

			level.timePassed(time);

			// TODO getProcEntities needs a variant that gets ALL procs including on inventory,
			// maybe filtered by ones that have an 'act' or a 'turnPassed'
			while (lastTurnProc + ONE_TURN < time) {
				lastTurnProc += ONE_TURN;
				for (EntityProc tuple : level.getEntityProcs()) {
					tuple.proc.turnPassed(tuple.entity);
				}
			}

			long lowestTurn = -1;
			long secondLowestTurn = -1;
			EntityProc lowestProc = null;
			for (EntityProc tuple : level.getEntityProcs()) {
				if (tuple.proc.nextAction != -1 && (lowestTurn == -1 || tuple.proc.nextAction < lowestTurn)) {
					lowestTurn = tuple.proc.nextAction;
					lowestProc = tuple;
				} else if (tuple.proc.nextAction != -1 && (secondLowestTurn == -1 || tuple.proc.nextAction < secondLowestTurn)) {
					secondLowestTurn = tuple.proc.nextAction;
				}
			}
			time = lowestTurn;
			if (lowestProc.proc == player.getEntity().getMover()) {
				if (restTurns-- > 0) {
					if (hasInterruption()) {
						announce("You are interrupted.");
						restTurns = 0;
						break;
					} else if (restTurns == 0) {
						announce("You finish resting.");
					}
				} else {
					break;
				}
			}
			lowestProc.proc.act(lowestProc.entity);
			for (Proc onActProc : lowestProc.entity.procs) {
				onActProc.onAction(lowestProc.entity);
			}
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
		} else if (transition.toMap.equals("out")) {
			announce("You can't leave the dungeon!");
		} else {
			if (transition.arrival != null) {
				changeLevel(Game.dungeon.getLevel(transition.toMap), transition.arrival);
			} else {
				changeLevel(transition.toMap, transition.fromMap);
			}
		}
	}
	
	public void cmdStairsDown() {
		LevelTransition transition = level.findTransition("down", player.getEntity().pos);
		if (transition == null) {
			announce("You can't go down here.");
		} else {
			if (transition.arrival != null) {
				changeLevel(Game.dungeon.getLevel(transition.toMap), transition.arrival);
			} else {
				changeLevel(transition.toMap, transition.fromMap);
			}
		}
	}

	public void cmdPickUp() {
		List<Entity> itemsHere = level.getItemsOnTile(player.getEntity().pos);
		itemsHere = itemsHere.stream().filter(i -> !i.getItemType().isFeature).collect(Collectors.toList());
		if (itemsHere.isEmpty()) {
			announce("There's nothing here to pick up.");
			return;
		}
		if (itemsHere.size() == 1) {
			if (!player.getEntity().pickup(itemsHere.get(0))) {
				announce("You can't pick up " + itemsHere.get(0).getVisibleNameThe() + ".");
			} else {
				GameLoop.roguelikeModule.game.passTime(Game.ONE_TURN);
			}
		} else {
			Inventory.openFloorToGet();
		}
	}

	public void cmdWait() {
		if (player.getEntity().isConfused()) {
			playerCmdMoveBy(0, 0);
		} else {
			player.getEntity().getMover().setDelay(getPlayerEntity(), ONE_TURN);
		}
	}

	public void cmdRest() {
		if (hasInterruption()) {
			announce("You can't rest right now.");
		} else {
			restTurns = 50;
			player.getEntity().getMover().setDelay(getPlayerEntity(), ONE_TURN);
		}
	}

	public void cmdWield() {
		Inventory.doWield();
	}

	public void cmdQuaff() {
		Inventory.doQuaff();
	}

	public void cmdRead() {
		Inventory.doRead();
	}

	public void cmdInventory() {
		Inventory.openInventory();
	}

	public void cmdDrop() {
		Inventory.openInventoryToDrop();
	}

	public void cmdEat() {
		if (player.getSatiationStatus() == Satiation.STUFFED) {
			announce("You are too stuffed to eat!");
			return;
		}
		Inventory.openInventoryToEat();
	}

	public void cmdRegenerate() { startCaves(); }

	public void cmdOpen() {
		GameLoop.directionModule.begin("Open or close a door in what direction?", this::cmdOpenHandle);
	}

	public void cmdOpenHandle(Compass dir) {
		if (dir == Compass.OTHER) {
			Game.announce("Canceled.");
			return;
		}
		boolean confused = getPlayerEntity().isConfused();
		if (confused) {
			dir = Compass.randomDirection();
		}
		boolean somethingToHandle = false;
		Point targetPoint = dir.from(player.getEntity().pos);
		for (Entity target : level.getEntitiesOnTile(targetPoint)) {
			if (target.tryOpen(player.getEntity())) {
				somethingToHandle = true;
			}
		}
		if (somethingToHandle) {
			passTime(player.getEntity().moveCost);
		} else if (!confused) {
			announce("There's nothing there to open.");
		} else {
			announce("You fail to grasp a door!");
			passTime(player.getEntity().moveCost);
		}
	}

	public void cmdChat() {
		GameLoop.directionModule.begin("Chat in what direction?", this::cmdChatHandle);
	}

	public void cmdChatHandle(Compass dir) {
		if (dir == Compass.OTHER) {
			Game.announce("Canceled.");
			return;
		}
		Point targetPoint = dir.from(player.getEntity().pos);
		for (Entity target : level.getEntitiesOnTile(targetPoint)) {
			// TODO
			if (target.getMover() != null) {
				String chatPage = Bestiary.get(target.phenotypeName).chatPage;
				if (chatPage == null) {
					Game.announce(target.getVisibleNameThe() + " won't talk to you.");
				} else {
					GameLoop.CHAT_MODULE.openStory(target);
				}
				passTime(player.getEntity().moveCost);
				return;
			}
		}
		Game.announce("There's nobody there.");
	}


	public static void cmdSave() {
		Persistence.saveLevel(level);
		Persistence.saveProfile();
	}

	public static void cmdLoad() {

	}

	public void cmdMagic() {
		spellbook.openSpellbookToCast();
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

	public void cmdPray() {
		boolean prayingAtProc = false;
		for (Entity e : getLevel().getEntitiesOnTile(getPlayerEntity().pos)) {
			for (Proc p : e.procs) {
				Boolean canPray = p.canPrayAt(e, getPlayerEntity());
				if (canPray == Boolean.TRUE) {
					prayingAtProc = true;
					p.prayAt(e, getPlayerEntity());
					break;
				}
			}
			if (prayingAtProc) {
				// only pray at the first prayable proc we find
				break;
			}
		}
		if (!prayingAtProc) {
			Game.announce("You pray.  Nothing happens yet.");
		}
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

		if (target != null) {
			CombatLogic.shoot(player.getEntity(), target, rangedWeapon, shotEntity);
		}
		if (lastShot) {
			announce("You're out of " + shotEntity.getVisiblePluralName() + ".");
		}

		level.addEntityWithStacking(shotEntity, targetPoint);
		passTime(player.getEntity().moveCost);
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
		if (getPlayerEntity().isConfused()) {
			Compass dir = Compass.randomDirection();
			dx = dir.getX();
			dy = dir.getY();
			Game.announce("You are confused!");
		}
		int tx = getPlayerEntity().pos.x + dx;
		int ty = getPlayerEntity().pos.y + dy;

		Entity targetCreature = level.moverAt(tx, ty);
		if (targetCreature != null) {
			ProcMover targetMover = targetCreature.getMover();
			if (targetMover.isPeacefulToPlayer(targetCreature)) {
				announce("You bump into " + targetCreature.getVisibleNameWithQuantity() + ". (Press 'c' to chat.)");
			} else {
				Entity weaponPrimary = player.getEntity().body.getEquipment(BodyPart.PRIMARY_HAND);
				// TODO 2-weapon fighting: split into trySwing, doHit, doMiss
				CombatLogic.swing(player.getEntity(), targetCreature, weaponPrimary);
				player.getEntity().getMover().setDelay(getPlayerEntity(), player.getEntity().moveCost);
			}
			return;
		}

		if (!level.cell(tx, ty).terrain.isPassable()) {
			if (level.cell(tx, ty).terrain.isBumpInto()) {
				announce("You bump into " + level.cell(tx, ty).terrain.getDescription() + ".");
			} else {
				announce("You can't go that way.");
			}
			if (player.getEntity().isConfused()) {
				player.getEntity().getMover().setDelay(getPlayerEntity(), player.getEntity().moveCost);
			}
			return;
		}

		for (Entity e : level.getEntitiesOnTile(new Point(tx, ty))) {
			if (e.isObstructive()) {
				announce("You bump into " + e.getVisibleNameThe() + ".");
				if (player.getEntity().isConfused()) {
					player.getEntity().getMover().setDelay(getPlayerEntity(), player.getEntity().moveCost);
				}
				return;
			}
		}

		movePlayer(tx, ty);

		if (level.cell(player.getEntity().pos).terrain.getName().equals("water")) {
			announce("You flail around in the water.");
			player.getEntity().getMover().setDelay(getPlayerEntity(), player.getEntity().moveCost * 4L);
		} else {
			player.getEntity().getMover().setDelay(getPlayerEntity(), player.getEntity().moveCost);
		}
	}

	public static boolean pushBy(Entity actor, int dx, int dy) {
		int tx = actor.pos.x + dx;
		int ty = actor.pos.y + dy;
		if (!level.cell(tx, ty).terrain.isPassable()) {
			return false;
		}
		for (Entity e : level.getEntitiesOnTile(new Point(tx, ty))) {
			if (e.isObstructive()) {
				return false;
			}
		}
		moveNpc(actor, tx, ty);
		return true;
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
			if (targetCreature.getMover().isPeacefulToPlayer(targetCreature)) {
				announce("Was moved into by a " +
						(targetCreature.getMover().isPeacefulToPlayer(targetCreature) ? "peaceful" : "hostile") +
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
		Point newPos = new Point(tx, ty);
		player.getEntity().pos = newPos;

		int oldRoomId = player.getEntity().roomId;
		int newRoomId = level.cell(newPos).roomId;
		if (player.getEntity().roomId != newRoomId) {
			Room oldRoom = oldRoomId < 0 ? null : level.rooms.get(oldRoomId);
			Room newRoom = newRoomId < 0 ? null : level.rooms.get(newRoomId);
			player.getEntity().changeRoom(oldRoom, newRoom);
		}

		List<Entity> items = level.getItemsOnTile(newPos);
		List<Entity> itemsToMention = new ArrayList<>(items);
		itemsToMention.removeIf(e -> e.getItemType().hideWalkOver);

		if (!items.isEmpty()) {
			if (!itemsToMention.isEmpty()) {
				StringBuilder listString = new StringBuilder();
				if (itemsToMention.size() == 1 && items.get(0).getItem().quantity == 1) {
					listString.append("There is ");
				} else {
					listString.append("There are ");
				}
				for (int i = 0; i < itemsToMention.size(); i++) {
					listString.append(itemsToMention.get(i).getVisibleNameSingularOrSpecific());
					if (itemsToMention.size() > 1 && i < itemsToMention.size() - 2) {
						listString.append(", ");
					}
					if (i == itemsToMention.size() - 2) {
						listString.append(" and ");
					}
				}
				listString.append(" here.");
				announce(listString.toString());
			}

			for (Entity item : items) {
				for (Proc p : item.procs) {
					p.postBeSteppedOn(item, player.getEntity());
				}
			}
		}

		for (Point adjacent : level.surroundingTiles(getPlayerEntity().pos)) {
			for (Entity e : level.getEntitiesOnTile(adjacent)) {
				e.entityProcs().forEach(ep -> ep.proc.onAdjacentToPlayer(e));
			}
		}
	}
	
	private static void moveNpc(Entity actor, int tx, int ty) {
		Point newPos = new Point(tx, ty);
		actor.pos = newPos;
		int oldRoomId = actor.roomId;
		int newRoomId = level.cell(newPos).roomId;
		if (actor.roomId != newRoomId) {
			Room oldRoom = oldRoomId < 0 ? null : level.rooms.get(oldRoomId);
			Room newRoom = newRoomId < 0 ? null : level.rooms.get(newRoomId);
			actor.changeRoom(oldRoom, newRoom);
		}
		for (Entity item : level.getItemsOnTile(actor.pos)) {
			for (Proc p : item.procs) {
				p.postBeSteppedOn(item, actor);
			}
		}
	}

	public static boolean canMoveBy(Entity actor, Compass dir) {
		return !isBlockedByTerrain(actor, actor.pos.x + dir.getX(), actor.pos.y + dir.getY()) &&
				!isBlockedByEntity(actor, actor.pos.x + dir.getX(), actor.pos.y + dir.getY());
	}

	public static boolean isBlockedByTerrain(Entity actor, int tx, int ty) {
		if (!level.cell(tx, ty).terrain.isPassable()) {
			return true;
		}
		switch (actor.ambulation) {
			case WALKING_ONLY:
				if (level.cell(tx, ty).terrain.getName().equals("water")) {
					return true;
				}
				break;
			case SWIMMING_ONLY:
				if (!level.cell(tx, ty).terrain.getName().equals("water")) {
					return true;
				}
				break;
		}
		return false;
	}

	public static boolean isBlockedByEntity(Entity actor, int tx, int ty) {
		if (level.moverAt(tx, ty) != null) {
			return true;
		}

		for (Entity target : level.getEntitiesOnTile(new Point(tx, ty))) {
			if (target != player.getEntity() && target.isManipulator) {
				if (target.isObstructiveToManipulators()) {
					return true;
				}
			}
			else if (target.isObstructive()) {
				return true;
			}
		}

		return false;
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
		roguelikeModule.announce(Util.capitalize(s));
	}

	public static void unannounce() {
		roguelikeModule.unannounce();
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
		Gender actorGender = actorEntity == null ? null : actorEntity.gender;
		Gender targetGender = targetEntity == null ? null : targetEntity.gender;
		Entity playerEntity = player.getEntity();
		if (playerEntity == actorEntity) {
			announce(Util.substitute(actor, actorGender, targetGender));
		} else if (playerEntity == targetEntity) {
			announce(Util.substitute(target, actorGender, targetGender));
		} else if ((actorEntity != null && playerEntity.canSee(actorEntity)) || (targetEntity != null && playerEntity.canSee(targetEntity))) {
			// TODO this might be a problem if you can't see the other actor or target?
			announce(Util.substitute(visible, actorGender, targetGender));
		} else if (actorEntity != null && playerEntity.canHear(actorEntity)){
			announce(Util.substitute(audible, actorGender, targetGender));
		}
	}

	public static boolean hasInterruption() {
		// TODO: Have a flag that can be set during a turn by things like starvation.
		if (getPlayerEntity().isConfused()) {
			return true;
		}
		for (Entity e : level.getMovers()) {
			if (e != getPlayerEntity() && !e.peaceful && getPlayerEntity().canSee(e)) {
				return true;
			}
		}
		return false;
	}
}
