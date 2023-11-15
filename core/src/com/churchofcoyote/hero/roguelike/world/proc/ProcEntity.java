package com.churchofcoyote.hero.roguelike.world.proc;

import com.churchofcoyote.hero.roguelike.game.Game;
import com.churchofcoyote.hero.roguelike.world.Entity;

public class ProcEntity {

    public Entity entity;
    public long nextAction = -1;
    public boolean active;

    public ProcEntity(Entity e) {
        entity = e;
        active = true;
    }

    public void setDelay(long delay) {
        nextAction = Game.time + delay;
    }

    public boolean hasAction() { return false; }

    public boolean isMover() {
        return false;
    }

    public void act() {

    }

    public boolean receivePlayerLos() { return false; }
    public void actPlayerLos() {}
}
