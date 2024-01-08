package com.bigsagebeast.hero.roguelike.world.proc;

import com.bigsagebeast.hero.roguelike.world.Entity;

public class ProcJitter extends Proc {
    float strength;

    @Override
    public Float getJitter(Entity entity) { return strength; }
}
