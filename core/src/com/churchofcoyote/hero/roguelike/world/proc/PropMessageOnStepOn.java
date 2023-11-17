package com.churchofcoyote.hero.roguelike.world.proc;

import com.churchofcoyote.hero.roguelike.game.Game;
import com.churchofcoyote.hero.roguelike.world.Entity;

public class PropMessageOnStepOn extends Proc {

    private String text;

    public PropMessageOnStepOn(Entity e, String text) {
        super(e);
        this.text = text;
    }

    @Override
    public void postBeSteppedOn(Entity actor) {
        if (active) {
            // TODO messages for NPCs as well?
            if (actor == Game.getPlayerEntity()) {
                Game.announce(text);
            }
        }
        active = false;
    }
}
