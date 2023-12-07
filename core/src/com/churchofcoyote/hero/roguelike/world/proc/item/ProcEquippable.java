package com.churchofcoyote.hero.roguelike.world.proc.item;

import com.churchofcoyote.hero.roguelike.world.BodyPart;
import com.churchofcoyote.hero.roguelike.world.Entity;
import com.churchofcoyote.hero.roguelike.world.proc.Proc;

public class ProcEquippable extends Proc {
    public BodyPart equipmentFor;
    protected ProcEquippable() { super(); }
    public ProcEquippable (BodyPart equipmentFor) {
        super();
        this.equipmentFor = equipmentFor;
    }
    public Boolean preBeEquipped(BodyPart bp, Entity actor) { return true; }
    public void postBeEquipped(BodyPart bp, Entity actor) {}
    public Boolean preBeUnequipped(BodyPart bp, Entity actor) { return null; }
    public void postBeUnequipped(BodyPart bp, Entity actor) {}

    @Override
    public Proc clone() {
        return new ProcEquippable(equipmentFor);
    }
}