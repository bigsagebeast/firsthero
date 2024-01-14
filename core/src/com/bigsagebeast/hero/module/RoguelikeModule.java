package com.bigsagebeast.hero.module;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.bigsagebeast.hero.enums.Beatitude;
import com.bigsagebeast.hero.roguelike.game.GameSpecials;
import com.bigsagebeast.hero.roguelike.world.Element;
import com.bigsagebeast.hero.roguelike.world.Itempedia;
import com.bigsagebeast.hero.roguelike.world.Spellpedia;
import com.bigsagebeast.hero.ui.*;
import com.bigsagebeast.hero.GameLoop;
import com.bigsagebeast.hero.GameState;
import com.bigsagebeast.hero.Graphics;
import com.bigsagebeast.hero.GraphicsState;
import com.bigsagebeast.hero.dialogue.DialogueBox;
import com.bigsagebeast.hero.roguelike.game.Game;
import com.bigsagebeast.hero.roguelike.game.MainWindow;
import com.bigsagebeast.hero.roguelike.world.Entity;
import com.bigsagebeast.hero.roguelike.world.dungeon.Level;
import com.bigsagebeast.hero.roguelike.world.proc.Proc;
import com.bigsagebeast.hero.text.TextBlock;
import com.bigsagebeast.hero.util.Compass;
import com.bigsagebeast.hero.util.Util;

import java.util.ArrayList;
import java.util.HashSet;

public class RoguelikeModule extends Module {

	public static final int FONT_SIZE = 16;

	static final float ZOOM_MIN = 1.0f;
	static final float ZOOM_MAX = 2.0f;
	static final float ZOOM_PER_MWHEEL = -0.1f;

	private float zoom = 1.0f;

	public static MainWindow mainWindow;
	public static AnnounceWindow announceWindow;
	public static EquipmentWindow equipWindow;
	public static HitPointWindow hitPointWindow;
	public static SpellPointWindow spellPointWindow;
	public static ExperienceWindow experienceWindow;
	public static StatBarWindow statBarWindow;

	private boolean dirty = true;
	
	public static TextBlock topBorder = null;

	public void initialize() {
	}

	@Override
	public void start() {
		super.start();

		mainWindow = new MainWindow();
		announceWindow = new AnnounceWindow();
		equipWindow = new EquipmentWindow();
		hitPointWindow = new HitPointWindow();
		spellPointWindow = new SpellPointWindow();
		experienceWindow = new ExperienceWindow();
		statBarWindow = new StatBarWindow();


		announceWindow.addLine("Announcements:");

		uiEngine.addBlock(announceWindow.getTextBlockParent());
//		uiEngine.addBlock(statsWindow.getTextBlockParent());
		uiEngine.addBlock(equipWindow.getTextBlockParent());
		uiEngine.addBlock(hitPointWindow.getTextBlockParent());
		uiEngine.addBlock(spellPointWindow.getTextBlockParent());
		uiEngine.addBlock(experienceWindow.getTextBlockParent());
		uiEngine.addBlock(statBarWindow.getTextBlockParent());
	}

	@Override
	public void end() {
		super.end();
		//mainWindow.close();
		announceWindow.close();
		equipWindow.close();
		hitPointWindow.close();
		spellPointWindow.close();
		experienceWindow.close();
		statBarWindow.close();
	}

	public void announce(String s) {
		// TODO this should already be initialized!
		// Failed when announcing an element being drawn on the first turn
		if (announceWindow != null) {
			announceWindow.addLine(Util.capitalize(s));
		}
	}

	public void announceLoud(String s) {
		// TODO this should already be initialized!
		if (announceWindow != null) {
			announceWindow.addLine(Util.capitalize(s), Color.YELLOW);
		}
	}

