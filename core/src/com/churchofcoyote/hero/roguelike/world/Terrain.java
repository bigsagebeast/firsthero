package com.churchofcoyote.hero.roguelike.world;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.badlogic.gdx.graphics.Color;
import com.churchofcoyote.hero.engine.asciitile.Glyph;

public class Terrain {
	private static final Random random = new Random();
	private static final long big1 = 10000;
	private static final long big2 = 10000;

	private List<Glyph> glyphs;
	private String description;
	private boolean passable;

	public static Map<String, Terrain> map;
	
	public static Terrain BLANK = new Terrain(Glyph.BLANK, "Empty.", false);
	
	static {
		// TODO: Load / initialize from...somewhere
		map = new HashMap<String, Terrain>();
		
		map.put("tree", new Terrain("T", Color.GREEN, "a tree", false));
		map.put("dirt", new Terrain("..,,", Color.FIREBRICK, "dirt", true));
		map.put("grass", new Terrain("..,,`'", Color.FOREST, "grass", true));
		map.put("wall", new Terrain("#", Color.LIGHT_GRAY, "a wall", false));
		map.put("floor", new Terrain("_", Color.BROWN, "wood floor", true));
		map.put("mountain", new Terrain("^^#", Color.LIGHT_GRAY, "the mountainside", false));
		
		map.put("upstair", new Terrain("<", Color.WHITE, "stairs leading up", true));
		map.put("downstair", new Terrain(">", Color.WHITE, "stairs leading down", true));
	}
	
	public Terrain(String symbols, Color color, String description, boolean passable) {
		glyphs = new ArrayList<Glyph>();
		for (int i=0; i<symbols.length(); i++) {
			glyphs.add(new Glyph(symbols.charAt(i), color));
		}
		this.description = description;
		this.passable = passable;
	}
	
	public Terrain(List<Glyph> glyphs, String description, boolean passable) {
		this.glyphs = glyphs;
		this.description = description;
		this.passable = passable;
	}
	
	public Terrain(Glyph glyph, String description, boolean passable) {
		this.glyphs = new ArrayList<Glyph>();
		glyphs.add(glyph);
		this.description = description;
		this.passable = passable;
	}
	
	public Glyph getGlyphForTile(int x, int y, int extra) {
		if (glyphs.size() == 1) {
			return glyphs.get(0);
		}
		
		random.setSeed((x * big1 * big2) + (y * big1) + extra);
		return glyphs.get(random.nextInt(glyphs.size()));
	}
	
	public String getDescription() {
		return description;
	}
	
	public boolean isPassable() {
		return passable;
	}
	
}
