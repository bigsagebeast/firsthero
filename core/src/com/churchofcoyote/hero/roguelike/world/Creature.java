package com.churchofcoyote.hero.roguelike.world;

import com.churchofcoyote.hero.engine.asciitile.Glyph;
import com.churchofcoyote.hero.roguelike.game.Player;
import com.churchofcoyote.hero.roguelike.game.Rank;
import com.churchofcoyote.hero.roguelike.world.ai.ChaseAndMeleeTactic;
import com.churchofcoyote.hero.roguelike.world.ai.Strategy;
import com.churchofcoyote.hero.roguelike.world.ai.Tactic;
import com.churchofcoyote.hero.util.Point;

public class Creature {
	public String name;
	public Phenotype phenotype;
	public Point pos;
	public Glyph glyph;
	public boolean peaceful = false;
	
	public Strategy strategy;
	
	// schedule for removal?
	public boolean dead;
	
	// combat stats
	public int hitPoints;
	public int spellPoints;
	public int divinePoints;
	public int maxHitPoints;
	public int maxSpellPoints;
	public int maxDivinePoints;

	// one action is typically 1000 units
	public int delay = 0;
	
	public Rank stats = Rank.C;
	
	public Creature() {
		
	}
	
	public void tookTurn(int turnLength) {
		delay += turnLength;
	}
	
	public String getVisibleName(Player p) {
		return name;
	}
	
	public boolean isPeacefulToPlayer(Player p) {
		return false;//peaceful;
	}
	
	public void npcAct() {
		// do something from Strategy
		if (!peaceful) {
			Tactic tactic = new ChaseAndMeleeTactic();
			tactic.execute(this);
		} else {
			tookTurn(1000);
		}
	}
}
