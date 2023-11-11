package com.churchofcoyote.hero.roguelike.world;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.graphics.Color;
import com.churchofcoyote.hero.engine.asciitile.Glyph;
import com.churchofcoyote.hero.roguelike.game.Rank;

public class Bestiary {
	public Map<String, Phenotype> map = new HashMap<String, Phenotype>();
	
	public Bestiary() {
		Phenotype pc = new Phenotype();
		Phenotype goblin = new Phenotype();
		Phenotype farmer = new Phenotype();

		pc.name = "player";
		pc.hitPoints = 50;
		pc.spellPoints = 20;
		pc.glyph = new Glyph('@', Color.WHITE);
		pc.stats = Rank.B_PLUS;

		Glyph glyphHuman = new Glyph('U', Color.LIGHT_GRAY);
		
		goblin.name="goblin";
		goblin.glyph = new Glyph('g', Color.GRAY);
		goblin.peaceful = false;
		goblin.hitPoints = 16;
		goblin.stats = Rank.C_MINUS;
		
		farmer.name="Farmer";
		farmer.glyph = glyphHuman;
		farmer.peaceful = true;
		farmer.hitPoints = 10;
		farmer.stats = Rank.D;
		
		map.put("player", pc);
		map.put("goblin", goblin);
		map.put("farmer", farmer);
	}
	
	public Creature create(String key, String name) {
		Creature c = new Creature();
		Phenotype p = map.get(key);
		c.phenotype = p;
		if (name != null) {
			c.name = name;
		} else {
			c.name = p.name;
		}
		c.hitPoints = p.hitPoints;
		c.maxHitPoints = p.hitPoints;
		c.spellPoints = p.spellPoints;
		c.maxSpellPoints = p.spellPoints;
		c.divinePoints = p.divinePoints;
		c.maxDivinePoints = p.divinePoints;
		c.glyph = p.glyph;
		c.peaceful = p.peaceful;
		c.stats = p.stats;
		return c;
	}
	
}
