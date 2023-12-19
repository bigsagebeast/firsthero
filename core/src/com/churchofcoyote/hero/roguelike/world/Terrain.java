package com.churchofcoyote.hero.roguelike.world;

import java.util.HashMap;
import java.util.Map;

import com.churchofcoyote.hero.glyphtile.Palette;
import com.churchofcoyote.hero.glyphtile.PaletteEntry;

public class Terrain {
	private String name;
	private String description;
	private boolean passable;
	private boolean spawnable;

	private String glyphName;
	private PaletteEntry paletteEntry;
	private String blockCategory;
	private String[] matchingBlocks;

	public static Map<String, Terrain> map;
	
	public static Terrain BLANK = new Terrain("empty", "Empty.", false, false, "terrain.dot", new PaletteEntry(Palette.COLOR_GRAY, Palette.COLOR_BROWN, Palette.COLOR_BROWN, Palette.COLOR_TRANSPARENT), null, null);
	
	static {
		// TODO: Load / initialize from...somewhere
		map = new HashMap<String, Terrain>();

		addTerrain("tree", "a tree", false, false, "terrain.tree", new PaletteEntry(Palette.COLOR_BROWN, Palette.COLOR_DARKGREEN, Palette.COLOR_LIGHTGREEN, Palette.COLOR_TRANSPARENT), null);
		addTerrain("dot", "dirt", true, true, "terrain.dot", new PaletteEntry(Palette.COLOR_BROWN, Palette.COLOR_BROWN, Palette.COLOR_BROWN, Palette.COLOR_TRANSPARENT), null);
		addTerrain("dirt1", "dirt", true, true, "terrain.dirt1", new PaletteEntry(Palette.COLOR_BROWN, Palette.COLOR_BROWN, Palette.COLOR_BROWN, Palette.COLOR_TRANSPARENT), null);
		addTerrain("dirt2", "dirt", true, true, "terrain.dirt2", new PaletteEntry(Palette.COLOR_BROWN, Palette.COLOR_BROWN, Palette.COLOR_BROWN, Palette.COLOR_TRANSPARENT), null);
		addTerrain("water", "water", true, false, "terrain.water", new PaletteEntry(Palette.COLOR_BLUE, Palette.COLOR_CERULEAN, Palette.COLOR_CYAN, Palette.COLOR_TRANSPARENT), "water");
		addTerrain("grass", "grass", true, true, "terrain.grass", new PaletteEntry(Palette.COLOR_LIGHTGREEN, Palette.COLOR_LIGHTGREEN, Palette.COLOR_CHARTREUSE, Palette.COLOR_TRANSPARENT), null);
		addTerrain("wall", "a wall", false, false, "wall.stone", new PaletteEntry(Palette.COLOR_WHITE, Palette.COLOR_BROWN, Palette.COLOR_TAN, Palette.COLOR_TRANSPARENT), "wall");
		addTerrain("uncarveable", "a wall", false, false, "terrain.mountain", new PaletteEntry(Palette.COLOR_WHITE, Palette.COLOR_BROWN, Palette.COLOR_TAN, Palette.COLOR_TRANSPARENT), null);
		addTerrain("floor", "wood floor", true, true, "terrain.floor", new PaletteEntry(Palette.COLOR_TAN, Palette.COLOR_DARKGREEN, Palette.COLOR_BROWN, Palette.COLOR_TRANSPARENT), null);
		addTerrain("mountain", "the mountainside", false, false, "terrain.mountain", new PaletteEntry(Palette.COLOR_GRAY, Palette.COLOR_FORESTGREEN, Palette.COLOR_GRAY, Palette.COLOR_TRANSPARENT), null);

		addTerrain("upstair", "stairs leading up", true, false, "terrain.upstair", new PaletteEntry(Palette.COLOR_WHITE, Palette.COLOR_WHITE, Palette.COLOR_WHITE, Palette.COLOR_TRANSPARENT), null);
		addTerrain("downstair", "stairs leading down", true, false, "terrain.downstair", new PaletteEntry(Palette.COLOR_WHITE, Palette.COLOR_WHITE, Palette.COLOR_WHITE, Palette.COLOR_TRANSPARENT), null);

		addTerrain("doorway", "a doorway", true, false, "terrain.door_open", new PaletteEntry(Palette.COLOR_GRAY, Palette.COLOR_TRANSPARENT, Palette.COLOR_BROWN), null);

		addTerrain("aurex.cliffside.dirt", "a cliffside", true, false, "aurex.cliffside.dirt", new PaletteEntry(Palette.COLOR_TAN, Palette.COLOR_BROWN, Palette.COLOR_SKYBLUE), "ground");
		addTerrain("aurex.cliffside.grass", "a cliffside", true, false, "aurex.cliffside.grass", new PaletteEntry(Palette.COLOR_TAN, Palette.COLOR_DARKGREEN, Palette.COLOR_SKYBLUE), "ground");
		addTerrain("aurex.cliff", "a cliff", true, false, "aurex.cliff", new PaletteEntry(Palette.COLOR_TAN, Palette.COLOR_DARKGREEN, Palette.COLOR_SKYBLUE), "cliff", new String[]{"ground"});
		addTerrain("aurex.sky", "sky", true, false, "aurex.sky", new PaletteEntry(Palette.COLOR_SKYBLUE, Palette.COLOR_SKYBLUE, Palette.COLOR_SKYBLUE, Palette.COLOR_SKYBLUE), null);
	}

	public static void addTerrain(String name, String description, boolean passable, boolean spawnable, String glyphName, PaletteEntry paletteEntry, String blockCategory, String[] matchingBlocks) {
		map.put(name, new Terrain(name, description, passable, spawnable, glyphName, paletteEntry, blockCategory, matchingBlocks));
	}

	public static void addTerrain(String name, String description, boolean passable, boolean spawnable, String glyphName, PaletteEntry paletteEntry, String blockCategory) {
		map.put(name, new Terrain(name, description, passable, spawnable, glyphName, paletteEntry, blockCategory, null));
	}

	public Terrain(String name, String description, boolean passable, boolean spawnable, String glyphName, PaletteEntry paletteEntry, String blockCategory, String[] matchingBlocks) {
		this.name = name;
		this.paletteEntry = paletteEntry;
		this.blockCategory = blockCategory;
		this.glyphName = glyphName;
		this.description = description;
		this.passable = passable;
		this.spawnable = spawnable;
		this.matchingBlocks = matchingBlocks;
	}

	public static Terrain get(String key) {
		return map.get(key);
	}

	public String getName() {
		return name;
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

	public boolean isSpawnable() { return spawnable; }

	public String getBlockCategory() {
		return blockCategory;
	}

	public String[] getMatchingBlocks() { return matchingBlocks; }
	
}
