package com.bigsagebeast.hero.glyphtile;

import java.util.ArrayList;

public class GlyphGrid {
	private int width, height;

	private GlyphTile[][] glyph;
	private ArrayList<GlyphTile>[][] glyphBackground;
	private float[][] jitter;

	// used by rendering engine
	private boolean dirty = true;

	public boolean withinBounds(int x, int y) {
		return (x >= 0 && x < width && y >= 0 && y < height);
	}

	public GlyphGrid(int width, int height) {
		this.width = width;
		this.height = height;
		initialize();
	}

	@SuppressWarnings("unchecked")
	private void initialize() {
		glyph = new GlyphTile[width][];
		glyphBackground = new ArrayList[width][];
		jitter = new float[width][];
		for (int i=0; i<width; i++) {
			glyph[i] = new GlyphTile[height];
			glyphBackground[i] = new ArrayList[height];
			jitter[i] = new float[height];
			for (int j=0; j<height; j++) {
				glyphBackground[i][j] = new ArrayList<GlyphTile>();
			}
		}
		clear();
	}
	
	public void clear() {
		for (int i=0; i<width; i++) {
			for (int j=0; j<height; j++) {
				glyph[i][j] = GlyphTile.BLANK;
			}
		}
	}
	
	public GlyphTile get(int x, int y) {
		if (x < 0 || y < 0 || x >= width || y >= height) {
			return GlyphTile.BLANK;
		}
		return glyph[x][y];
	}

	public void setJitter(int x, int y, float val) {
		jitter[x][y] = val;
	}

	public float getJitter(int x, int y) {
		if (x < 0 || y < 0 || x >= width || y >= height) {
			return 0;
		}
		return jitter[x][y];
	}
	
	public void put(GlyphTile g, int x, int y) {
		dirty = true;
		if (x < 0 || y < 0 || x >= width || y >= height) {
			return;
		}
		glyph[x][y] = g;
	}

	public void addBackground(GlyphTile g, int x, int y) {
		dirty = true;
		if (x < 0 || y < 0 || x >= width || y >= height) {
			return;
		}
		glyphBackground[x][y].add(g);
	}

	public void clearBackground(int x, int y) {
		dirty = true;
		glyphBackground[x][y].clear();
	}

	public int width() {
		return width;
	}
	
	public int height() {
		return height;
	}
	
	public boolean isDirty() {
		return dirty;
	}
	
	public void markClean() {
		dirty = false;
	}
}
