package com.churchofcoyote.hero.roguelike.world.proc.environment;

import com.churchofcoyote.hero.roguelike.world.Entity;
import com.churchofcoyote.hero.roguelike.world.proc.Proc;

public class ProcFeature extends Proc {
    protected ProcFeature() { super(); }
    @Override
    public Boolean preBePickedUp(Entity entity, Entity actor) {
        return Boolean.FALSE;
    }
}
