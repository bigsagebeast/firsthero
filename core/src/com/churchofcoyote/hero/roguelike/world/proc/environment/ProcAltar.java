package com.churchofcoyote.hero.roguelike.world.proc.environment;

import com.churchofcoyote.hero.glyphtile.EntityGlyph;
import com.churchofcoyote.hero.roguelike.world.Entity;
import com.churchofcoyote.hero.roguelike.world.enums.Alignment;
import com.churchofcoyote.hero.roguelike.world.proc.Proc;

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
