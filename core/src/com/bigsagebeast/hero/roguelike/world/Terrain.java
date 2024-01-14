package com.bigsagebeast.hero.roguelike.world;

import java.util.HashMap;
import java.util.Map;

import com.bigsagebeast.hero.glyphtile.Palette;
import com.bigsagebeast.hero.glyphtile.PaletteEntry;

public class Terrain {
	private String name;
	private String description;
	private boolean passable;
	private boolean bumpInto; // just for message generation, ignored if passable
	private boolean opaque;
	private boolean spawnable;
	private boolean safe; // is this okay to walk through?

	private String glyphName;
	private PaletteEntry paletteEntry;
	private String blockCategory;
	private String[] matchingBlocks;

	public static Map<String, Terrain> map;
	
	public static Terrain BLANK = new Terrain("empty", "Empty.", false, false, true, false, false,"terrain.dot", new PaletteEntry(Palette.COLOR_GRAY, Palette.COLOR_BROWN, Palette.COLOR_BROWN, Palette.COLOR_TRANSPARENT), null, null);
	
	static {
		// TODO: Load / initialize from...somewhere
		map = new HashMap<String, Terrain>();

		addTerrain("tree", "a tree", false, true, true, false, false, "terrain.tree", new PaletteEntry(Palette.COLOR_BROWN, Palette.COLOR_DARKGREEN, Palette.COLOR_LIGHTGREEN, Palette.COLOR_TRANSPARENT), null);
		addTerrain("dot", "dirt", true, false, false, true, true, "terrain.dot", new PaletteEntry(Palette.COLOR_BROWN, Palette.COLOR_BROWN, Palette.COLOR_BROWN, Palette.COLOR_TRANSPARENT), null);
		addTerrain("dirt1", "dirt", true, false, false, true, true, "terrain.dirt1", new PaletteEntry(Palette.COLOR_BROWN, Palette.COLOR_BROWN, Palette.COLOR_BROWN, Palette.COLOR_TRANSPARENT), null);
		addTerrain("dirt2", "dirt", true, false, false, true, true, "terrain.dirt2", new PaletteEntry(Palette.COLOR_BROWN, Palette.COLOR_BROWN, Palette.COLOR_BROWN, Palette.COLOR_TRANSPARENT), null);
		addTerrain("farmland", "farmland", true, false, false, true, true, "terrain.farmland", new PaletteEntry(Palette.COLOR_TAN), null);
		addTerrain("water", "water", true, false, false, false, false, "terrain.water", new PaletteEntry(Palette.COLOR_BLUE, Palette.COLOR_CERULEAN, Palette.COLOR_CYAN, Palette.COLOR_TRANSPARENT), "water");
		addTerrain("grass", "grass", true, false, false, true, true, "terrain.grass", new PaletteEntry(Palette.COLOR_LIGHTGREEN, Palette.COLOR_LIGHTGREEN, Palette.COLOR_CHARTREUSE, Palette.COLOR_TRANSPARENT), null);
		addTerrain("wall", "a wall", false, true, true, false, false, "wall.stone", new PaletteEntry(Palette.COLOR_WHITE, Palette.COLOR_BROWN, Palette.COLOR_TAN, Palette.COLOR_TRANSPARENT), "wall");
		addTerrain("cavernwall", "a wall", false, true, true, false, false, "wall.cavern", new PaletteEntry(Palette.COLOR_WHITE, Palette.COLOR_BROWN, Palette.COLOR_TAN, Palette.COLOR_TRANSPARENT), "wall");
		addTerrain("uncarveable", "a wall", false, false, true, false, false, "terrain.mountain", new PaletteEntry(Palette.COLOR_WHITE, Palette.COLOR_BROWN, Palette.COLOR_TAN, Palette.COLOR_TRANSPARENT), null);
		addTerrain("floor", "wood floor", true, false, false, true, true, "terrain.floor", new PaletteEntry(Palette.COLOR_TAN, Palette.COLOR_DARKGREEN, Palette.COLOR_BROWN, Palette.COLOR_TRANSPARENT), null);
		addTerrain("mountain", "the mountainside", false, true, true, false, false, "terrain.mountain", new PaletteEntry(Palette.COLOR_GRAY, Palette.COLOR_FORESTGREEN, Palette.COLOR_GRAY, Palette.COLOR_TRANSPARENT), null);

//		addTerrain("upstair", "stairs leading up", true, false, false, false, true, "terrain.upstair", new PaletteEntry(Palette.COLOR_WHITE, Palette.COLOR_WHITE, Palette.COLOR_WHITE, Palette.COLOR_TRANSPARENT), null);
//		addTerrain("downstair", "stairs leading down", true, false, false, false, true, "terrain.downstair", new PaletteEntry(Palette.COLOR_WHITE, Palette.COLOR_WHITE, Palette.COLOR_WHITE, Palette.COLOR_TRANSPARENT), null);

		addTerrain("doorway", "a doorway", true, false, false, false, true, "terrain.door_open", new PaletteEntry(Palette.COLOR_GRAY, Palette.COLOR_TRANSPARENT, Palette.COLOR_BROWN), null);

		addTerrain("aurex.cliffside.dirt", "a cliffside", true, false, false, false, true, "aurex.cliffside.dirt", new PaletteEntry(Palette.COLOR_TAN, Palette.COLOR_BROWN, Palette.COLOR_SKYBLUE), "ground");
		addTerrain("aurex.cliffside.grass", "a cliffside", true, false, false, false, true, "aurex.cliffside.grass", new PaletteEntry(Palette.COLOR_TAN, Palette.COLOR_DARKGREEN, Palette.COLOR_SKYBLUE), "ground");
		addTerrain("aurex.cliff", "a cliff", false, false, false, false, false, "aurex.cliff", new PaletteEntry(Palette.COLOR_TAN, Palette.COLOR_DARKGREEN, Palette.COLOR_SKYBLUE), "cliff", new String[]{"ground"});
		addTerrain("aurex.sky", "sky", false, false, false, false, false, "aurex.sky", new PaletteEntry(Palette.COLOR_SKYBLUE, Palette.COLOR_SKYBLUE, Palette.COLOR_SKYBLUE, Palette.COLOR_SKYBLUE), null);
	}

