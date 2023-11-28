package com.churchofcoyote.hero.ui;

import com.badlogic.gdx.graphics.Color;
import com.churchofcoyote.hero.engine.WindowEngine;
import com.churchofcoyote.hero.module.RoguelikeModule;
import com.churchofcoyote.hero.roguelike.game.Game;
import com.churchofcoyote.hero.roguelike.world.Entity;
import com.churchofcoyote.hero.text.TextBlock;

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
