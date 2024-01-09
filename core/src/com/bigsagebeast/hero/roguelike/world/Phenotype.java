package com.bigsagebeast.hero.roguelike.world;

import com.bigsagebeast.hero.enums.Ambulation;
import com.bigsagebeast.hero.enums.Gender;
import com.bigsagebeast.hero.glyphtile.Palette;
import com.bigsagebeast.hero.glyphtile.PaletteEntry;
import com.bigsagebeast.hero.enums.BodySize;
import com.bigsagebeast.hero.roguelike.game.Rank;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

public class Phenotype {
	public String key;
	public String name;
	public String description;
	public int hitPoints;
	public int spellPoints;
	public int divinePoints;
	public boolean peaceful;
	public boolean isMonster;
	public String bodyPlan;
	public String glyphName;
	public List<String> glyphNames = new ArrayList<>();
	public Gender gender = Gender.AGENDER;
	public PaletteEntry palette;
	public boolean isManipulator;
	public Ambulation ambulation = Ambulation.WALKING_ONLY;
	public boolean incorporeal;
	public int experienceAwarded;
	public int moveCost = 1000;
	public int naturalWeaponToHit = 0;
	public int naturalWeaponDamage = 5;
	public int naturalRangedWeaponToHit = 0;
	public int naturalRangedWeaponDamage = 0;
	public int naturalRangedWeaponRange = 0;
	public int naturalArmorClass = 5;
	public int naturalArmorThickness = 0;
	public String naturalWeaponText;
	public int threat;
	public int packSize = 1;
	public int packSpawnArea = 3;
	public boolean wandering = true;
	public int frequency = 100;
	public int corpseSpawnPercent = 50;
	public String corpseMessage;
	public String corpseMethod;
	public String corpseMethodPre;
	public BodySize size = BodySize.MEDIUM;
	public PaletteEntry corpseSpawnColors = new PaletteEntry(Palette.COLOR_WHITE, Palette.COLOR_RED, Palette.COLOR_BROWN);
	public String chatPage;
	Set<Consumer<Entity>> setup = new HashSet<>();
	public List<LoadProc> procLoaders = new ArrayList<>();
	public LoadTactic tacticLoader;
	public List<String> tags = new ArrayList<>();
}
