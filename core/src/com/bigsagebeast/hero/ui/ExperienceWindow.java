package com.bigsagebeast.hero.ui;

import com.badlogic.gdx.graphics.Color;
import com.bigsagebeast.hero.engine.WindowEngine;
import com.bigsagebeast.hero.roguelike.world.Entity;
import com.bigsagebeast.hero.roguelike.game.Game;

public class ExperienceWindow extends ProgressWindow {

    @Override
    protected String getWindowName() {
        return UIManager.NAME_EXPERIENCE;
    }

    @Override
    protected String getLabel() {
        return "Exp";
    }

    @Override
    protected Color getColor(int current, int max) {
        return Color.GOLD;
    }

    @Override
    public void update() {
        WindowEngine.setDirty(UIManager.NAME_EXPERIENCE);
        Entity pc = Game.getPlayerEntity();
        if (pc == null) {
            return;
        }
        setValue(pc.experience, pc.experienceToNext);
    }
}
