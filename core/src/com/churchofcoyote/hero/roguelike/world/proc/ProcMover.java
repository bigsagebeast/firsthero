package com.churchofcoyote.hero.roguelike.world.proc;

import com.churchofcoyote.hero.roguelike.game.Player;
import com.churchofcoyote.hero.roguelike.world.Entity;
import com.churchofcoyote.hero.roguelike.world.EntityTracker;

public class ProcMover extends Proc {

    public int targetEntityId = EntityTracker.NONE;


    protected ProcMover() {}
    public ProcMover(Entity e) {
        super(e);
    }


    public boolean isMover() {
        return true;
    }

    public boolean hasAction() {
        return true;
    }

    public void act()
    {
        setDelay(1000);
    }

    @Override
    public Boolean preDoPickup(Entity target) { return false; }
    @Override
    public void postDoPickup(Entity target) {}

    public boolean isPeacefulToPlayer() {
        return false;//peaceful;
    }
}
