package com.churchofcoyote.hero.roguelike.game;

import com.churchofcoyote.hero.SetupException;
import com.churchofcoyote.hero.module.RoguelikeModule;
import com.churchofcoyote.hero.roguelike.world.*;
import com.churchofcoyote.hero.roguelike.world.proc.Proc;
import com.churchofcoyote.hero.roguelike.world.proc.ProcEquippable;
import com.churchofcoyote.hero.roguelike.world.proc.ProcMover;
import com.churchofcoyote.hero.util.Point;

import java.util.List;

public class Game {
	// current level
	private static Level level;
	private static Player player;
	public static RoguelikeModule roguelikeModule;
	public static Dungeon dungeon = new Dungeon();
	public static Bestiary bestiary = new Bestiary();
	public static Itempedia itempedia = new Itempedia();
	public static long time = 0;
	
	public Game(RoguelikeModule module) {
		try {
			BodyPlanpedia.initialize();
		} catch (SetupException e) {
			throw new RuntimeException(e);
		}
		//level = new Level(60, 60);
		player = new Player();
		Entity pc = bestiary.create("player", null);
		Entity pitchfork = itempedia.create("pitchfork", null);
		player.entity = pc;
		Game.roguelikeModule = module;
		dungeon.generateFromFile("start", "start.fhm");
		dungeon.generateFromFile("cave-entry", "cave-entry.fhm");
		dungeon.generateFromFile("cave", "cave.fhm");
		changeLevel(dungeon.getLevel("start"), new Point(31, 46));
		level.addEntity(pitchfork);
		pitchfork.pos = new Point(35, 43);
		level = dungeon.getLevel("start");
	}
	
	public void changeLevel(Level nextLevel, Point playerPos) {
		if (level != null) {
			for (Proc p : level.getProcEntities()) {
				if (p.hasAction()) {
					p.nextAction = p.nextAction - Game.time;
				}
			}
			level.removeEntity(player.entity);
		}

		level = nextLevel;
		Game.time = 0;
		level.addEntity(player.entity);
		player.entity.pos = playerPos;

	}
	
	public static Level getLevel() {
		return level;
	}
	
	public static Player getPlayer() {
		return player;
	}
	
	public static Entity getPlayerEntity() {
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
			roguelikeModule.redraw();

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
			if (lowestProc == getPlayerEntity().getMover()) {
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
		actor.inventory.add(target);

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
		LevelTransition transition = level.findTransition("up", player.entity.pos);
		if (transition == null) {
			announce("You can't go up here.");
		} else {
			changeLevel(Game.dungeon.getLevel(transition.destination), transition.arrival);
		}
	}
	
	public void cmdStairsDown() {
		LevelTransition transition = level.findTransition("down", player.entity.pos);
		if (transition == null) {
			announce("You can't go down here.");
		} else {
			changeLevel(Game.dungeon.getLevel(transition.destination), transition.arrival);
		}
	}

	public void cmdPickUp() {
		List<Entity> itemsHere = level.getItemsOnTile(player.entity.pos);
		for (Entity e : itemsHere) {
			// TODO move this announce into pickup
			if (!pickup(player.entity, e)) {
				announce("You can't pick up the " + e.name + ".");
			}
		}
	}

	public void cmdWait() {
		player.entity.getMover().setDelay(1000);
	}

	public void cmdWield() {
		for (Entity e : player.entity.inventory) {
			ProcEquippable equippable = e.getEquippable();
			if (equippable == null)
				continue;
			if (equippable.equipmentFor == BodyPart.ANY_HAND) {
				player.entity.equip(e, BodyPart.PRIMARY_HAND);
			}
		}
		player.entity.getMover().setDelay(1000);
	}

	public static void cmdMoveBy(int dx, int dy) {
		int tx = player.entity.pos.x + dx;
		int ty = player.entity.pos.y + dy;

		Entity targetCreature = level.moverAt(tx, ty);
		if (targetCreature != null) {
			ProcMover targetMover = targetCreature.getMover();
			if (targetMover.isPeacefulToPlayer(player)) {
				announce("Moved into a " +
						(targetMover.isPeacefulToPlayer(player) ? "peaceful" : "hostile") +
						" creature (" + targetCreature.getVisibleName(player) + ").");
			} else {
				CombatLogic.swing(player.entity, targetCreature);
				player.entity.getMover().setDelay(1000);
				// happen every tick?
				if (targetCreature.dead) {
					level.removeEntity(targetCreature);
				}
			}
			return;
		}
		
		if (level.cell(tx, ty).terrain.isPassable()) {
			//announce("Walked one square.");
			player.entity.getMover().setDelay(1000);
			movePlayer(tx, ty);
		} else {
			if (level.cell(tx, ty).terrain == Terrain.BLANK) {
				announce("You can't go that way.");
			} else {
				announce("You bump into " + level.cell(tx, ty).terrain.getDescription() + ".");
			}
		}
	}
	
	public static void npcMoveBy(Entity e, ProcMover pm, int dx, int dy) {
		int tx = e.pos.x + dx;
		int ty = e.pos.y + dy;
		
		pm.setDelay(1000);
		
		Entity targetCreature = level.moverAt(tx, ty);
		if (targetCreature != null) {
			if (targetCreature.getMover().isPeacefulToPlayer(player)) {
				announce("Was moved into by a " +
						(targetCreature.getMover().isPeacefulToPlayer(player) ? "peaceful" : "hostile") +
						" creature (" + e.getVisibleName(player) + ").");
			} else {
				CombatLogic.swing(e, targetCreature);
				
				// check if player is dead
			}
			return;
		} else {
			if (Game.canMove(e, dx, dy)) {
				moveNpc(e, tx, ty);
			}
		}
	}
	
	private static void movePlayer(int tx, int ty) {
		player.entity.pos = new Point(tx, ty);

		for (Entity item : level.getItemsOnTile(player.entity.pos)) {
			announce("There is a " + item.name + " here.");
			for (Proc p : item.procs) {
				p.postBeSteppedOn(player.entity);
			}
		}

		//announce("Now standing at " + tx + ", " + ty + ".");
	}
	
	private static void moveNpc(Entity e, int tx, int ty) {
		e.pos = new Point(tx, ty);
		for (Entity item : level.getItemsOnTile(e.pos)) {
			for (Proc p : item.procs) {
				p.postBeSteppedOn(e);
			}
		}
	}
	
	public static boolean canMove(Entity e, int dx, int dy) {
		int tx = e.pos.x + dx;
		int ty = e.pos.y + dy;
		return canMoveTo(e, tx, ty);
	}
	
	public static boolean canMoveTo(Entity e, int tx, int ty) {
		if (level.moverAt(tx, ty) != null) {
			return false;
		}
		if (!level.cell(tx, ty).terrain.isPassable()) {
			return false;
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
}
