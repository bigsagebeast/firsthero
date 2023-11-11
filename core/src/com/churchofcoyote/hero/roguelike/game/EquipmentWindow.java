package com.churchofcoyote.hero.roguelike.game;

import com.badlogic.gdx.graphics.Color;
import com.churchofcoyote.hero.module.RoguelikeModule;
import com.churchofcoyote.hero.roguelike.world.Creature;
import com.churchofcoyote.hero.text.TextBlock;
import com.churchofcoyote.hero.util.StringFormat;

public class EquipmentWindow {
	private int windowWidth = 26;
	private int windowHeight = 10;
	TextBlock parent;

	private TextBlock[] rows = new TextBlock[windowHeight];
	
	public EquipmentWindow() {
		parent = new TextBlock("", RoguelikeModule.FONT_SIZE, 79, 22, Color.WHITE);
		for (int i=0; i<windowHeight; i++) {
			rows[i] = new TextBlock("", RoguelikeModule.FONT_SIZE, 0, i, Color.WHITE);
			parent.addChild(rows[i]);
		}
		parent.compile();
	}
	
	public TextBlock getTextBlockParent() {
		return parent;
	}

	public void update(Creature c) {
		rows[0].text = StringFormat.format("Head:  %.16s", "Light helmet");
		rows[1].text = StringFormat.format("Torso: %.16s", "Leather armor");
		rows[2].text = StringFormat.format("Legs:  %.16s", "Greaves");
		rows[3].text = StringFormat.format("Arms:  %.16s", "Gloves");
		rows[4].text = StringFormat.format("Feet:  %.16s", "Leather boots");
		rows[5].text = "";
		rows[6].text = StringFormat.format("RHand: %.16s", "Iron short sword");
		rows[7].text = StringFormat.format("LHand: %.16s", "Iron shield");
		rows[8].text = StringFormat.format("Range: %.16s", "Shortbow");
		rows[9].text = StringFormat.format("Ammo:  %.16s", "15 wooden arrows");
	}
}
