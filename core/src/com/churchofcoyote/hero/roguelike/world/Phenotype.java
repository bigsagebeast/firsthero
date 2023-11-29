package com.churchofcoyote.hero.roguelike.world;

import com.churchofcoyote.hero.glyphtile.PaletteEntry;
import com.churchofcoyote.hero.roguelike.game.Rank;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

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
	public int naturalWeaponToHit = 0;
	public int naturalWeaponDamage = 5;
	public int naturalArmorClass = 5;
	public int naturalArmorThickness = 0;
	public int threat;
	Set<Consumer<Entity>> setup = new HashSet<>();
}
