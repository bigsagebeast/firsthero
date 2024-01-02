package com.bigsagebeast.hero.roguelike.game;

import com.bigsagebeast.hero.roguelike.world.Entity;
import com.bigsagebeast.hero.roguelike.world.proc.Proc;

public class EntityProc {
    public Entity entity;
    public Proc proc;
    public EntityProc(Entity entity, Proc proc) {
        this.entity = entity;
        this.proc = proc;
    }
}
