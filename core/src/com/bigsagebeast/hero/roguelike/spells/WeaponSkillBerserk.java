package com.bigsagebeast.hero.roguelike.spells;

import com.bigsagebeast.hero.enums.Stat;
import com.bigsagebeast.hero.enums.WeaponType;
import com.bigsagebeast.hero.roguelike.game.EquipmentScaling;
import com.bigsagebeast.hero.roguelike.game.Game;
import com.bigsagebeast.hero.roguelike.world.Element;
import com.bigsagebeast.hero.roguelike.world.Entity;
import com.bigsagebeast.hero.roguelike.world.proc.effect.ProcEffectBerserk;
import com.bigsagebeast.hero.roguelike.world.proc.effect.ProcEffectConfusion;
import com.bigsagebeast.hero.roguelike.world.proc.effect.ProcEffectTimedMissileDeflection;
import com.bigsagebeast.hero.util.Compass;
import com.bigsagebeast.hero.util.Point;

import java.util.Collections;
import java.util.Map;

public class WeaponSkillBerserk extends Spell {
    public WeaponSkillBerserk() {
    }

    @Override
    public TargetType getTargetType() {
        return TargetType.PERSONAL;
    }

    @Override
    public SpellType getSpellType() {
        return SpellType.WEAPON_SKILL;
    }

    @Override
    public WeaponType getWeaponType() {
        return WeaponType.AXE;
    }

    @Override
    public String getName() {
        return "Berserk";
    }

    @Override
    public String getDescription() { return "Boost your agility by +8 and your weapon penetration by +4 for a time. Repeated usages stack."; };

    @Override
    public int getBaseCost(Entity caster) { return 15; }

    @Override
    public Float getBaseDuration() {
        return 10f;
    }

    @Override
    public Map<Element, Integer> getElementCost(Entity caster) { return Collections.singletonMap(Element.FIRE, 2); }

    @Override
    public void affectTarget(Entity actor, Entity target, Compass dir) {
        ProcEffectBerserk existing = (ProcEffectBerserk) target.getProcByType(ProcEffectBerserk.class);
        if (existing != null) {
            existing.turnsRemaining = Math.max(existing.turnsRemaining, getDuration(actor));
        } else {
            ProcEffectBerserk proc = new ProcEffectBerserk();
            proc.turnsRemaining = getDuration(actor);
            target.addProc(proc);
        }
    }
}