	public void unannounce() {
		announceWindow.unannounce();
	}
	
	
	private void process() {
		// main window
		Level level = Game.getLevel();

		for (Entity e : level.getNonMovers()) {
			int wx = e.pos.x - mainWindow.getCameraX();
			int wy = e.pos.y - mainWindow.getCameraY();
			if (wx < 0 || wx >= mainWindow.getWidth() || wy < 0 || wy >= mainWindow.getWidth()) {
				continue;
			}
			if (level.cell(e.pos.x, e.pos.y).visible()) {
				for (Proc p : e.procs) {
					p.actPlayerLos(e);
				}
			}
		}

		for (Entity e : level.getMovers()) {
			/*
			int wx = e.pos.x - mainWindow.getCameraX();
			int wy = e.pos.y - mainWindow.getCameraY();
			if (wx < 0 || wx >= mainWindow.getWidth() || wy < 0 || wy >= mainWindow.getWidth()) {
				continue;
			}
			 */
			if (level.cell(e.pos.x, e.pos.y).visible()) {
				for (Proc p : e.procs) {
					p.actPlayerLos(e);
				}
			}

			HashSet<Proc> moverLosProcs = new HashSet<>();
			for (Proc p : e.procs) {
				if (p.wantsMoverLos() == Boolean.TRUE) {
					moverLosProcs.add(p);
				}
			}
			ArrayList<Entity> visibleMovers = new ArrayList<>();
			if (!moverLosProcs.isEmpty()) {
				for (Entity target : level.getMovers()) {
					if (e == target)
						continue;
					if (e.canSee(target)) {
						visibleMovers.add(target);
					}
				}
			}
			for (Proc p : moverLosProcs) {
				p.handleMoverLos(e, visibleMovers);
			}
		}
	}
	
	public void redraw() {
		if (Game.getPlayerEntity() == null) {
			return;
		}
		if (equipWindow != null) {
			equipWindow.update();
			hitPointWindow.update();
			spellPointWindow.update();
			experienceWindow.update();
			statBarWindow.update();

			Game.getLevel().updateVis();
		}
	}

	public void setDirty() {
		dirty = true;
	}
	
	@Override
	public void update(GameState state) {
		if (dirty) {
			Game.getLevel().recalculateJitter();
			redraw();
			process();
		}
		while (Game.getPlayerEntity().isParalyzed()) {
			Game.passTime(Game.ONE_TURN);
			Game.turn();
			Game.getLevel().recalculateJitter();
			redraw();
			process();
		}
		while (Game.hasLongTask()) {
			Game.turn();
			Game.getLevel().recalculateJitter();
			redraw();
			process();
		}
		dirty = false;
	}

	@Override
	public void render(Graphics g, GraphicsState gState) {

	}

