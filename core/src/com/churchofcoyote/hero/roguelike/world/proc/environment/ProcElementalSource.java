package com.churchofcoyote.hero.roguelike.world.proc.environment;

import com.churchofcoyote.hero.roguelike.world.Element;
import com.churchofcoyote.hero.roguelike.world.Entity;
import com.churchofcoyote.hero.roguelike.world.proc.Proc;

public class ProcElementalSource extends Proc {
    public Element element = null;

    @Override
    public Element providesElement(Entity entity) {
        return element;
    }

    @Override
    public int drawElement(Entity entity, Entity actor, int max) {
        return max;
    }
}
