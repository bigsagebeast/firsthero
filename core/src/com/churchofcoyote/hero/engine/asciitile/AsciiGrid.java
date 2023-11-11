package com.churchofcoyote.hero.engine.asciitile;

public class AsciiGrid {
	private int width, height;
	
	private Glyph[][] glyph;
	
	// used by rendering engine
	private boolean dirty = true;
	
	public AsciiGrid() {
		this(60, 60);
	}
	
	public AsciiGrid(int width, int height) {
		this.width = width;
		this.height = height;
		initialize();
	}
	
	private void initialize() {
		glyph = new Glyph[height][];
		for (int i=0; i<height; i++) {
			glyph[i] = new Glyph[width];
		}
		clear();
	}
	
	public void clear() {
		for (int i=0; i<height; i++) {
			for (int j=0; j<width; j++) {
				glyph[i][j] = Glyph.BLANK; 
			}
		}
	}
	
	public Glyph get(int x, int y) {
		if (x < 0 || y < 0 || x >= width || y >= height) {
			return Glyph.BLANK;
		}
		return glyph[y][x];
	}
	
	public void put(Glyph g, int x, int y) {
		dirty = true;
		if (x < 0 || y < 0 || x >= width || y >= height) {
			return;
		}
		glyph[y][x] = g;
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
