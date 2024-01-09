package com.bigsagebeast.hero.roguelike.spells;

import com.badlogic.gdx.graphics.Color;
import com.bigsagebeast.hero.roguelike.game.CombatLogic;
import com.bigsagebeast.hero.roguelike.world.Element;
import com.bigsagebeast.hero.util.Compass;
import com.bigsagebeast.hero.roguelike.game.Game;
import com.bigsagebeast.hero.roguelike.world.Entity;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static com.bigsagebeast.hero.roguelike.game.Game.announceVis;

public class SpellWaterBlast extends Spell {
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
        return "Water Blast";
    }

    @Override
    public float getRange(Entity caster) {
        return 6;
    }

    @Override
    public int getCost(Entity caster) {
        return 5;
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
        cost.put(Element.WATER, 1);
        return cost;
    }

    @Override
    public void affectTargets(Entity caster, Collection<Entity> targets, Compass dir) {
        targets.stream().sorted(Comparator.comparing(t -> -t.pos.distance(caster.pos))).collect(Collectors.toList());
        for (Entity target : targets) {
            affectTarget(caster, target, dir);
        }
    }

    @Override
    public void affectTarget(Entity caster, Entity target, Compass dir) {
        if (CombatLogic.castAttempt(caster, target, this)) {
            CombatLogic.castDamage(caster, target, this, 8);
        }
        if (target.dead) {
            return;
        }
        boolean pushed = Game.pushBy(target, dir.getX(), dir.getY());
        if (pushed) {
            announceVis(target, caster, "You are pushed back!",
                    target.getVisibleNameDefinite() + " is pushed back!",
                    target.getVisibleNameDefinite() + " is pushed back!",
                    null);
        }
    }

    @Override
    public Color getAnimationColor() {
        return Color.CYAN;
    }

    public void announceHitWithoutKill(Entity caster, Entity target) {
        Game.announceVis(target, caster, "You are pummeled with water.",
                target.getVisibleNameDefinite() + " is pummeled with water.",
                target.getVisibleNameDefinite() + " is pummeled with water.", null);
    }

    public void announceHitWithKill(Entity caster, Entity target) {
        Game.announceVis(target, caster, "You are pummeled to death by water!",
                target.getVisibleNameDefinite() + " is pummeled to death by water!",
                target.getVisibleNameDefinite() + " is pummeled to death by water!", null);
    }




}
