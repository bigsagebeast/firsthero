package com.churchofcoyote.hero.roguelike.world.proc;

import com.churchofcoyote.hero.GameLoop;
import com.churchofcoyote.hero.roguelike.world.Entity;

public class ProcPopupOnSeen extends Proc {

    private String text;

    public ProcPopupOnSeen() {}
    public ProcPopupOnSeen(Entity e, String text) {
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
