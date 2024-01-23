package com.bigsagebeast.hero.roguelike.spells;

import com.bigsagebeast.hero.enums.Stat;
import com.bigsagebeast.hero.enums.WeaponType;
import com.bigsagebeast.hero.roguelike.game.EquipmentScaling;
import com.bigsagebeast.hero.roguelike.world.Element;
import com.bigsagebeast.hero.roguelike.world.Entity;
import com.bigsagebeast.hero.roguelike.world.proc.effect.ProcEffectTimedMissileDeflection;
import com.bigsagebeast.hero.util.Compass;

import java.util.Collections;
import java.util.Map;

public class WeaponSkillDeflection extends Spell {
    public WeaponSkillDeflection() {
        scaling.put(Stat.AGILITY, new EquipmentScaling());
        scaling.get(Stat.AGILITY).duration = 2f;
    }

    @Override
    public SpellType getSpellType() {
        return SpellType.WEAPON_SKILL;
    }

    @Override
    public WeaponType getWeaponType() {
        return WeaponType.BROAD_BLADE;
    }

    @Override
    public TargetType getTargetType() {
        return TargetType.PERSONAL;
    }

    @Override
    public String getName() {
        return "Deflection";
    }

    @Override
    public String getDescription() {
        return "Spin your weapon to deflect all ranged projectiles (not spells) for a time. Repeated usage stacks the duration.";
    }

    @Override
    public Float getBaseDuration() {
        return 10f;
    }

    @Override
    public int getBaseCost(Entity caster) {
        return 10;
    }

    @Override
    public Map<Element, Integer> getElementCost(Entity caster) { return Collections.singletonMap(Element.LIGHTNING, 1); }

    @Override
    public void affectTarget(Entity actor, Entity target, Compass dir) {
        ProcEffectTimedMissileDeflection existing = (ProcEffectTimedMissileDeflection) target.getProcByType(ProcEffectTimedMissileDeflection.class);
        if (existing != null) {
            existing.turnsRemaining = Math.max(existing.turnsRemaining, getDuration(actor));
        } else {
            ProcEffectTimedMissileDeflection proc = new ProcEffectTimedMissileDeflection();
            proc.turnsRemaining = getDuration(actor);
            target.addProc(proc);
        }
    }
}
