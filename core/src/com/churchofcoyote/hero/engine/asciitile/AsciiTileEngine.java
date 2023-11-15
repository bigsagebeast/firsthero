package com.churchofcoyote.hero.engine.asciitile;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.churchofcoyote.hero.GameLogic;
import com.churchofcoyote.hero.GameState;
import com.churchofcoyote.hero.Graphics;
import com.churchofcoyote.hero.GraphicsState;
import com.churchofcoyote.hero.HeroGame;
import com.churchofcoyote.hero.module.RoguelikeModule;
import com.churchofcoyote.hero.text.TextBlock;

public class AsciiTileEngine implements GameLogic {

	private List<AsciiGrid> layers;
	
	private TextBlock[][][] blockLayers;
	private FrameBuffer buffer;
	private TextureRegion texRegion;
	
	public AsciiTileEngine() {
		layers = new ArrayList<AsciiGrid>();
	}
	
	public void addLayer(int width, int height) {
		layers.add(new AsciiGrid(width, height));
		blockLayers = new TextBlock[layers.size()][][];
	}
	
	public AsciiGrid getLayer(int index) {
		return layers.get(index);
	}
	
	@Override
	public void update(GameState state) {
		// Glyph effects are not updated here.
		// The entity owning the glyphs is responsible for that. 
	}

	private boolean isDirty() {
		for (int l = 0; l < layers.size(); l++) {
			if (layers.get(l).isDirty()) {
				return true;
			}
		}
		return false;
	}
	
	private void compile() {
		long start = System.currentTimeMillis();
		for (int l = 0; l < layers.size(); l++) {
			AsciiGrid layer = layers.get(l);
			
			if (layer.isDirty()) {
				float fontSize = RoguelikeModule.FONT_SIZE;
				blockLayers[l] = new TextBlock[layer.height()][];
				for (int y = 0; y < layer.height(); y++) {
					blockLayers[l][y] = new TextBlock[layer.width()];
					for (int x=0; x<layer.width(); x++) {
						Glyph glyph = layer.get(x,  y);
						TextBlock temp = 
						new TextBlock(
								"" + glyph.getSymbol(), fontSize,
								(float)x, (float)y, glyph.getColor(),
								-1f, -1f, null, null);
						
						blockLayers[l][y][x] = temp;
						// TODO: Effects
					}
				}
				layer.markClean();
			}
		}
		HeroGame.updateTimer("gCom", System.currentTimeMillis() - start);
	}
	
	@Override
	public void render(Graphics g, GraphicsState gState) {
		/*
		compile();
		
		for (int l=0; l<layers.size(); l++) {
			AsciiGrid layer = layers.get(l);
			for (int y=0; y<layer.height(); y++) {
				for (int x=0; x<layer.width(); x++) {
					blockLayers[l][y][x].render(g, gState);
				}
			}
		}
		*/
		
		if (buffer == null || isDirty()) {
			long start = System.currentTimeMillis();
			if (buffer != null) {
				buffer.dispose();
			}
			buffer = new FrameBuffer(Format.RGB565, Graphics.WIDTH, Graphics.HEIGHT, false);
			texRegion = new TextureRegion(buffer.getColorBufferTexture(), 0, 0, Graphics.WIDTH, Graphics.HEIGHT);
			texRegion.flip(false, true);
			
			compile();
			g.endBatch();
			buffer.begin();
			g.startBatch();
			for (int l=0; l<layers.size(); l++) {
				AsciiGrid layer = layers.get(l);
				for (int y=0; y<layer.height(); y++) {
					for (int x=0; x<64; x++) {
						blockLayers[l][y][x].render(g, gState);
					}
				}
			}
			g.endBatch();
			buffer.end();
			g.startBatch();
			HeroGame.updateTimer("gDrw", System.currentTimeMillis() - start);
		}
		g.batch().draw(texRegion, 0, 0, g.WIDTH, g.HEIGHT);
	}

	public static float getTilePixelX(int x, int y) {
		return (x + 1.3f) * RoguelikeModule.FONT_SIZE;
	}

	public static float getTilePixelY(int x, int y) {
		return Graphics.HEIGHT - ((y + 1.6f) * RoguelikeModule.FONT_SIZE);
	}
}
