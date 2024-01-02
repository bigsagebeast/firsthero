package com.bigsagebeast.hero.ui;

import com.badlogic.gdx.graphics.Color;
import com.bigsagebeast.hero.engine.WindowEngine;
import com.bigsagebeast.hero.roguelike.game.Game;
import com.bigsagebeast.hero.roguelike.world.Entity;

public class HitPointWindow extends ProgressWindow {

    @Override
    protected String getWindowName() {
        return UIManager.NAME_HIT_POINTS;
    }

    @Override
    protected String getLabel() {
        return "HP";
    }

    @Override
    protected Color getColor(int current, int max) {
        if (current >= (max*2/3)) {
            return Color.GREEN;
        } else if (current >= (max/3)) {
            return Color.YELLOW;
        } else {
            return Color.RED;
        }
    }

    @Override
    public void update() {
        WindowEngine.setDirty(UIManager.NAME_HIT_POINTS);
        Entity pc = Game.getPlayerEntity();
        if (pc == null) {
            return;
        }
        setValue(pc.hitPoints, pc.maxHitPoints);
    }
}
