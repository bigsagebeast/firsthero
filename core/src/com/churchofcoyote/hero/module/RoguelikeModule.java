package com.churchofcoyote.hero.module;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.churchofcoyote.hero.GameLoop;
import com.churchofcoyote.hero.GameState;
import com.churchofcoyote.hero.Graphics;
import com.churchofcoyote.hero.GraphicsState;
import com.churchofcoyote.hero.engine.asciitile.AsciiGrid;
import com.churchofcoyote.hero.glyphtile.EntityGlyph;
import com.churchofcoyote.hero.glyphtile.GlyphIndex;
import com.churchofcoyote.hero.glyphtile.Palette;
import com.churchofcoyote.hero.glyphtile.PaletteEntry;
import com.churchofcoyote.hero.roguelike.game.AnnounceWindow;
import com.churchofcoyote.hero.roguelike.game.Game;
import com.churchofcoyote.hero.roguelike.game.EquipmentWindow;
import com.churchofcoyote.hero.roguelike.game.MainWindow;
import com.churchofcoyote.hero.roguelike.game.StatsWindow;
import com.churchofcoyote.hero.roguelike.world.Entity;
import com.churchofcoyote.hero.roguelike.world.dungeon.Level;
import com.churchofcoyote.hero.roguelike.world.proc.Proc;
import com.churchofcoyote.hero.roguelike.world.proc.ProcMover;
import com.churchofcoyote.hero.text.TextBlock;
import com.churchofcoyote.hero.util.Fov;
import com.churchofcoyote.hero.util.Point;

import java.util.ArrayList;
import java.util.HashSet;

public class RoguelikeModule extends Module {

	int mainWindowOffsetX = 1;
	int mainWindowOffsetY = 1;
	
	public static final int FONT_SIZE = 16;
	
	public Game game;
	
	MainWindow mainWindow;
	AnnounceWindow announceWindow;
	StatsWindow statsWindow;
	EquipmentWindow equipWindow;
	
	AsciiGrid mainGrid;
	
	private boolean dirty = true;
	
	public static TextBlock topBorder = null;
	
	@Override
	public void start() {
		super.start();

		game = new Game(this);
		mainWindow = new MainWindow();
		announceWindow = new AnnounceWindow();
		statsWindow = new StatsWindow();
		equipWindow = new EquipmentWindow();
		//asciiTileEngine.addLayer(110, 62);
		//mainGrid = asciiTileEngine.getLayer(0);
		
		TextBlock borderBlock = new TextBlock("", FONT_SIZE, 0, 0, new Color(0, 0, 0, 0));

		uiEngine.addBlock(new TextBlock("##############################################################################################################",
				FONT_SIZE, 0, 0, Color.GRAY));
		borderBlock.addChild(new TextBlock("##############################################################################################################",
				FONT_SIZE, 0, 61, Color.GRAY));
		for (int i=1; i<61; i++) {
			if (i != 21) {
				borderBlock.addChild(new TextBlock("#                                                            #                                               #",
						FONT_SIZE, 0, i, Color.GRAY));
			} else {
				borderBlock.addChild(new TextBlock("#                                                            #################################################",
						FONT_SIZE, 0, i, Color.GRAY));
			}
		}
		borderBlock.compile();
		uiEngine.addBlock(borderBlock);

		//uiEngine.addBlock(new TextBlock(EntityGlyph.getGlyph(Game.getPlayerEntity()), 20f, 20f, 20f, 0, 0, null, null));
		//uiEngine.addBlock(new TextBlock(GlyphIndex.get("player.farmer").create(new PaletteEntry(Palette.COLOR_WHITE, Palette.COLOR_RED, Palette.COLOR_WHITE)), 40f, 0f, 0f, 20, 20, null, null));

		announceWindow.addLine("Announcements:");
		//statsWindow.update(Game.getPlayerEntity());
		//equipWindow.update(Game.getPlayerEntity());
		//Game.getLevel().updateVis();

		uiEngine.addBlock(announceWindow.getTextBlockParent());
		uiEngine.addBlock(statsWindow.getTextBlockParent());
		uiEngine.addBlock(equipWindow.getTextBlockParent());

		//GameLoop.popupModule.createPopup("Pick up your weapon", 5f, Game.getPlayerEntity(), 0.75f);
	}
	
	
	public void announce(String s) {
		announceWindow.addLine(s);
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
					p.actPlayerLos();
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
					p.actPlayerLos();
				}
			}

			HashSet<Proc> moverLosProcs = new HashSet<>();
			for (Proc p : e.procs) {
				if (p.wantsMoverLos() == Boolean.TRUE) {
					moverLosProcs.add(p);
				}
			}
			ArrayList<ProcMover> visibleMovers = new ArrayList<>();
			if (!moverLosProcs.isEmpty()) {
				for (Entity target : level.getMovers()) {
					if (e == target)
						continue;
					if (Fov.canSee(level, e.pos, target.pos, 15, 0)) {
						//System.out.println("Can   see: " + e.name + ", " + target.name);
						visibleMovers.add(target.getMover());
					} else {
						//System.out.println("Can't see: " + e.name + ", " + target.name);
					}
				}
			}
			for (Proc p : moverLosProcs) {
				p.handleMoverLos(visibleMovers);
			}
		}
	}
	
	public void redraw() {
		if (Game.getPlayerEntity() == null) {
			return;
		}
		statsWindow.update(Game.getPlayerEntity());
		equipWindow.update(Game.getPlayerEntity());
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
			dirty = false;
		}
		//asciiTileEngine.update(state);
	}

	@Override
	public void render(Graphics g, GraphicsState gState) {
		//asciiTileEngine.render(g, gState);

	}

	@Override
	public boolean keyDown(int keycode, boolean shift, boolean ctrl, boolean alt) {
		GameLoop.glyphEngine.dirty();
		if (!shift && !ctrl && !alt) {
			switch (keycode) {
				case Keys.A:
					announceWindow.addLine("Adding an announcement line.");
					break;
				case Keys.LEFT:
					game.cmdMoveLeft();
					break;
				case Keys.RIGHT:
					game.cmdMoveRight();
					break;
				case Keys.UP:
					game.cmdMoveUp();
					break;
				case Keys.DOWN:
					game.cmdMoveDown();
					break;
				case Keys.HOME:
					game.cmdMoveUpLeft();
					break;
				case Keys.END:
					game.cmdMoveDownLeft();
					break;
				case Keys.PAGE_UP:
					game.cmdMoveUpRight();
					break;
				case Keys.PAGE_DOWN:
					game.cmdMoveDownRight();
					break;
				case Keys.COMMA:
					game.cmdPickUp();
					break;
				case Keys.UNKNOWN:
					game.cmdWait();
					break;
				case Keys.W:
					game.cmdWield();
					break;
				case Keys.I:
					game.cmdInventory();
					break;
				case Keys.O:
					game.cmdOpen();
					break;
				case Keys.C:
					game.cmdClose();
					break;
			}
		}
		if (shift) {
			switch (keycode) {
				case Keys.R:
					game.cmdRegenerate();
					break;
				case Keys.S:
					game.cmdSave();
					break;
				case Keys.L:
					game.cmdLoad();
					break;
				case Keys.PERIOD:
					game.cmdStairsDown();
					break;
				case Keys.COMMA:
					game.cmdStairsUp();
					break;
			}
		}
		game.turn();
		return true;
	}

	@Override
	public boolean keyTyped(char key, boolean ctrl, boolean alt) {
		return true;
	}

	public void updateEquipmentWindow() {
		equipWindow.update(Game.getPlayerEntity());
	}
	
}
