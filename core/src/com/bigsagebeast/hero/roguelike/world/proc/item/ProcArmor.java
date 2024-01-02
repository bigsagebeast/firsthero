package com.bigsagebeast.hero.roguelike.world.proc.item;

import com.bigsagebeast.hero.roguelike.world.proc.Proc;

public class ProcArmor extends Proc {
    int armorClass = 0;
    int armorThickness = 0;
    public ProcArmor() { super(); }
    public ProcArmor(int armorClass, int armorThickness) {
        this();
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
    public Proc clone() {
        return new ProcArmor(armorClass, armorThickness);
    }
}
