package com.churchofcoyote.hero.roguelike.world;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.badlogic.gdx.graphics.Color;
import com.churchofcoyote.hero.engine.asciitile.Glyph;
import com.churchofcoyote.hero.glyphtile.Palette;
import com.churchofcoyote.hero.glyphtile.PaletteEntry;

public class Terrain {
	private static final Random random = new Random();
	private static final long big1 = 10000;
	private static final long big2 = 10000;

	private List<Glyph> glyphs;
	private String description;
	private boolean passable;

	private String glyphName;
	private PaletteEntry paletteEntry;
	private String blockCategory;

	public static Map<String, Terrain> map;
	
	public static Terrain BLANK = new Terrain("Empty.", false, "terrain.dot", new PaletteEntry(Palette.COLOR_GRAY, Palette.COLOR_BROWN, Palette.COLOR_BROWN, Palette.COLOR_TRANSPARENT), null);
	
	static {
		// TODO: Load / initialize from...somewhere
		map = new HashMap<String, Terrain>();

		map.put("tree", new Terrain("a tree", false, "terrain.tree", new PaletteEntry(Palette.COLOR_BROWN, Palette.COLOR_DARKGREEN, Palette.COLOR_LIGHTGREEN, Palette.COLOR_TRANSPARENT), null));
		map.put("dirt", new Terrain("dirt", true, "terrain.dot", new PaletteEntry(Palette.COLOR_BROWN, Palette.COLOR_BROWN, Palette.COLOR_BROWN, Palette.COLOR_TRANSPARENT), null));
		map.put("grass", new Terrain("grass", true, "terrain.grass", new PaletteEntry(Palette.COLOR_LIGHTGREEN, Palette.COLOR_LIGHTGREEN, Palette.COLOR_CHARTREUSE, Palette.COLOR_TRANSPARENT), null));
		map.put("wall", new Terrain("a wall", false, "wall.stone", new PaletteEntry(Palette.COLOR_WHITE, Palette.COLOR_BROWN, Palette.COLOR_TAN, Palette.COLOR_TRANSPARENT), "wall"));
		map.put("floor", new Terrain("wood floor", true, "terrain.floor", new PaletteEntry(Palette.COLOR_TAN, Palette.COLOR_DARKGREEN, Palette.COLOR_BROWN, Palette.COLOR_TRANSPARENT), null));
		map.put("mountain", new Terrain("the mountainside", false, "terrain.mountain", new PaletteEntry(Palette.COLOR_GRAY, Palette.COLOR_FORESTGREEN, Palette.COLOR_GRAY, Palette.COLOR_TRANSPARENT), null));
		
		map.put("upstair", new Terrain("stairs leading up", true, "terrain.upstair", new PaletteEntry(Palette.COLOR_WHITE, Palette.COLOR_WHITE, Palette.COLOR_WHITE, Palette.COLOR_TRANSPARENT), null));
		map.put("downstair", new Terrain("stairs leading down", true, "terrain.downstair", new PaletteEntry(Palette.COLOR_WHITE, Palette.COLOR_WHITE, Palette.COLOR_WHITE, Palette.COLOR_TRANSPARENT), null));

		map.put("doorway", new Terrain("a doorway", true, "terrain.door_open", new PaletteEntry(Palette.COLOR_GRAY, Palette.COLOR_TRANSPARENT, Palette.COLOR_BROWN), null));
	}

	public Terrain(String description, boolean passable, String glyphName, PaletteEntry paletteEntry, String blockCategory) {
		this.paletteEntry = paletteEntry;
		this.blockCategory = blockCategory;
		this.glyphName = glyphName;
		this.description = description;
		this.passable = passable;
	}

	public static Terrain get(String key) {
		return map.get(key);
	}

	public String getGlyphName() {
		return glyphName;
	}

	public PaletteEntry getPaletteEntry() {
		return paletteEntry;
	}

	public String getDescription() {
		return description;
	}
	
	public boolean isPassable() {
		return passable;
	}

	public String getBlockCategory() {
		return blockCategory;
	}
	
}
