package com.churchofcoyote.hero.roguelike.world.proc;

import com.churchofcoyote.hero.roguelike.game.Player;
import com.churchofcoyote.hero.roguelike.world.Entity;
import com.churchofcoyote.hero.roguelike.world.EntityTracker;

public class ProcMover extends Proc {

    public int targetEntityId = EntityTracker.NONE;

    public boolean isMover() {
        return true;
    }

    public boolean hasAction() {
        return true;
    }

    public void act(Entity entity)
    {
        setDelay(entity, 1000);
    }

    @Override
    public Boolean preDoPickup(Entity entity, Entity target) { return false; }
    @Override
    public void postDoPickup(Entity entity, Entity target) {}

    public boolean isPeacefulToPlayer() {
        return false;//peaceful;
    }
}
