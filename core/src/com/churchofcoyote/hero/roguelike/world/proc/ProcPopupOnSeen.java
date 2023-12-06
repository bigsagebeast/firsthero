package com.churchofcoyote.hero.roguelike.world.proc;

import com.churchofcoyote.hero.GameLoop;
import com.churchofcoyote.hero.roguelike.world.Entity;

public class ProcPopupOnSeen extends Proc {

    private String text;

    public ProcPopupOnSeen() { super(); }
    public ProcPopupOnSeen(String text) {
        this();
        this.text = text;
    }

    @Override
    public void actPlayerLos(Entity entity) {
        if (active) {
            GameLoop.popupModule.createPopup(text, 3f, entity, 0.75f);
        }
        active = false;
    }
}
