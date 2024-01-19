package com.bigsagebeast.hero.roguelike.spells;

import com.badlogic.gdx.graphics.Color;
import com.bigsagebeast.hero.enums.Stat;
import com.bigsagebeast.hero.roguelike.game.Game;
import com.bigsagebeast.hero.roguelike.world.Element;
import com.bigsagebeast.hero.roguelike.world.Entity;
import com.bigsagebeast.hero.roguelike.world.proc.effect.ProcEffectOakStrength;
import com.bigsagebeast.hero.util.Compass;

import java.util.HashMap;
import java.util.Map;

public class SpellOakStrength extends Spell {
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
    public Integer getDuration(Entity caster) {
        int turns = 30;
        turns += Stat.getScaling(caster.getStat(Stat.ARCANUM), 3);
        return turns;
    }

    @Override
    public int getCost(Entity caster) {
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

    @Override
    public Color getAnimationColor() {
        return Color.CYAN;
    }

    public void announceHitWithoutKill(Entity caster, Entity target) {
        Game.announceVis(target, caster, "You are chilled.",
                target.getVisibleNameDefinite() + " is chilled.",
                target.getVisibleNameDefinite() + " is chilled.", null);
    }

    public void announceHitWithKill(Entity caster, Entity target) {
        Game.announceVis(target, caster, "You are frozen solid!",
                target.getVisibleNameDefinite() + " is frozen solid!",
                target.getVisibleNameDefinite() + " is frozen solid!", null);
    }
}
