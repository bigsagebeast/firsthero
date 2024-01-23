package com.bigsagebeast.hero.roguelike.spells;

import com.badlogic.gdx.graphics.Color;
import com.bigsagebeast.hero.enums.Stat;
import com.bigsagebeast.hero.roguelike.game.EquipmentScaling;
import com.bigsagebeast.hero.roguelike.game.Game;
import com.bigsagebeast.hero.roguelike.world.Element;
import com.bigsagebeast.hero.roguelike.world.Entity;
import com.bigsagebeast.hero.roguelike.world.proc.effect.ProcEffectOakStrength;
import com.bigsagebeast.hero.util.Compass;

import java.util.HashMap;
import java.util.Map;

public class SpellOakStrength extends Spell {
    public SpellOakStrength() {
        scaling.put(Stat.ARCANUM, new EquipmentScaling());
        scaling.get(Stat.ARCANUM).duration = 3f;
    }

    @Override
    public SpellType getSpellType() {
        return SpellType.ARCANUM;
    }

    @Override
    public TargetType getTargetType() {
        return TargetType.PERSONAL;
    }

    @Override
    public String getName() {
        return "Oak Strength";
    }

    @Override
    public String getDescription() {
        return "Temporarily boosts your strength and toughness by 4, +/- 0.5 per point of Arcanum. This toughness boost effectively grants temporary hit points. Multiple castings stack the duration.";
    }

    @Override
    public Float getBaseDuration() {
        return 30f;
    }

    @Override
    public int getBaseCost(Entity caster) {
        return 15;
    }

    @Override
    public Map<Element, Integer> getElementCost(Entity caster) {
        HashMap<Element, Integer> cost = new HashMap<>();
        cost.put(Element.NATURAE, 1);
        return cost;
    }

    @Override
    public void affectTarget(Entity actor, Entity target, Compass dir) {

        ProcEffectOakStrength existing = (ProcEffectOakStrength) target.getProcByType(ProcEffectOakStrength.class);
        if (existing != null) {
            existing.turnsRemaining = Math.max(existing.turnsRemaining, getDuration(actor));
        } else {
            ProcEffectOakStrength proc = new ProcEffectOakStrength();
            proc.turnsRemaining = getDuration(actor);
            target.addProc(proc);
        }
    }
}
