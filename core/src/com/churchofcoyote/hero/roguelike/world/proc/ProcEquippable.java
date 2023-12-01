package com.churchofcoyote.hero.roguelike.world.proc;

import com.churchofcoyote.hero.roguelike.world.BodyPart;
import com.churchofcoyote.hero.roguelike.world.Entity;

public class ProcEquippable extends Proc {
    public BodyPart equipmentFor;
    protected ProcEquippable() {}
    public ProcEquippable (Entity e, BodyPart equipmentFor) {
        super(e);
        this.equipmentFor = equipmentFor;
    }
    public Boolean preBeEquipped(BodyPart bp, Entity actor) { return true; }
    public void postBeEquipped(BodyPart bp, Entity actor) {}
    public Boolean preBeUnequipped(BodyPart bp, Entity actor) { return null; }
    public void postBeUnequipped(BodyPart bp, Entity actor) {}

    @Override
    public Proc clone(Entity other) {
        return new ProcEquippable(other, equipmentFor);
    }
}
