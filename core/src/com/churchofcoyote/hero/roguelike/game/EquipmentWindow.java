package com.churchofcoyote.hero.roguelike.game;

import com.badlogic.gdx.graphics.Color;
import com.churchofcoyote.hero.glyphtile.EntityGlyph;
import com.churchofcoyote.hero.module.RoguelikeModule;
import com.churchofcoyote.hero.roguelike.world.BodyPart;
import com.churchofcoyote.hero.roguelike.world.Entity;
import com.churchofcoyote.hero.text.TextBlock;
import com.churchofcoyote.hero.util.StringFormat;

public class EquipmentWindow {
	private int windowWidth = 26;
	private int windowHeight = 10;
	TextBlock parent;

	private TextBlock[] rows = new TextBlock[windowHeight];
	private TextBlock[] glyphs = new TextBlock[windowHeight];
	
	public EquipmentWindow() {
		parent = new TextBlock("", RoguelikeModule.FONT_SIZE, 79, 22, Color.WHITE);
		for (int i=0; i<windowHeight; i++) {
			rows[i] = new TextBlock("", RoguelikeModule.FONT_SIZE, 0, i, Color.WHITE);
			glyphs[i] = new TextBlock(null, RoguelikeModule.FONT_SIZE, 8, i, Color.WHITE);
			parent.addChild(rows[i]);
			parent.addChild(glyphs[i]);
		}
		parent.compile();
	}
	
	public TextBlock getTextBlockParent() {
		return parent;
	}

	public void update(Entity c) {
		int row = 0;
		for (BodyPart bp : c.body.getParts())
		{
			Entity equipped = c.body.getEquipment(bp);
			String equipmentString;
			if (equipped != null)
			{
				glyphs[row].glyph = EntityGlyph.getGlyph(equipped);
				equipmentString = equipped.name;
			}
			else if (bp == BodyPart.OFF_HAND && c.body.getEquipment(BodyPart.PRIMARY_HAND) != null &&
					c.body.getEquipment(BodyPart.PRIMARY_HAND).getEquippable().equipmentFor == BodyPart.TWO_HAND)
			{
				// TODO should involve an 'is 2h' flag on the player
				equipmentString = "(2-handed)";
			} else {
				equipmentString = "empty";
			}
			rows[row].text = StringFormat.format("%-7s: %-16s", bp.getAbbrev(), equipmentString);

			row++;
		}
		parent.compile();
	}
}
