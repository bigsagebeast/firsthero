package com.churchofcoyote.hero.roguelike.world;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.graphics.Color;
import com.churchofcoyote.hero.engine.asciitile.Glyph;
import com.churchofcoyote.hero.roguelike.game.Rank;
import com.churchofcoyote.hero.roguelike.world.proc.ProcMonster;
import com.churchofcoyote.hero.roguelike.world.proc.ProcMover;
import com.churchofcoyote.hero.roguelike.world.proc.ProcPlayer;
import com.churchofcoyote.hero.roguelike.world.proc.PropPopupOnSeen;

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
		pc.isMonster = false;

		Glyph glyphHuman = new Glyph('U', Color.LIGHT_GRAY);
		
		goblin.name="goblin";
		goblin.glyph = new Glyph('g', Color.GRAY);
		goblin.peaceful = false;
		goblin.hitPoints = 16;
		goblin.stats = Rank.C_MINUS;
		goblin.isMonster = true;
		
		farmer.name="Farmer";
		farmer.glyph = glyphHuman;
		farmer.peaceful = true;
		farmer.hitPoints = 10;
		farmer.stats = Rank.D;
		farmer.isMonster = false;
		
		map.put("player", pc);
		map.put("goblin", goblin);
		map.put("farmer", farmer);
	}
	
	public Entity create(String key, String name) {
		Entity e = new Entity();

		Phenotype p = map.get(key);
		e.phenotype = p;
		if (name != null) {
			e.name = name;
		} else {
			e.name = p.name;
		}
		e.hitPoints = p.hitPoints;
		e.maxHitPoints = p.hitPoints;
		e.spellPoints = p.spellPoints;
		e.maxSpellPoints = p.spellPoints;
		e.divinePoints = p.divinePoints;
		e.maxDivinePoints = p.divinePoints;
		e.glyph = p.glyph;
		e.stats = p.stats;
		if (key.equals("player")) {
			e.addProc(new ProcPlayer(e));
		}
		else if (p.isMonster) {
			e.addProc(new ProcMonster(e));
			//e.addProc(new PropPopupOnSeen(e, "It's a monster!"));
		} else {
			e.addProc(new ProcMover(e));
			//e.addProc(new PropPopupOnSeen(e, "It's a creature!"));
		}
		return e;
	}
	
}
