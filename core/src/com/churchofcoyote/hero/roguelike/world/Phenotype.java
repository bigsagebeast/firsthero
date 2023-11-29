package com.churchofcoyote.hero.roguelike.world;

import com.churchofcoyote.hero.glyphtile.PaletteEntry;
import com.churchofcoyote.hero.roguelike.game.Rank;

public class Phenotype {
	public String name;
	public int hitPoints;
	public int spellPoints;
	public int divinePoints;
	//public Glyph glyph;
	public boolean peaceful;
	public Rank stats;
	public boolean isMonster;
	public String bodyPlan;
	public String glyphName;
	public PaletteEntry paletteEntry;
	public boolean isManipulator;
	public int experienceAwarded;
	public int moveCost = 1000;
	public int threat;
}