	public static void addTerrain(String name, String description, boolean passable, boolean bumpInto, boolean opaque, boolean spawnable, boolean safe, String glyphName, PaletteEntry paletteEntry, String blockCategory, String[] matchingBlocks) {
		map.put(name, new Terrain(name, description, passable, bumpInto, opaque, spawnable, safe, glyphName, paletteEntry, blockCategory, matchingBlocks));
	}

	public static void addTerrain(String name, String description, boolean passable, boolean bumpInto, boolean opaque, boolean spawnable, boolean safe, String glyphName, PaletteEntry paletteEntry, String blockCategory) {
		map.put(name, new Terrain(name, description, passable, bumpInto, opaque, spawnable, safe, glyphName, paletteEntry, blockCategory, null));
	}

	public Terrain(String name, String description, boolean passable, boolean bumpInto, boolean opaque, boolean spawnable, boolean safe, String glyphName, PaletteEntry paletteEntry, String blockCategory, String[] matchingBlocks) {
		this.name = name;
		this.paletteEntry = paletteEntry;
		this.blockCategory = blockCategory;
		this.glyphName = glyphName;
		this.description = description;
		this.passable = passable;
		this.bumpInto = bumpInto;
		this.opaque = opaque;
		this.spawnable = spawnable;
		this.safe = safe;
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

	public boolean isOpaque() { return opaque; }

	public boolean isBumpInto() { return bumpInto; }

	public boolean isSpawnable() { return spawnable; }

	public boolean isSafe() { return safe; }

	public String getBlockCategory() {
		return blockCategory;
	}

	public String[] getMatchingBlocks() { return matchingBlocks; }
	
}
