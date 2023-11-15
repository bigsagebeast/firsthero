package com.churchofcoyote.hero.roguelike.world.proc;

import com.churchofcoyote.hero.roguelike.game.Game;
import com.churchofcoyote.hero.roguelike.world.Entity;

public class ProcPlayer extends ProcMover {

    public ProcPlayer(Entity e) {
        super(e);
    }

    @Override
    public Boolean preDoPickup(Entity target) { return true; }
    @Override
    public void postDoPickup(Entity target) {
        //Game.announce("You pick up the " + target.name + ".");
    }
}
