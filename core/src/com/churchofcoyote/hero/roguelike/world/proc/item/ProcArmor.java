package com.churchofcoyote.hero.roguelike.world.proc.item;

import com.churchofcoyote.hero.roguelike.world.Entity;
import com.churchofcoyote.hero.roguelike.world.proc.Proc;

public class ProcArmor extends Proc {
    int armorClass = 0;
    int armorThickness = 0;

    public ProcArmor(Entity e, int armorClass, int armorThickness) {
        super(e);
        this.armorClass = armorClass;
        this.armorThickness = armorThickness;
    }

    @Override
    public int provideArmorClass() {
        return armorClass;
    }

    @Override
    public int provideArmorThickness() {
        return armorThickness;
    }

    @Override
    public Proc clone(Entity other) {
        return new ProcArmor(other, armorClass, armorThickness);
    }
}
