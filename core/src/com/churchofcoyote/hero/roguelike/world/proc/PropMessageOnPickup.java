package com.churchofcoyote.hero.roguelike.world.proc;

import com.churchofcoyote.hero.GameLoop;
import com.churchofcoyote.hero.roguelike.game.Game;
import com.churchofcoyote.hero.roguelike.world.Entity;

public class PropMessageOnPickup extends Proc {

    private String text;

    public PropMessageOnPickup(Entity e, String text) {
        super(e);
        this.text = text;
    }

    @Override
    public void postBePickedUp(Entity actor) {
        if (active) {
            // TODO only for player?
            Game.announce(text);
        }
        active = false;
    }
}
