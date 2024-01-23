package com.bigsagebeast.hero.roguelike.spells;

import com.bigsagebeast.hero.enums.Stat;
import com.bigsagebeast.hero.roguelike.game.CombatLogic;
import com.bigsagebeast.hero.roguelike.game.EquipmentScaling;
import com.bigsagebeast.hero.roguelike.world.Element;
import com.bigsagebeast.hero.util.Compass;
import com.bigsagebeast.hero.roguelike.world.Entity;

import java.util.HashMap;
import java.util.Map;

public class SpellRootSpear extends Spell {
    public SpellRootSpear() {
        scaling.put(Stat.ARCANUM, new EquipmentScaling());
        scaling.get(Stat.ARCANUM).damage = 1f;
    }

    @Override
    public SpellType getSpellType() {
        return SpellType.ARCANUM;
    }

    @Override
    public TargetType getTargetType() {
        return TargetType.BOLT;
    }

    @Override
    public String getName() {
        return "Root Spear";
    }

    @Override
    public String getDescription() {
        return "Fires penetrating and high-damage physical projectiles of wood that cannot be resisted.";
    }

    @Override
    public Float getBaseDamage() {
        return 8.0f;
    }

    @Override
    public Float getBaseRange() {
        return 8.0f;
    }

    @Override
    public int getBaseCost(Entity caster) {
        return 5;
    }

    @Override
    public boolean isDodgeable() {
        return true;
    }

    @Override
    public boolean isResistable() {
        return false;
    }

    @Override
    public Map<Element, Integer> getElementCost(Entity caster) {
        HashMap<Element, Integer> cost = new HashMap<>();
        cost.put(Element.NATURAE, 2);
        return cost;
    }

    @Override
    public void affectTarget(Entity actor, Entity target, Compass dir) {
        if (CombatLogic.castAttempt(actor, target, this)) {
            CombatLogic.castDamage(actor, target, this, getDamage(actor));
        }
    }
}
