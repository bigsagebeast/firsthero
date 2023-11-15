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

    public void act() { }


    // return true if pickup is allowed, false if it's aborted, null if no opinion
    public Boolean preBePickedUp(Entity actor) { return null; }
    public void postBePickedUp(Entity actor) {}

    public Boolean preDoPickup(Entity target) { return null; }
    public void postDoPickup(Entity target) {}

    public void actPlayerLos() {}
}
