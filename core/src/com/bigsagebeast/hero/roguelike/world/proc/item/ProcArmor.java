package com.bigsagebeast.hero.roguelike.world.proc.item;

import com.bigsagebeast.hero.enums.Stat;
import com.bigsagebeast.hero.roguelike.game.EquipmentScaling;
import com.bigsagebeast.hero.roguelike.game.Game;
import com.bigsagebeast.hero.roguelike.world.Entity;
import com.bigsagebeast.hero.roguelike.world.proc.Proc;
import com.bigsagebeast.hero.util.Util;

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
    public String getUnidDescription(Entity entity) {
        StringBuilder sb = new StringBuilder();
        sb.append("Base stats: Armor class ").append(Util.formatFloat(armorClass));
        sb.append(" Armor thickness ").append(Util.formatFloat(armorThickness));
        sb.append(". ");
        return sb.toString();
    }

    @Override
    public int provideArmorClass(Entity entity) {
        return armorClass;
    }

    @Override
    public int provideArmorThickness(Entity entity) {
        return armorThickness;
    }

    @Override
    public Proc clone(Entity entity) {
        return new ProcArmor(armorClass, armorThickness);
    }
}
