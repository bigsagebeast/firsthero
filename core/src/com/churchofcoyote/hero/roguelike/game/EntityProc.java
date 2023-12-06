package com.churchofcoyote.hero.roguelike.game;

import com.churchofcoyote.hero.roguelike.world.Entity;
import com.churchofcoyote.hero.roguelike.world.proc.Proc;

public class EntityProc {
    public Entity entity;
    public Proc proc;
    public EntityProc(Entity entity, Proc proc) {
        this.entity = entity;
        this.proc = proc;
    }
}