	@Override
	public boolean keyDown(int keycode, boolean shift, boolean ctrl, boolean alt) {
		if (Game.getLevel() == null) {
			return false;
		}
		GameLoop.glyphEngine.dirty();
		if (!shift && !ctrl && !alt) {
			switch (keycode) {
				case Keys.LEFT:
				case Keys.NUMPAD_4:
					Game.cmdMoveLeft();
					break;
				case Keys.RIGHT:
				case Keys.NUMPAD_6:
					Game.cmdMoveRight();
					break;
				case Keys.UP:
				case Keys.NUMPAD_8:
					Game.cmdMoveUp();
					break;
				case Keys.DOWN:
				case Keys.NUMPAD_2:
					Game.cmdMoveDown();
					break;
				case Keys.HOME:
				case Keys.NUMPAD_7:
					Game.cmdMoveUpLeft();
					break;
				case Keys.END:
				case Keys.NUMPAD_1:
					Game.cmdMoveDownLeft();
					break;
				case Keys.PAGE_UP:
				case Keys.NUMPAD_9:
					Game.cmdMoveUpRight();
					break;
				case Keys.PAGE_DOWN:
				case Keys.NUMPAD_3:
					Game.cmdMoveDownRight();
					break;
				case Keys.UNKNOWN:
				case Keys.NUMPAD_5:
				case Keys.PERIOD:
					Game.cmdWait();
					break;
				case Keys.C:
					Game.cmdChat();
					break;
				case Keys.D:
					Game.cmdDrop();
					break;
				case Keys.E:
					Game.cmdEat();
					break;
				case Keys.G:
					Game.cmdPickup();
					break;
				case Keys.L:
					Game.cmdLook();
					break;
				case Keys.Z:
					Game.cmdMagic();
					break;
				case Keys.I:
					Game.cmdInventory();
					break;
				case Keys.O:
					Game.cmdOpen();
					break;
				case Keys.P:
					Game.cmdPray();
					break;
				case Keys.Q:
					Game.cmdQuaff();
					break;
				case Keys.R:
					Game.cmdRead();
					break;
				case Keys.T:
					Game.cmdTarget();
					break;
				case Keys.W:
					Game.cmdWield();
					break;
				case Keys.ENTER:
				case Keys.NUMPAD_ENTER:
					popupCommands();
					break;
				case Keys.ESCAPE:
					popupEscapeMenu();
					break;
			}
		}
		if (shift && !ctrl && !alt) {
			switch (keycode) {
				case Keys.LEFT:
				case Keys.NUMPAD_4:
					Game.cmdLongWalk(Compass.WEST);
					break;
				case Keys.RIGHT:
				case Keys.NUMPAD_6:
					Game.cmdLongWalk(Compass.EAST);
					break;
				case Keys.UP:
				case Keys.NUMPAD_8:
					Game.cmdLongWalk(Compass.NORTH);
					break;
				case Keys.DOWN:
				case Keys.NUMPAD_2:
					Game.cmdLongWalk(Compass.SOUTH);
					break;
				case Keys.HOME:
				case Keys.NUMPAD_7:
					Game.cmdLongWalk(Compass.NORTH_WEST);
					break;
				case Keys.END:
				case Keys.NUMPAD_1:
					Game.cmdLongWalk(Compass.SOUTH_WEST);
					break;
				case Keys.PAGE_UP:
				case Keys.NUMPAD_9:
					Game.cmdLongWalk(Compass.NORTH_EAST);
					break;
				case Keys.PAGE_DOWN:
				case Keys.NUMPAD_3:
					Game.cmdLongWalk(Compass.SOUTH_EAST);
					break;
				case Keys.NUMPAD_5:
					Game.cmdRest();
				case Keys.L:
					Game.cmdLoad();
					break;
				case Keys.R:
					Game.cmdRegenerate();
					break;
				case Keys.S:
					Game.cmdSave();
					break;
				case Keys.PERIOD:
					Game.cmdStairsDown();
					break;
				case Keys.COMMA:
					Game.cmdStairsUp();
					break;
				case Keys.BACKSLASH:
					Game.getPlayerEntity().acquireWithStacking(Itempedia.create("scroll.magic.map", 100));
					Game.getPlayerEntity().acquireWithStacking(Itempedia.create("scroll.identify", 100));
					Game.getPlayer().gainStatElement(Element.FIRE, 99, 99);
					Game.getPlayer().gainStatElement(Element.WATER, 99, 99);
					Game.getPlayer().gainStatElement(Element.LIGHTNING, 99, 99);
					Game.getPlayer().gainStatElement(Element.NATURAE, 99, 99);
					for (String key : Spellpedia.keys()) {
						Game.spellbook.addSpell(key);
					}
					break;
				case Keys.LEFT_BRACKET:
					GameSpecials.wishSummon();
					break;
				case Keys.RIGHT_BRACKET:
					GameSpecials.wish();
					break;
			}
		}
		if (!shift && ctrl && !alt) {
			switch (keycode) {
				case Keys.RIGHT_BRACKET:
					for (String key : Itempedia.map.keySet()) {
						if (!Itempedia.map.get(key).isFeature) {
							int quantity = Itempedia.map.get(key).stackable ? 10 : 1;
							Entity itemEnt = Itempedia.create(key, quantity);
							itemEnt.identifyItemFully();
							Game.getPlayerEntity().acquireWithStacking(itemEnt);
						}
					}
			}
		}
		if (shift && ctrl && !alt) {
			switch (keycode) {
				case Keys.RIGHT_BRACKET:
					for (String key : Itempedia.map.keySet()) {
						if (!Itempedia.map.get(key).isFeature) {
							int quantity = Itempedia.map.get(key).stackable ? 10 : 1;
							Entity itemEnt = Itempedia.create(key, quantity);
							itemEnt.getItem().beatitude = Beatitude.CURSED;
							itemEnt.identifyItemFully();
							Game.getPlayerEntity().acquireWithStacking(itemEnt);
						}
					}
			}
		}
		if (!shift && ctrl && alt) {
			switch (keycode) {
				case Keys.RIGHT_BRACKET:
					for (String key : Itempedia.map.keySet()) {
						if (!Itempedia.map.get(key).isFeature) {
							int quantity = Itempedia.map.get(key).stackable ? 10 : 1;
							Entity itemEnt = Itempedia.create(key, quantity);
							itemEnt.getItem().beatitude = Beatitude.BLESSED;
							itemEnt.identifyItemFully();
							Game.getPlayerEntity().acquireWithStacking(itemEnt);
						}
					}
			}
		}
		Game.turn();
		return true;
	}

