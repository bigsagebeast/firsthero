package com.churchofcoyote.hero.roguelike.world.proc;

import com.churchofcoyote.hero.GameLoop;
import com.churchofcoyote.hero.roguelike.game.Game;
import com.churchofcoyote.hero.roguelike.world.Entity;

public class PropPopupOnSeen extends ProcEntity {

    private String text;

    public PropPopupOnSeen(Entity e, String text) {
        super(e);
        this.text = text;
    }

    public void actPlayerLos() {
        if (active) {
            GameLoop.popupModule.createPopup(text, 3f, entity, 0.75f);
        }
        active = false;
    }
}
