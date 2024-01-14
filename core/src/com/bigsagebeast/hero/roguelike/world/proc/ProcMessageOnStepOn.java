package com.bigsagebeast.hero.roguelike.world.proc;

import com.bigsagebeast.hero.roguelike.world.Entity;
import com.bigsagebeast.hero.roguelike.game.Game;

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
                Game.announceLoud(text);
            }
        }
        active = false;
    }
}