	public void popupEscapeMenu() {
		DialogueBox box = new DialogueBox()
				.withMargins(60, 60)
				.withFooterSelectable()
				.withCancelable(false)
				.withTitle("Menu");
		box.addItem("Return to Game", "r");
		box.addItem("Quit to main menu", "q");
		box.autoHeight();
		GameLoop.dialogueBoxModule.openDialogueBox(box, this::handleEscapeMenu);
	}

	public void handleEscapeMenu(Object o) {
		String response = (String)o;
		if (response.equals("q")) {
			end();
			GameLoop.titleModule.start();
		}
	}

	public void popupCommands() {
		DialogueBox box = new DialogueBox()
				.withMargins(60, 60)
				.withFooterClosableAndSelectable()
				.withTitle("Commands");
		box.addItem("Keypad       Move or attack", null);
		box.addItem("Keypad 5     Wait", "5");
		box.addItem("Shift KP 5   Rest", "~");
		box.addItem("c            Chat to an NPC", "c");
		box.addItem("d            Drop an item", "d");
		box.addItem("e            Eat something (including off the ground)", "e");
		box.addItem("g            Pick up an item", "g");
		box.addItem("i            Check inventory", "i");
		box.addItem("l            Look around", "l");
		box.addItem("o            Open or close a door", "o");
		box.addItem("p            Pray", "p");
		box.addItem("q            Quaff a potion", "q");
		box.addItem("r            Read a scroll or book", "r");
		box.addItem("t            Target ranged attack", "t");
		box.addItem("w            Wear or wield", "w");
		box.addItem("z            Cast a magic spell", "z");
		box.addItem("<            Go up stairs", "<");
		box.addItem(">            Go down stairs", ">");
		box.autoHeight();
		GameLoop.dialogueBoxModule.openDialogueBox(box, this::handlePopupCommands);
	}

	public void handlePopupCommands(Object o) {
		if (o == null) {
			return;
		}
		String key = (String)o;
		switch (key) {
			case "5":
				Game.cmdWait();
				break;
			case "~":
				Game.cmdRest();
				break;
			case "c":
				Game.cmdChat();
				break;
			case "d":
				Game.cmdDrop();
				break;
			case "e":
				Game.cmdEat();
				break;
			case "g":
				Game.cmdPickup();
				break;
			case "i":
				Game.cmdInventory();
				break;
			case "l":
				Game.cmdLook();
				break;
			case "o":
				Game.cmdOpen();
				break;
			case "p":
				Game.cmdPray();
				break;
			case "q":
				Game.cmdQuaff();
				break;
			case "r":
				Game.cmdRead();
				break;
			case "t":
				Game.cmdTarget();
				break;
			case "w":
				Game.cmdWield();
				break;
			case "z":
				Game.cmdMagic();
				break;
			case "<":
				Game.cmdStairsUp();
				break;
			case ">":
				Game.cmdStairsDown();
				break;
			default:
				break;
		}
	}

	@Override
	public boolean keyTyped(char key, boolean ctrl, boolean alt) {
		return true;
	}

	@Override
	public boolean scrolled(float distance) {
		float zoomBy = distance * ZOOM_PER_MWHEEL;
		zoom += zoomBy;
		zoom = Math.min(Math.max(zoom, ZOOM_MIN), ZOOM_MAX);
		GameLoop.glyphEngine.zoom(zoom);
		return true;
	}


	public void updateEquipmentWindow() {
		equipWindow.update();
	}
	
}
