package com.churchofcoyote.hero.roguelike.world.proc;

import com.churchofcoyote.hero.roguelike.world.Entity;

public class ProcItem extends Proc {
    public ProcItem(Entity e) {
        super(e);
    }

    @Override
    public Boolean preBePickedUp(Entity actor) { return true; }

    @Override
    public void postBePickedUp(Entity actor) {}

}
