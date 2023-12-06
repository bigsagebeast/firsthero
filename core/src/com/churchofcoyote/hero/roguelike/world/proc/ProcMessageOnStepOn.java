package com.churchofcoyote.hero.roguelike.world.proc;

import com.churchofcoyote.hero.roguelike.game.Game;
import com.churchofcoyote.hero.roguelike.world.Entity;

public class ProcMessageOnStepOn extends Proc {

    private String text;

    public ProcMessageOnStepOn() { super(); }
    public ProcMessageOnStepOn(String text) {
        this();
        this.text = text;
    }

    @Override
    public void postBeSteppedOn(Entity entity, Entity actor) {
        if (active) {
            // TODO messages for NPCs as well?
            if (actor == Game.getPlayerEntity()) {
                Game.announce(text);
            }
        }
        active = false;
    }
}
