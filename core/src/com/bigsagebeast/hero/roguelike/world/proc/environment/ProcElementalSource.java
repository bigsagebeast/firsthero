package com.bigsagebeast.hero.roguelike.world.proc.environment;

import com.bigsagebeast.hero.roguelike.world.Element;
import com.bigsagebeast.hero.roguelike.world.Entity;
import com.bigsagebeast.hero.roguelike.world.proc.Proc;

public class ProcElementalSource extends Proc {
    public Element element = null;
    public String elementSymbol = null;

    @Override
    public Element providesElement(Entity entity) {
        if (element != null) {
            return element;
        } else {
            return Element.lookup(elementSymbol);
        }
    }

    @Override
    public int drawElement(Entity entity, Entity actor, int max) {
        return max;
    }
}
