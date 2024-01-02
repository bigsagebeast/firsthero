package com.bigsagebeast.hero.roguelike.world.proc.item;

import com.bigsagebeast.hero.roguelike.world.BodyPart;
import com.bigsagebeast.hero.roguelike.world.Entity;
import com.bigsagebeast.hero.roguelike.world.proc.Proc;

public class ProcEquippable extends Proc {
    public BodyPart equipmentFor;
    public ProcEquippable() { super(); }
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
