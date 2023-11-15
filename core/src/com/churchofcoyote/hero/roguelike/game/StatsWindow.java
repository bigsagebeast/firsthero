package com.churchofcoyote.hero.roguelike.game;

import com.badlogic.gdx.graphics.Color;
import com.churchofcoyote.hero.module.RoguelikeModule;
import com.churchofcoyote.hero.roguelike.world.Entity;
import com.churchofcoyote.hero.text.TextBlock;
import com.churchofcoyote.hero.util.StringFormat;

public class StatsWindow {
	private int windowWidth = 16;
	private int windowHeight = 10;
	TextBlock parent;

	private TextBlock[] rows = new TextBlock[windowHeight];
	
	public StatsWindow() {
		parent = new TextBlock("", RoguelikeModule.FONT_SIZE, 62, 22, Color.WHITE);
		for (int i=0; i<windowHeight; i++) {
			rows[i] = new TextBlock("", RoguelikeModule.FONT_SIZE, 0, i, Color.WHITE);
			parent.addChild(rows[i]);
			parent.compile();
		}
	}
	
	public TextBlock getTextBlockParent() {
		return parent;
	}

	public void update(Entity c) {
		rows[0].text = StringFormat.format("HP: %3d/%3d", c.hitPoints, c.maxHitPoints);
		if (c.hitPoints > (c.maxHitPoints / 2)) {
			rows[0].color = Color.WHITE;
		} else if (c.hitPoints > (c.maxHitPoints / 4)) {
			rows[0].color = Color.YELLOW;
		} else {
			rows[0].color = Color.RED;
		}
		rows[1].text = StringFormat.format("SP: %3d/%3d", c.spellPoints, c.maxSpellPoints);
		rows[2].text = StringFormat.format("DP: %3d/%3d", c.divinePoints, c.maxDivinePoints);
		rows[3].text = StringFormat.format("STR: %-2s", c.stats);
		rows[4].text = StringFormat.format("AGI: %-2s", c.stats);
		rows[5].text = StringFormat.format("DEX: %-2s", c.stats);
		rows[6].text = StringFormat.format("CON: %-2s", c.stats);
		rows[7].text = StringFormat.format("INT: %-2s", c.stats);
		rows[8].text = StringFormat.format("WIS: %-2s", c.stats);
		rows[9].text = StringFormat.format("PER: %-2s", c.stats);
		parent.compile();
	}
}
