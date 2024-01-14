package com.bigsagebeast.hero.roguelike.world.proc;

import com.bigsagebeast.hero.roguelike.world.Entity;
import com.bigsagebeast.hero.roguelike.game.Game;

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
            Game.announceLoud(text);
        }
        active = false;
    }
}
