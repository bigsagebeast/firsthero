package com.churchofcoyote.hero.roguelike.world;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.badlogic.gdx.graphics.Color;
import com.churchofcoyote.hero.engine.asciitile.Glyph;
import com.churchofcoyote.hero.glyphtile.GlyphTile;
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
	
	public static Terrain BLANK = new Terrain(Glyph.BLANK, "Empty.", false);
	
	static {
		// TODO: Load / initialize from...somewhere
		map = new HashMap<String, Terrain>();

		map.put("tree", new Terrain("T", Color.GREEN, "a tree", false, "terrain.tree", new PaletteEntry(Palette.COLOR_BROWN, Palette.COLOR_DARKGREEN, Palette.COLOR_LIGHTGREEN, Palette.COLOR_TRANSPARENT), null));
		map.put("dirt", new Terrain("..,,", Color.FIREBRICK, "dirt", true, "terrain.dot", new PaletteEntry(Palette.COLOR_BROWN, Palette.COLOR_BROWN, Palette.COLOR_BROWN, Palette.COLOR_TRANSPARENT), null));
		map.put("grass", new Terrain("..,,`'", Color.FOREST, "grass", true, "terrain.grass", new PaletteEntry(Palette.COLOR_LIGHTGREEN, Palette.COLOR_LIGHTGREEN, Palette.COLOR_CHARTREUSE, Palette.COLOR_TRANSPARENT), null));
		map.put("wall", new Terrain("#", Color.LIGHT_GRAY, "a wall", false, "wall.stone", new PaletteEntry(Palette.COLOR_WHITE, Palette.COLOR_BROWN, Palette.COLOR_BROWN, Palette.COLOR_TRANSPARENT), "wall"));
		map.put("floor", new Terrain("_", Color.BROWN, "wood floor", true, "terrain.floor", new PaletteEntry(Palette.COLOR_TAN, Palette.COLOR_DARKGREEN, Palette.COLOR_BROWN, Palette.COLOR_TRANSPARENT), null));
		map.put("mountain", new Terrain("^^#", Color.LIGHT_GRAY, "the mountainside", false, "terrain.mountain", new PaletteEntry(Palette.COLOR_GRAY, Palette.COLOR_FORESTGREEN, Palette.COLOR_GRAY, Palette.COLOR_TRANSPARENT), null));
		
		map.put("upstair", new Terrain("<", Color.WHITE, "stairs leading up", true, "terrain.upstair", new PaletteEntry(Palette.COLOR_WHITE, Palette.COLOR_WHITE, Palette.COLOR_WHITE, Palette.COLOR_TRANSPARENT), null));
		map.put("downstair", new Terrain(">", Color.WHITE, "stairs leading down", true, "terrain.downstair", new PaletteEntry(Palette.COLOR_WHITE, Palette.COLOR_WHITE, Palette.COLOR_WHITE, Palette.COLOR_TRANSPARENT), null));


		/*0 17 creature.humanoid
0 18 terrain.tree
0 19 terrain.mountain
0 20 terrain.downstair
0 21 terrain.upstair
0 22 terrain.dot

		 */
	}

	public Terrain(String symbols, Color color, String description, boolean passable, String glyphName, PaletteEntry paletteEntry, String blockCategory) {
		this(symbols, color, description, passable);
		this.paletteEntry = paletteEntry;
		this.blockCategory = blockCategory;
		this.glyphName = glyphName;
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
