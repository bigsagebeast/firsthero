package com.bigsagebeast.hero.roguelike.world.proc.intrinsic;

import com.bigsagebeast.hero.enums.DamageType;
import com.bigsagebeast.hero.roguelike.world.Entity;
import com.bigsagebeast.hero.roguelike.world.proc.Proc;

import java.util.Collections;
import java.util.List;

public class ProcResistDamageType extends Proc {
    List<DamageType> damageTypes;
    DamageType damageType;
    @Override
    public List<DamageType> provideDamageTypeResist(Entity entity) {
        if (damageTypes != null) {
            return damageTypes;
        }
        return Collections.singletonList(damageType);
    }
}
