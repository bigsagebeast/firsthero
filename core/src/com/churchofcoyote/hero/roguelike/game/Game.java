package com.churchofcoyote.hero.roguelike.game;

import com.churchofcoyote.hero.GameLoop;
import com.churchofcoyote.hero.SetupException;
import com.churchofcoyote.hero.module.RoguelikeModule;
import com.churchofcoyote.hero.persistence.Persistence;
import com.churchofcoyote.hero.persistence.PersistentProfile;
import com.churchofcoyote.hero.roguelike.world.*;
import com.churchofcoyote.hero.roguelike.world.dungeon.Level;
import com.churchofcoyote.hero.roguelike.world.proc.Proc;
import com.churchofcoyote.hero.roguelike.world.proc.ProcMover;
import com.churchofcoyote.hero.roguelike.world.proc.environment.ProcDoor;
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
	public static long time = 0;
	public static long lastTurnProc = 0;
	public static Random random = new Random();
	public static int ONE_TURN = 1000;

	private Inventory inventory = new Inventory();
	
	public Game(RoguelikeModule module) {
		try {
			BodyPlanpedia.initialize();

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
		player.getEntity().inventoryIds.add(shortsword.entityId);
		player.getEntity().inventoryIds.add(longsword.entityId);
		player.getEntity().inventoryIds.add(buckler.entityId);
		player.getEntity().inventoryIds.add(dagger.entityId);
		dungeon.generateFromFile("start", "start.fhm");
		dungeon.generateFromFile("cave-entry", "cave-entry.fhm");
		dungeon.generateFromFile("cave", "cave.fhm");
		changeLevel(dungeon.getLevel("start"), new Point(31, 46));
		level.addEntity(pitchfork);
		pitchfork.pos = new Point(35, 43);
		level = dungeon.getLevel("start");
	}

	public void startCaves() {
		Entity pc = bestiary.create("player");
		Entity dagger = itempedia.create("dagger");
		pc.equip(dagger, BodyPart.PRIMARY_HAND);
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
			for (Proc p : level.getProcEntities()) {
				if (p.hasAction()) {
					p.nextAction = p.nextAction - Game.time;
				}
			}
			level.removeEntity(player.getEntity());
		}

		level = nextLevel;
		Game.time = 0;
		Game.lastTurnProc = 0;
		level.addEntity(player.getEntity());
		player.getEntity().pos = playerPos;

		GameLoop.glyphEngine.initializeLevel(level);
	}

	public void changeLevel(Level nextLevel) {
		level = nextLevel;
		GameLoop.glyphEngine.initializeLevel(level);
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
				for (Proc p : level.getProcEntities()) {
					p.turnPassed();
				}
			}

			long lowestTurn = -1;
			long secondLowestTurn = -1;
			Proc lowestProc = null;
			for (Proc p : level.getProcEntities()) {
				if (p.nextAction != -1 && (lowestTurn == -1 || p.nextAction < lowestTurn)) {
					lowestTurn = p.nextAction;
					lowestProc = p;
				} else if (p.nextAction != -1 && (secondLowestTurn == -1 || p.nextAction < secondLowestTurn)) {
					secondLowestTurn = p.nextAction;
				}
			}
			time = lowestTurn;
			if (lowestProc == player.getEntity().getMover()) {
				break;
			}
			lowestProc.act();
		}
	}

	public boolean pickup(Entity actor, Entity target) {
		boolean canBePickedUp = false;
		for (Proc p : target.procs) {
			Boolean attempt = p.preBePickedUp(actor);
			if (attempt == null) {
				continue;
			} else if (attempt == true) {
				canBePickedUp = true;
			} else {
				return false;
			}
		}

		if (!canBePickedUp) {
			return false;
		}

		boolean canDoPickup = false;
		for (Proc p : actor.procs) {
			Boolean attempt = p.preDoPickup(target);
			if (attempt == null) {
				continue;
			} else if (attempt == true) {
				canDoPickup = true;
			} else {
				return false;
			}
		}

		if (!canDoPickup) {
			return false;
		}

		announce("You pick up the " + target.name);

		level.removeEntity(target);
		actor.inventoryIds.add(target.entityId);

		for (Proc p : actor.procs) {
			p.postDoPickup(target);
		}
		for (Proc p : target.procs) {
			p.postBePickedUp(actor);
		}
		return true;
	}

	public void cmdMoveLeft() {
		cmdMoveBy(-1, 0);
	}
	
	public void cmdMoveRight() {
		cmdMoveBy(+1, 0);
	}
	
	public void cmdMoveUp() {
		cmdMoveBy(0, -1);
	}
	
	public void cmdMoveDown() {
		cmdMoveBy(0, +1);
	}
	
	public void cmdMoveUpLeft() {
		cmdMoveBy(-1, -1);
	}
	
	public void cmdMoveDownLeft() {
		cmdMoveBy(-1, +1);
	}
	
	public void cmdMoveDownRight() {
		cmdMoveBy(+1, +1);
	}
	
	public void cmdMoveUpRight() {
		cmdMoveBy(+1, -1);
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
		for (Entity e : itemsHere) {
			// TODO move this announce into pickup
			if (!pickup(player.getEntity(), e)) {
				announce("You can't pick up the " + e.name + ".");
			}
		}
	}

	public void cmdWait() {
		player.getEntity().getMover().setDelay(ONE_TURN);
	}

	public void cmdWield() {
		inventory.doWield();
	}

	public void cmdInventory() {
		inventory.openInventory();
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
		if (!somethingToHandle) {
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
		if (!somethingToHandle) {
			announce("There's nothing to close.");
		}
	}

	public static void cmdSave() {
		Persistence.saveLevel(level);
		Persistence.saveProfile();
	}

	public static void cmdLoad() {

	}

	public static void cmdMoveBy(int dx, int dy) {
		int tx = player.getEntity().pos.x + dx;
		int ty = player.getEntity().pos.y + dy;

		Entity targetCreature = level.moverAt(tx, ty);
		if (targetCreature != null) {
			ProcMover targetMover = targetCreature.getMover();
			if (targetMover.isPeacefulToPlayer()) {
				announce("Moved into a " +
						(targetMover.isPeacefulToPlayer() ? "peaceful" : "hostile") +
						" creature (" + targetCreature.getVisibleName() + ").");
			} else {
				Entity weaponPrimary = player.getEntity().body.getEquipment(BodyPart.PRIMARY_HAND);
				// TODO 2-weapon fighting: split into trySwing, doHit, doMiss
				CombatLogic.swing(player.getEntity(), targetCreature, weaponPrimary);
				player.getEntity().getMover().setDelay(player.getEntity().moveCost);
				// happen every tick?
				if (targetCreature.dead) {
					level.removeEntity(targetCreature);
					targetCreature.destroy();
				}
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
				announce("You bump into " + e.name + ".");
				return;
			}
		}

		//announce("Walked one square.");
		player.getEntity().getMover().setDelay(player.getEntity().moveCost);
		movePlayer(tx, ty);
	}

	public static void npcMoveBy(Entity actor, ProcMover pm, int dx, int dy) {
		int tx = actor.pos.x + dx;
		int ty = actor.pos.y + dy;
		
		pm.setDelay(actor.moveCost);
		
		//Entity targetCreature = level.moverAt(tx, ty);

		for (Entity target : level.getEntitiesOnTile(new Point(tx, ty))) {
			ProcDoor door = (ProcDoor)target.getProcByType(ProcDoor.class);
			if (door != null && !door.isOpen) {
				target.tryOpen(actor);
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
						" creature (" + actor.getVisibleName() + ").");
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

		for (Entity item : level.getItemsOnTile(player.getEntity().pos)) {
			announce("There is a " + item.name + " here.");
			for (Proc p : item.procs) {
				p.postBeSteppedOn(player.getEntity());
			}
		}

		//announce("Now standing at " + tx + ", " + ty + ".");
	}
	
	private static void moveNpc(Entity actor, int tx, int ty) {
		actor.pos = new Point(tx, ty);
		for (Entity item : level.getItemsOnTile(actor.pos)) {
			for (Proc p : item.procs) {
				p.postBeSteppedOn(actor);
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
				announceVis(e, target, "You bump into " + target.name + ".", e.name + " bumps into you.", e.name + " bumps into " + target.name + ".", null);
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
