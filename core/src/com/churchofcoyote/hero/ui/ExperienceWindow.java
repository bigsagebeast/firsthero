package com.churchofcoyote.hero.ui;

import com.badlogic.gdx.graphics.Color;
import com.churchofcoyote.hero.engine.WindowEngine;
import com.churchofcoyote.hero.roguelike.game.Game;
import com.churchofcoyote.hero.roguelike.world.Entity;

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
