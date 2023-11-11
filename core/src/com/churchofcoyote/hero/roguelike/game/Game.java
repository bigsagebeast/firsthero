package com.churchofcoyote.hero.roguelike.game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.churchofcoyote.hero.module.RoguelikeModule;
import com.churchofcoyote.hero.roguelike.world.Bestiary;
import com.churchofcoyote.hero.roguelike.world.Creature;
import com.churchofcoyote.hero.roguelike.world.Dungeon;
import com.churchofcoyote.hero.roguelike.world.Level;
import com.churchofcoyote.hero.roguelike.world.LevelTransition;
import com.churchofcoyote.hero.roguelike.world.Terrain;
import com.churchofcoyote.hero.util.Point;

public class Game {
	// current level
	private static Level level;
	private static Player player;
	private static RoguelikeModule module;
	public static Dungeon dungeon = new Dungeon();
	public static Bestiary bestiary = new Bestiary();
	
	public Game(RoguelikeModule module) {
		//level = new Level(60, 60);
		player = new Player();
		Creature pc = bestiary.create("player", null);
		player.creature = pc;
		Game.module = module;
		dungeon.generateFromFile("start", "start.fhm");
		dungeon.generateFromFile("cave-entry", "cave-entry.fhm");
		dungeon.generateFromFile("cave", "cave.fhm");
		changeLevel(dungeon.getLevel("start"), new Point(30, 30));
		level = dungeon.getLevel("start");
	}
	
	public void changeLevel(Level nextLevel, Point playerPos) {
		if (level != null) {
			level.removeCreature(player.creature);
		}
		level = nextLevel;
		level.addCreature(player.creature);
		player.creature.pos = playerPos;
	}
	
	public static Level getLevel() {
		return level;
	}
	
	public static Player getPlayer() {
		return player;
	}
	
	public static Creature getPlayerCreature() {
		for (Creature c : level.getCreatures()) {
			if (player.isCreature(c)) {
				return c;
			}
		}
		return null;
	}
	
	public static void feelMsg(Creature creature, String message) {
		if (player.isCreature(creature)) {
			//emitMessage(message);
		}
	}
	
	public void turn() {
		int delayReduction = player.creature.delay;
		for (Creature c : level.getCreatures()) {
			c.delay -= delayReduction;
		}
		while (true) {
			module.redraw();
			List<Creature> initiativeOrder = new ArrayList<Creature>(level.getCreatures()); 
			Collections.sort(initiativeOrder, new InitiativeOrderComparator());
			Creature actor = initiativeOrder.get(0);
			if (player.isCreature(actor)) {
				break;
			}
			actor.npcAct();
		}
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
		LevelTransition transition = level.findTransition("up", player.creature.pos);
		if (transition == null) {
			announce("You can't go up here.");
		} else {
			changeLevel(Game.dungeon.getLevel(transition.destination), transition.arrival);
		}
	}
	
	public void cmdStairsDown() {
		LevelTransition transition = level.findTransition("down", player.creature.pos);
		if (transition == null) {
			announce("You can't go down here.");
		} else {
			changeLevel(Game.dungeon.getLevel(transition.destination), transition.arrival);
		}
	}
	
	public void cmdWait() {
		player.creature.tookTurn(1000);
	}
	
	public static void cmdMoveBy(int dx, int dy) {
		int tx = player.creature.pos.x + dx;
		int ty = player.creature.pos.y + dy;
		
		Creature targetCreature = level.creatureAt(tx, ty);
		if (targetCreature != null) {
			if (targetCreature.isPeacefulToPlayer(player)) {
				announce("Moved into a " +
						(targetCreature.isPeacefulToPlayer(player) ? "peaceful" : "hostile") +
						" creature (" + targetCreature.getVisibleName(player) + ").");
			} else {
				CombatLogic.swing(player.creature, targetCreature);
				player.creature.tookTurn(1000);
				// happen every tick?
				if (targetCreature.dead) {
					level.removeCreature(targetCreature);
				}
			}
			return;
		}
		
		if (level.cell(tx, ty).terrain.isPassable()) {
			//announce("Walked one square.");
			player.creature.tookTurn(1000);
			movePlayer(tx, ty);
		} else {
			if (level.cell(tx, ty).terrain == Terrain.BLANK) {
				announce("You can't go that way.");
			} else {
				announce("You bump into " + level.cell(tx, ty).terrain.getDescription() + ".");
			}
		}
	}
	
	public static void npcMoveBy(Creature c, int dx, int dy) {
		int tx = c.pos.x + dx;
		int ty = c.pos.y + dy;
		
		c.tookTurn(1000);
		
		Creature targetCreature = level.creatureAt(tx, ty);
		if (targetCreature != null && player.isCreature(targetCreature)) {
			if (targetCreature.isPeacefulToPlayer(player)) {
				announce("Was moved into by a " +
						(targetCreature.isPeacefulToPlayer(player) ? "peaceful" : "hostile") +
						" creature (" + c.getVisibleName(player) + ").");
			} else {
				CombatLogic.swing(c, targetCreature);
				
				// check if player is dead
			}
			return;
		} else {
			if (Game.canMove(c, dx, dy)) {
				moveNpc(c, tx, ty);
			}
		}
	}
	
	private static void movePlayer(int tx, int ty) {
		player.creature.pos = new Point(tx, ty);
		
		//announce("Now standing at " + tx + ", " + ty + ".");
	}
	
	private static void moveNpc(Creature c, int tx, int ty) {
		c.pos = new Point(tx, ty);
	}
	
	public static boolean canMove(Creature c, int dx, int dy) {
		int tx = c.pos.x + dx;
		int ty = c.pos.y + dy;
		return canMoveTo(c, tx, ty);
	}
	
	public static boolean canMoveTo(Creature c, int tx, int ty) {
		if (level.creatureAt(tx, ty) != null) {
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
		module.announce(s);
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
