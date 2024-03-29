package com.bigsagebeast.hero.ui;

import com.badlogic.gdx.graphics.Color;
import com.bigsagebeast.hero.engine.WindowEngine;
import com.bigsagebeast.hero.enums.Stat;
import com.bigsagebeast.hero.module.RoguelikeModule;
import com.bigsagebeast.hero.roguelike.game.*;
import com.bigsagebeast.hero.roguelike.world.Element;
import com.bigsagebeast.hero.text.TextBlock;
import com.bigsagebeast.hero.roguelike.world.Entity;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
	TextBlock tbStatDe;
	TextBlock tbStatAt;
	TextBlock tbStatSpeed;
	TextBlock tbStatLevel;

	TextBlock tbElementWater;
	TextBlock tbElementFire;
	TextBlock tbElementLightning;
	TextBlock tbElementNaturae;

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
		tbStatDe = new TextBlock("20", 5, 0, Color.WHITE);
		tbStatAt = new TextBlock("20", 13, 0, Color.WHITE);
		tbStatSpeed = new TextBlock("100", 23, 0, Color.WHITE);
		tbStatLevel = new TextBlock("1", 35, 0, Color.WHITE);
		tbElementWater = new TextBlock("W 8/8", 0, 0, Color.CYAN);
		tbElementFire = new TextBlock("F 8/8", 9, 0, Color.RED);
		tbElementLightning = new TextBlock("L 8/8", 18, 0, Color.YELLOW);
		tbElementNaturae = new TextBlock("P 8/8", 27, 0, Color.GREEN);

		statRows.add(new TextBlock("", 0, 0, Color.WHITE));
		statRows.add(new TextBlock("", 0, 1, Color.WHITE));
		statRows.get(1).addChild(levelDesc);
		statRows.add(new TextBlock("", 0, 2, Color.WHITE));
		statRows.get(2).addChild(roomDesc);
		statRows.add(new TextBlock("Str:    Tou:    Dex:    Agi:    ", 0, 3, Color.WHITE));
		statRows.get(3).addChild(tbStatSt);
		statRows.get(3).addChild(tbStatTo);
		statRows.get(3).addChild(tbStatDx);
		statRows.get(3).addChild(tbStatAg);
		statRows.add(new TextBlock("Per:    Wil:    Arc:    Ava:    ", 0, 4, Color.WHITE));
		statRows.get(4).addChild(tbStatPe);
		statRows.get(4).addChild(tbStatWi);
		statRows.get(4).addChild(tbStatAr);
		statRows.get(4).addChild(tbStatAv);
		statRows.add(new TextBlock("Def:    AT:     Speed:      Level:     ", 0, 5, Color.WHITE));
		statRows.get(5).addChild(tbStatDe);
		statRows.get(5).addChild(tbStatAt);
		statRows.get(5).addChild(tbStatSpeed);
		statRows.get(5).addChild(tbStatLevel);

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

		statRows.add(new TextBlock("", 0, 7));
		statRows.get(7).addChild(tbElementWater);
		statRows.get(7).addChild(tbElementFire);
		statRows.get(7).addChild(tbElementLightning);
		statRows.get(7).addChild(tbElementNaturae);

		statRows.add(new TextBlock("", 0, 8));

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

		String name = "";
		if (Game.getPlayerEntity() != null) {
			name = Game.getPlayerEntity().name;
			if (name == null) {
				name = Profile.getString("godName" + "'s Avatar");
			}
		}
		statRows.get(0).text = name;

		levelDesc.text = Game.getLevel().getFriendlyName();
		if (entity.roomId < 0) {
			roomDesc.text = "";
		} else {
			roomDesc.text = Game.getLevel().rooms.get(entity.roomId).roomType.roomName;
		}

		tbStatSt.text = "" + entity.getStat(Stat.STRENGTH);
		tbStatTo.text = "" + entity.getStat(Stat.TOUGHNESS);
		tbStatDx.text = "" + entity.getStat(Stat.DEXTERITY);
		tbStatAg.text = "" + entity.getStat(Stat.AGILITY);
		tbStatPe.text = "" + entity.getStat(Stat.PERCEPTION);
		tbStatWi.text = "" + entity.getStat(Stat.WILLPOWER);
		tbStatAr.text = "" + entity.getStat(Stat.ARCANUM);
		tbStatAv.text = "" + entity.getStat(Stat.AVATAR);
		tbStatSt.color = colorForStat(Stat.STRENGTH);
		tbStatTo.color = colorForStat(Stat.TOUGHNESS);
		tbStatDx.color = colorForStat(Stat.DEXTERITY);
		tbStatAg.color = colorForStat(Stat.AGILITY);
		tbStatPe.color = colorForStat(Stat.PERCEPTION);
		tbStatWi.color = colorForStat(Stat.WILLPOWER);
		tbStatAr.color = colorForStat(Stat.ARCANUM);
		tbStatAv.color = colorForStat(Stat.AVATAR);

		tbStatDe.text = "" + entity.getArmorClass();
		tbStatAt.text = "" + entity.getArmorThickness();
		tbStatSpeed.text = "" + (int)entity.getSpeed();
		tbStatSpeed.color = colorForSpeed();
		tbStatLevel.text = "" + entity.level;

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

		int statusChars = 0;
		statRows.get(8).close();
		statRows.set(8, new TextBlock("", 0, 8));
		parent.addChild(statRows.get(8));
		// TODO multiple rows
		for (EntityProc ep : entity.allEntityProcsIncludingEquipment().collect(Collectors.toList())) {
			TextBlock tb = ep.proc.getStatusBlock(entity);
			if (tb != null) {
				tb.x = statusChars;
				statusChars += tb.text.length() + 1;
				statRows.get(8).addChild(tb);
			}
		}

		if (player.maxElementCharges.get(Element.WATER) != null) {
			tbElementWater.text = "W: " + player.currentElementCharges.get(Element.WATER) + "/" + player.maxElementCharges.get(Element.WATER);
			tbElementFire.text = "F: " + player.currentElementCharges.get(Element.FIRE) + "/" + player.maxElementCharges.get(Element.FIRE);
			tbElementLightning.text = "L: " + player.currentElementCharges.get(Element.LIGHTNING) + "/" + player.maxElementCharges.get(Element.LIGHTNING);
			tbElementNaturae.text = "N: " + player.currentElementCharges.get(Element.NATURAE) + "/" + player.maxElementCharges.get(Element.NATURAE);
		}

		WindowEngine.setDirty(UIManager.NAME_STATBOX);
		parent.compile();
	}

	private Color colorForStat(Stat stat) {
		Entity entity = Game.getPlayerEntity();
		int originalStat = entity.statblock.get(stat);
		int modifiedStat = entity.getStat(stat);
		if (originalStat > modifiedStat) {
			return Color.RED;
		} else if (originalStat < modifiedStat) {
			return Color.GREEN;
		} else {
			return Color.WHITE;
		}
	}

	private Color colorForSpeed() {
		Entity entity = Game.getPlayerEntity();
		int originalStat = entity.statblock.speed;
		int modifiedStat = (int)entity.getSpeed();
		if (originalStat > modifiedStat) {
			return Color.RED;
		} else if (originalStat < modifiedStat) {
			return Color.GREEN;
		} else {
			return Color.WHITE;
		}
	}

	@Override
	public void close() {
		parent.close();
	}

}
