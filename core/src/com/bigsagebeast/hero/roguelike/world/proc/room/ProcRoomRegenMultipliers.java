package com.bigsagebeast.hero.roguelike.world.proc.room;

import com.bigsagebeast.hero.roguelike.world.Entity;
import com.bigsagebeast.hero.roguelike.world.proc.Proc;

public class ProcRoomRegenMultipliers extends Proc {
    float hpMultiplier = 1.0f;
    float spMultiplier = 1.0f;
    float dpMultiplier = 1.0f;

    @Override
    public float getRegenHpMultiplier(Entity entity, Entity actor) { return hpMultiplier; }

    @Override
    public float getRegenSpMultiplier(Entity entity, Entity actor) { return spMultiplier; }

    @Override
    public float getRegenDpMultiplier(Entity entity, Entity actor) { return dpMultiplier; }

}
