package com.bigsagebeast.hero.ui;

import com.badlogic.gdx.graphics.Color;
import com.bigsagebeast.hero.engine.WindowEngine;
import com.bigsagebeast.hero.roguelike.game.Game;
import com.bigsagebeast.hero.roguelike.world.Entity;

public class SpellPointWindow extends ProgressWindow {

    @Override
    protected String getWindowName() {
        return UIManager.NAME_SPELL_POINTS;
    }

    @Override
    protected String getLabel() {
        return "SP";
    }

    @Override
    protected Color getColor(int current, int max) {
        if (current >= (max*2/3)) {
            return Color.valueOf("00cccc");
        } else if (current >= (max/3)) {
            return Color.valueOf("007777");
        } else {
            return Color.GRAY;
        }
    }

    @Override
    public void update() {
        WindowEngine.setDirty(UIManager.NAME_SPELL_POINTS);
        Entity pc = Game.getPlayerEntity();
        if (pc == null) {
            return;
        }
        setValue(pc.spellPoints, pc.maxSpellPoints);
    }
}
