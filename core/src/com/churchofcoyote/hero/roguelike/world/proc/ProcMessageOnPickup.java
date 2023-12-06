package com.churchofcoyote.hero.roguelike.world.proc;

import com.churchofcoyote.hero.GameLoop;
import com.churchofcoyote.hero.roguelike.game.Game;
import com.churchofcoyote.hero.roguelike.world.Entity;

public class ProcMessageOnPickup extends Proc {

    private String text;

    public ProcMessageOnPickup() { super(); }
    public ProcMessageOnPickup(String text) {
        this();
        this.text = text;
    }

    @Override
    public void postBePickedUp(Entity entity, Entity actor) {
        if (active) {
            // TODO only for player?
            Game.announce(text);
        }
        active = false;
    }
}
