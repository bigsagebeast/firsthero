package com.bigsagebeast.hero.roguelike.world.proc.environment;

import com.bigsagebeast.hero.roguelike.world.Entity;
import com.bigsagebeast.hero.roguelike.world.proc.Proc;

public class ProcFeature extends Proc {
    public ProcFeature() { super(); }
    @Override
    public Boolean preBePickedUp(Entity entity, Entity actor) {
        return Boolean.FALSE;
    }
}
