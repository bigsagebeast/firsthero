package com.churchofcoyote.hero.ui;

import com.badlogic.gdx.graphics.Color;
import com.churchofcoyote.hero.GameLoop;
import com.churchofcoyote.hero.engine.WindowEngine;
import com.churchofcoyote.hero.glyphtile.EntityGlyph;
import com.churchofcoyote.hero.module.RoguelikeModule;
import com.churchofcoyote.hero.roguelike.game.Game;
import com.churchofcoyote.hero.roguelike.world.BodyPart;
import com.churchofcoyote.hero.roguelike.world.Entity;
import com.churchofcoyote.hero.text.TextBlock;
import com.churchofcoyote.hero.util.StringFormat;

public class EquipmentWindow extends UIWindow {
	private int windowWidth = 26;
	private int windowHeight = 10;
	TextBlock parent;

	private TextBlock[] rows = new TextBlock[windowHeight];
	private TextBlock[] glyphs = new TextBlock[windowHeight];
	private TextBlock[] equipmentName = new TextBlock[windowHeight];

	public EquipmentWindow() {
		parent = new TextBlock("", UIManager.NAME_EQUIPMENT, RoguelikeModule.FONT_SIZE, 0, 0, 0, 5, Color.WHITE);
		for (int i=0; i<windowHeight; i++) {
			rows[i] = new TextBlock("", null, RoguelikeModule.FONT_SIZE, 0, i, Color.WHITE);
			glyphs[i] = new TextBlock(null, null, RoguelikeModule.FONT_SIZE, 8, i, Color.WHITE);
			equipmentName[i] = new TextBlock(null, null, RoguelikeModule.FONT_SIZE, 8, i, Color.WHITE);
			parent.addChild(rows[i]);
			parent.addChild(glyphs[i]);
			parent.addChild(equipmentName[i]);
		}
		parent.compile();
	}
	
	public TextBlock getTextBlockParent() {
		return parent;
	}

	@Override
	public void update() {
		Entity c = Game.getPlayerEntity();
		if (c == null) {
			return;
		}
		WindowEngine.setDirty(UIManager.NAME_EQUIPMENT);
		int row = 0;
		for (BodyPart bp : c.body.getParts())
		{
			Entity equipped = c.body.getEquipment(bp);
			String equipmentString;
			equipmentName[row].close();
			if (equipped != null)
			{
				glyphs[row].glyph = EntityGlyph.getGlyph(equipped);
				equipmentString = equipped.getVisibleName();
				//equipmentName[row] = new TextBlock(equipmentString, null, RoguelikeModule.FONT_SIZE, 9, 0, Color.WHITE);
				equipmentName[row] = equipped.getNameBlock();
				equipmentName[row].x = 9;
			}
			else if (bp == BodyPart.OFF_HAND && c.body.getEquipment(BodyPart.PRIMARY_HAND) != null &&
					c.body.getEquipment(BodyPart.PRIMARY_HAND).getEquippable().equipmentFor == BodyPart.TWO_HAND)
			{
				glyphs[row].glyph = null;
				// TODO should involve an 'is 2h' flag on the player
				equipmentString = "(2-handed)";
				equipmentName[row] = new TextBlock("(2-handed)", null, RoguelikeModule.FONT_SIZE, 9, 0, Color.WHITE);
			} else {
				glyphs[row].glyph = null;
				equipmentString = "empty";
				equipmentName[row] = new TextBlock("empty", null, RoguelikeModule.FONT_SIZE, 9, 0, Color.WHITE);
			}
			//rows[row].text = StringFormat.format("%-7s: %-16s", bp.getAbbrev(), equipmentString);
			rows[row].text = StringFormat.format("%-7s:", bp.getAbbrev());
			rows[row].addChild(equipmentName[row]);
			row++;
		}
		parent.compile();
	}
}
