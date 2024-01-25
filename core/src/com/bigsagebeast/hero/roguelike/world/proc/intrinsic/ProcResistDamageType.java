package com.bigsagebeast.hero.roguelike.world.proc.intrinsic;

import com.bigsagebeast.hero.enums.DamageType;
import com.bigsagebeast.hero.roguelike.world.Entity;
import com.bigsagebeast.hero.roguelike.world.proc.Proc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ProcResistDamageType extends Proc {
    public List<DamageType> damageTypes;
    public DamageType damageType;
    public ProcResistDamageType() {};
    public ProcResistDamageType(List<DamageType> damageTypes, DamageType damageType) {
        if (damageTypes != null) {
            this.damageTypes = new ArrayList<>();
            this.damageTypes.addAll(damageTypes);
        }
        this.damageType = damageType;
    }

    @Override
    public List<DamageType> provideDamageTypeResist(Entity entity) {
        if (damageTypes != null) {
            return damageTypes;
        }
        return Collections.singletonList(damageType);
    }

    @Override
    public int getDescriptionPriority(Entity entity) {
        return 1;
    }

    @Override
    public String getIdenDescription(Entity entity) {
        // TODO pluralize
        StringBuilder sb = new StringBuilder();
        sb.append("It grants resistance to ");
        if (damageTypes == null) {
            sb.append(damageType.description);
        } else {
            int count = 0;
            for (DamageType dt : damageTypes) {
                if (count > 0) {
                    sb.append(", ");
                }
                if (count == damageTypes.size() - 1) {
                    sb.append("and ");
                }
                sb.append(dt.description);
                count++;
            }
        }
        sb.append(".");
        return sb.toString();
    }

    @Override
    public Proc clone(Entity entity) {
        return new ProcResistDamageType(damageTypes, damageType);
    }
}
