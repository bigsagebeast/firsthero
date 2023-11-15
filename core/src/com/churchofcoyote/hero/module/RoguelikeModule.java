package com.churchofcoyote.hero.module;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.churchofcoyote.hero.GameState;
import com.churchofcoyote.hero.Graphics;
import com.churchofcoyote.hero.GraphicsState;
import com.churchofcoyote.hero.engine.asciitile.AsciiGrid;
import com.churchofcoyote.hero.engine.asciitile.Glyph;
import com.churchofcoyote.hero.roguelike.game.AnnounceWindow;
import com.churchofcoyote.hero.roguelike.game.Game;
import com.churchofcoyote.hero.roguelike.game.EquipmentWindow;
import com.churchofcoyote.hero.roguelike.game.MainWindow;
import com.churchofcoyote.hero.roguelike.game.StatsWindow;
import com.churchofcoyote.hero.roguelike.world.Entity;
import com.churchofcoyote.hero.roguelike.world.Level;
import com.churchofcoyote.hero.roguelike.world.Terrain;
import com.churchofcoyote.hero.roguelike.world.proc.ProcEntity;
import com.churchofcoyote.hero.text.TextBlock;
import com.churchofcoyote.hero.util.Point;

public class RoguelikeModule extends Module {

	int mainWindowOffsetX = 1;
	int mainWindowOffsetY = 1;
	
	public static final int FONT_SIZE = 12;
	
	Game game;
	
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
		asciiTileEngine.addLayer(110, 62);
		mainGrid = asciiTileEngine.getLayer(0);
		
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
		/*
		TextBlock block = new TextBlock("##############################################################################################################",
				FONT_SIZE, 0, 0, Color.GRAY);
				*/
		//block.lock();
		//textEngine.addBlock(block);
		
		
		announceWindow.addLine("Announcements:");
		statsWindow.update(Game.getPlayerEntity());
		equipWindow.update(Game.getPlayerEntity());
		Game.getLevel().updateVis();

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
		if (!dirty) {
			return;
		}
		dirty = false;
		Level level = game.getLevel();
		for (int x=0; x<mainWindow.getWidth(); x++) {
			int ix = x + mainWindow.getCameraX();
			for (int y=0; y<mainWindow.getHeight(); y++) {
				int iy = y + mainWindow.getCameraY();
				if (level.cell(ix, iy).explored) {
					Terrain t = level.cell(ix, iy).terrain;
					Glyph tGlyph = t.getGlyphForTile(ix, iy, 0);
					if (!level.cell(ix, iy).visible()) {
						tGlyph = tGlyph.getShadow();
					}
					mainGrid.put(tGlyph, x + mainWindowOffsetX, y + mainWindowOffsetY);
				} else {
					mainGrid.put(Glyph.BLANK, x + mainWindowOffsetX, y + mainWindowOffsetY);
				}
			}
		}
		
		for (Entity c : level.getEntities()) {
			int wx = c.pos.x - mainWindow.getCameraX();
			int wy = c.pos.y - mainWindow.getCameraY();
			if (wx < 0 || wx >= mainWindow.getWidth() || wy < 0 || wy >= mainWindow.getWidth()) {
				continue;
			}
			if (level.cell(c.pos.x, c.pos.y).visible()) {
				mainGrid.put(c.glyph, wx + mainWindowOffsetX, wy + mainWindowOffsetY);
				for (ProcEntity pe : c.procs) {
					pe.actPlayerLos();
				}
			}
		}
	}
	
	public void redraw() {
		dirty = true;
		statsWindow.update(Game.getPlayerEntity());
		equipWindow.update(Game.getPlayerEntity());
		Game.getLevel().updateVis();
	}
	
	@Override
	public void update(GameState state) {
		process();
		//asciiTileEngine.update(state);

	}

	@Override
	public void render(Graphics g, GraphicsState gState) {
		//asciiTileEngine.render(g, gState);

	}

	@Override
	public boolean keyDown(int keycode, boolean shift, boolean ctrl, boolean alt) {
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
			case Keys.UNKNOWN:
				game.cmdWait();
				break;
			}
		}
		if (shift) {
			switch (keycode) {
			case Keys.P:
				game.getPlayerEntity().pos = new Point(59, 58);
				game.cmdMoveDown();
				break;
			case Keys.O:
				game.getPlayerEntity().pos = new Point(1, 2);
				game.cmdMoveUp();
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
	
}
