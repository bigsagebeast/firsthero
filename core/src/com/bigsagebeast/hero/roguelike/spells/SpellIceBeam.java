package com.bigsagebeast.hero.roguelike.spells;

import com.badlogic.gdx.graphics.Color;
import com.bigsagebeast.hero.enums.Stat;
import com.bigsagebeast.hero.roguelike.game.CombatLogic;
import com.bigsagebeast.hero.roguelike.game.Game;
import com.bigsagebeast.hero.roguelike.world.Element;
import com.bigsagebeast.hero.roguelike.world.Entity;
import com.bigsagebeast.hero.roguelike.world.proc.effect.ProcEffectSlow;
import com.bigsagebeast.hero.util.Compass;

import java.util.HashMap;
import java.util.Map;

public class SpellIceBeam extends Spell {
    @Override
    public SpellType getSpellType() {
        return SpellType.ARCANUM;
    }

    @Override
    public TargetType getTargetType() {
        return TargetType.BEAM;
    }

    @Override
    public String getName() {
        return "Icebeam";
    }

    @Override
    public Float getRange(Entity caster) {
        return 5.0f;
    }

    @Override
    public Integer getDuration(Entity caster) {
        int turns = 5;
        turns += (Math.max(caster.getStat(Stat.ARCANUM), 6) - 8) / 2;
        return turns;
    }

    @Override
    public int getCost(Entity caster) {
        return 15;
    }

    @Override
    public boolean isDodgeable() {
        return true;
    }

    @Override
    public boolean isResistable() {
        return true;
    }

    @Override
    public Map<Element, Integer> getElementCost(Entity caster) {
        HashMap<Element, Integer> cost = new HashMap<>();
        cost.put(Element.WATER, 3);
        return cost;
    }

    @Override
    public void affectTarget(Entity actor, Entity target, Compass dir) {
        if (CombatLogic.castAttempt(actor, target, this)) {
            CombatLogic.castDamage(actor, target, this, 8);
            if (!target.dead) {
                ProcEffectSlow existing = (ProcEffectSlow) target.getProcByType(ProcEffectSlow.class);
                if (existing != null) {
                    existing.turnsRemaining = Math.max(existing.turnsRemaining, getDuration(actor));
                } else {
                    ProcEffectSlow proc = new ProcEffectSlow();
                    proc.turnsRemaining = getDuration(actor);
                    target.addProc(proc);
                }
            }
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
