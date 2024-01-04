package com.bigsagebeast.hero.module;

import com.badlogic.gdx.Input.Keys;
import com.bigsagebeast.hero.roguelike.game.GameSpecials;
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
import com.bigsagebeast.hero.util.Fov;
import com.bigsagebeast.hero.util.Util;

import java.util.ArrayList;
import java.util.HashSet;

public class RoguelikeModule extends Module {

	public static final int FONT_SIZE = 16;

	static final float ZOOM_MIN = 1.0f;
	static final float ZOOM_MAX = 2.0f;
	static final float ZOOM_PER_MWHEEL = -0.1f;

	private float zoom = 1.0f;

	public Game game;

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
		game = new Game(this);
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
		// Failed when announcing an elemeng being drawn on the first turn
		if (announceWindow != null) {
			announceWindow.addLine(Util.capitalize(s));
		}
	}

	public void unannounce() {
		announceWindow.unannounce();
	}
	
	
	private void process() {
		// main window
		Level level = game.getLevel();

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
					if (Fov.canSee(level, e.pos, target.pos, 15)) {
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
		equipWindow.update();
		hitPointWindow.update();
		spellPointWindow.update();
		experienceWindow.update();
		statBarWindow.update();

		Game.getLevel().updateVis();
	}

	public void setDirty() {
		dirty = true;
	}
	
	@Override
	public void update(GameState state) {
		if (dirty) {
			redraw();
			process();
		}
		while (game.hasLongTask()) {
			game.turn();
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
		GameLoop.glyphEngine.dirty();
		if (!shift && !ctrl && !alt) {
			switch (keycode) {
				case Keys.LEFT:
				case Keys.NUMPAD_4:
					game.cmdMoveLeft();
					break;
				case Keys.RIGHT:
				case Keys.NUMPAD_6:
					game.cmdMoveRight();
					break;
				case Keys.UP:
				case Keys.NUMPAD_8:
					game.cmdMoveUp();
					break;
				case Keys.DOWN:
				case Keys.NUMPAD_2:
					game.cmdMoveDown();
					break;
				case Keys.HOME:
				case Keys.NUMPAD_7:
					game.cmdMoveUpLeft();
					break;
				case Keys.END:
				case Keys.NUMPAD_1:
					game.cmdMoveDownLeft();
					break;
				case Keys.PAGE_UP:
				case Keys.NUMPAD_9:
					game.cmdMoveUpRight();
					break;
				case Keys.PAGE_DOWN:
				case Keys.NUMPAD_3:
					game.cmdMoveDownRight();
					break;
				case Keys.UNKNOWN:
				case Keys.NUMPAD_5:
				case Keys.PERIOD:
					game.cmdWait();
					break;
				case Keys.C:
					game.cmdChat();
					break;
				case Keys.D:
					game.cmdDrop();
					break;
				case Keys.E:
					game.cmdEat();
					break;
				case Keys.G:
					game.cmdPickUp();
					break;
				case Keys.L:
					game.cmdLook();
					break;
				case Keys.M:
					game.cmdMagic();
					break;
				case Keys.I:
					game.cmdInventory();
					break;
				case Keys.O:
					game.cmdOpen();
					break;
				case Keys.P:
					game.cmdPray();
					break;
				case Keys.Q:
					game.cmdQuaff();
					break;
				case Keys.R:
					game.cmdRead();
					break;
				case Keys.T:
					game.cmdTarget();
					break;
				case Keys.W:
					game.cmdWield();
					break;
				case Keys.ENTER:
					popupCommands();
			}
		}
		if (shift) {
			switch (keycode) {
				case Keys.LEFT:
				case Keys.NUMPAD_4:
					game.cmdLongWalk(Compass.WEST);
					break;
				case Keys.RIGHT:
				case Keys.NUMPAD_6:
					game.cmdLongWalk(Compass.EAST);
					break;
				case Keys.UP:
				case Keys.NUMPAD_8:
					game.cmdLongWalk(Compass.NORTH);
					break;
				case Keys.DOWN:
				case Keys.NUMPAD_2:
					game.cmdLongWalk(Compass.SOUTH);
					break;
				case Keys.HOME:
				case Keys.NUMPAD_7:
					game.cmdLongWalk(Compass.NORTH_WEST);
					break;
				case Keys.END:
				case Keys.NUMPAD_1:
					game.cmdLongWalk(Compass.SOUTH_WEST);
					break;
				case Keys.PAGE_UP:
				case Keys.NUMPAD_9:
					game.cmdLongWalk(Compass.NORTH_EAST);
					break;
				case Keys.PAGE_DOWN:
				case Keys.NUMPAD_3:
					game.cmdLongWalk(Compass.SOUTH_EAST);
					break;
				case Keys.NUMPAD_5:
					game.cmdRest();
				case Keys.L:
					game.cmdLoad();
					break;
				case Keys.R:
					game.cmdRegenerate();
					break;
				case Keys.S:
					game.cmdSave();
					break;
				case Keys.PERIOD:
					game.cmdStairsDown();
					break;
				case Keys.COMMA:
					game.cmdStairsUp();
					break;
				case Keys.BACKSLASH:
					Game.getPlayerEntity().receiveItem(Game.itempedia.create("scroll.magic.map", 100));
					Game.getPlayerEntity().receiveItem(Game.itempedia.create("scroll.identify", 100));
					break;
				case Keys.LEFT_BRACKET:
					GameSpecials.wishSummon();
					break;
				case Keys.RIGHT_BRACKET:
					GameSpecials.wish();
					break;
			}
		}
		game.turn();
		return true;
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
		box.addItem("m            Cast a magic spell", "m");
		box.addItem("o            Open or close a door", "o");
		box.addItem("p            Pray", "p");
		box.addItem("q            Quaff a potion", "q");
		box.addItem("r            Read a scroll or book", "r");
		box.addItem("t            Target ranged attack", "t");
		box.addItem("w            Wear or wield", "w");
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
				game.cmdWait();
				break;
			case "~":
				game.cmdRest();
				break;
			case "c":
				game.cmdChat();
				break;
			case "d":
				game.cmdDrop();
				break;
			case "e":
				game.cmdEat();
				break;
			case "g":
				game.cmdPickUp();
				break;
			case "i":
				game.cmdInventory();
				break;
			case "l":
				game.cmdLook();
				break;
			case "m":
				game.cmdMagic();
				break;
			case "o":
				game.cmdOpen();
				break;
			case "p":
				game.cmdPray();
				break;
			case "q":
				game.cmdQuaff();
				break;
			case "r":
				game.cmdRead();
				break;
			case "t":
				game.cmdTarget();
				break;
			case "w":
				game.cmdWield();
				break;
			case "<":
				game.cmdStairsUp();
				break;
			case ">":
				game.cmdStairsDown();
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
