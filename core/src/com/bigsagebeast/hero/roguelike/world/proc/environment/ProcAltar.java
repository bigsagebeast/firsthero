package com.bigsagebeast.hero.roguelike.world.proc.environment;

import com.bigsagebeast.hero.glyphtile.EntityGlyph;
import com.bigsagebeast.hero.roguelike.world.Entity;
import com.bigsagebeast.hero.roguelike.world.enums.Alignment;
import com.bigsagebeast.hero.roguelike.world.proc.Proc;

public class ProcAltar extends Proc {

    public Alignment alignment;

    public Boolean canPrayAt(Entity entity, Entity actor) {
        return Boolean.TRUE;
    }

    public void setAlignment(Entity entity, Alignment alignment) {
        this.alignment = alignment;
        entity.palette = alignment.palette;
        EntityGlyph.updateEntity(entity);
    }
}
