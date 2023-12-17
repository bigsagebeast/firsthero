package com.churchofcoyote.hero.ui;

import com.badlogic.gdx.graphics.Color;
import com.churchofcoyote.hero.engine.WindowEngine;
import com.churchofcoyote.hero.module.RoguelikeModule;
import com.churchofcoyote.hero.roguelike.game.Game;
import com.churchofcoyote.hero.roguelike.game.Player;
import com.churchofcoyote.hero.roguelike.game.Statblock;
import com.churchofcoyote.hero.roguelike.world.Element;
import com.churchofcoyote.hero.roguelike.world.Entity;
import com.churchofcoyote.hero.text.TextBlock;

import java.util.ArrayList;
import java.util.List;

public class StatBarWindow extends UIWindow {

	TextBlock parent;
	List<TextBlock> statRows = new ArrayList<>();

	TextBlock levelDesc;
	TextBlock roomDesc;

	TextBlock tbStatSt;
	TextBlock tbStatTo;
	TextBlock tbStatDx;
	TextBlock tbStatAg;
	TextBlock tbStatPe;
	TextBlock tbStatWi;
	TextBlock tbStatAr;
	TextBlock tbStatAv;
	TextBlock tbStatDr;
	TextBlock tbStatDt;

	TextBlock tbElementWater;

	TextBlock tbNumHp;
	TextBlock tbNumMaxHp;
	TextBlock tbNumSp;
	TextBlock tbNumMaxSp;
	TextBlock tbNumDp;
	TextBlock tbNumMaxDp;

	private int windowWidth = 16;
	private int windowHeight = 10;

	private TextBlock[] rows = new TextBlock[windowHeight];

	public StatBarWindow() {
		parent = new TextBlock("", UIManager.NAME_STATBOX, RoguelikeModule.FONT_SIZE, 0, 0, 0, 5, Color.WHITE);

		levelDesc = new TextBlock("Dungeon of Testing", 0, 0, Color.WHITE);
		roomDesc = new TextBlock("", 0, 0, Color.WHITE);

		tbStatSt = new TextBlock("20", 5, 0, Color.WHITE);
		tbStatTo = new TextBlock("20", 13, 0, Color.WHITE);
		tbStatDx = new TextBlock("20", 21, 0, Color.WHITE);
		tbStatAg = new TextBlock("20", 29, 0, Color.WHITE);
		tbStatPe = new TextBlock("20", 5, 0, Color.WHITE);
		tbStatWi = new TextBlock("20", 13, 0, Color.WHITE);
		tbStatAr = new TextBlock("20", 21, 0, Color.WHITE);
		tbStatAv = new TextBlock("20", 29, 0, Color.WHITE);
		tbStatDr = new TextBlock("20", 5, 0, Color.WHITE);
		tbStatDt = new TextBlock("20", 13, 0, Color.WHITE);
		tbElementWater = new TextBlock("W 8/8", 0, 0, Color.CYAN);

		statRows.add(new TextBlock("Character Name", 0, 0, Color.WHITE));
		statRows.add(new TextBlock("", 0, 1, Color.WHITE));
		statRows.get(1).addChild(levelDesc);
		statRows.add(new TextBlock("", 0, 2, Color.WHITE));
		statRows.get(2).addChild(roomDesc);
		statRows.add(new TextBlock("Str:    Tou:    Dex:    Agi:    ", 0, 3, Color.WHITE));
		statRows.get(3).addChild(tbStatSt);
		statRows.get(3).addChild(tbStatTo);
		statRows.get(3).addChild(tbStatDx);
		statRows.get(3).addChild(tbStatAg);
		statRows.add(new TextBlock("Per:    Wis:    Arc:    Ava:    ", 0, 4, Color.WHITE));
		statRows.get(4).addChild(tbStatPe);
		statRows.get(4).addChild(tbStatWi);
		statRows.get(4).addChild(tbStatAr);
		statRows.get(4).addChild(tbStatAv);
		statRows.add(new TextBlock("DR:     DT:     ", 0, 5, Color.WHITE));
		statRows.get(5).addChild(tbStatDr);
		statRows.get(5).addChild(tbStatDt);

		statRows.add(new TextBlock("HP:    /      SP:    /      DP:    /   ", 0, 6, Color.WHITE));
		tbNumHp = new TextBlock("", 3, 0);
		tbNumMaxHp = new TextBlock("", 8, 0);
		tbNumSp = new TextBlock("", 17, 0);
		tbNumMaxSp = new TextBlock("", 22, 0);
		tbNumDp = new TextBlock("", 31, 0);
		tbNumMaxDp = new TextBlock("", 36, 0);
		statRows.get(6).addChild(tbNumHp);
		statRows.get(6).addChild(tbNumMaxHp);
		statRows.get(6).addChild(tbNumSp);
		statRows.get(6).addChild(tbNumMaxSp);
		statRows.get(6).addChild(tbNumDp);
		statRows.get(6).addChild(tbNumMaxDp);

		statRows.add(new TextBlock("", 0, 7, Color.YELLOW));

		statRows.add(new TextBlock("", 0, 8));
		statRows.get(8).addChild(tbElementWater);

		for (TextBlock statRow : statRows) {
			parent.addChild(statRow);
		}

		parent.compile();
	}
	
	public TextBlock getTextBlockParent() {
		return parent;
	}

	@Override
	public void update() {
		Entity entity = Game.getPlayerEntity();
		Player player = Game.getPlayer();
		Statblock statblock = entity.statblock;

		levelDesc.text = Game.getLevel().getName();
		if (entity.roomId < 0) {
			roomDesc.text = "";
		} else {
			roomDesc.text = Game.getLevel().rooms.get(entity.roomId).roomType.roomName;
		}

		tbStatSt.text = "" + statblock.str;
		tbStatTo.text = "" + statblock.tou;
		tbStatDx.text = "" + statblock.dex;
		tbStatAg.text = "" + statblock.agi;
		tbStatPe.text = "" + statblock.per;
		tbStatWi.text = "" + statblock.wil;
		tbStatAr.text = "" + statblock.arc;
		tbStatAv.text = "" + statblock.ava;
		tbStatDr.text = "" + statblock.dr;
		tbStatDt.text = "" + statblock.dt;

		tbNumHp.text = String.format("%4d", entity.hitPoints);
		tbNumMaxHp.text = String.format("%-4d", entity.maxHitPoints);
		tbNumSp.text = String.format("%4d", entity.spellPoints);
		tbNumMaxSp.text = String.format("%-4d", entity.maxSpellPoints);
		tbNumDp.text = String.format("%4d", entity.divinePoints);
		tbNumMaxDp.text = String.format("%-4d", entity.maxDivinePoints);

		if (entity.hitPoints > (entity.maxHitPoints / 2)) {
			tbNumHp.color = Color.WHITE;
		} else if (entity.hitPoints > (entity.maxHitPoints / 4)) {
			tbNumHp.color = Color.YELLOW;
		} else {
			tbNumHp.color = Color.RED;
		}

		statRows.get(7).text = "Hungry";

		if (player.maxElementCharges.get(Element.WATER) != null) {
			tbElementWater.text = "W: " + player.currentElementCharges.get(Element.WATER) + "/" + player.maxElementCharges.get(Element.WATER);
		}

		WindowEngine.setDirty(UIManager.NAME_STATBOX);
		parent.compile();
	}
}
