package com.bigsagebeast.hero.roguelike.spells;

import com.badlogic.gdx.graphics.Color;
import com.bigsagebeast.hero.enums.Stat;
import com.bigsagebeast.hero.roguelike.game.CombatLogic;
import com.bigsagebeast.hero.roguelike.game.EquipmentScaling;
import com.bigsagebeast.hero.roguelike.game.Game;
import com.bigsagebeast.hero.roguelike.world.Element;
import com.bigsagebeast.hero.roguelike.world.Entity;
import com.bigsagebeast.hero.util.Compass;

import java.util.HashMap;
import java.util.Map;

public class SpellDebugDeath extends Spell {
    public SpellDebugDeath() {
    }

    @Override
    public SpellType getSpellType() {
        return SpellType.ARCANUM;
    }

    @Override
    public TargetType getTargetType() {
        return TargetType.MELEE;
    }

    @Override
    public String getName() {
        return "Debug Death";
    }

    @Override
    public String getDescription() {
        return "Deal massive damage to an adjacent opponent.";
    }

    @Override
    public Float getBaseDamage() { return 9999f; }

    @Override
    public Float getBaseRange() {
        return 1.0f;
    }

    @Override
    public int getBaseCost(Entity caster) {
        return 0;
    }

    @Override
    public boolean isDodgeable() {
        return false;
    }

    @Override
    public boolean isResistable() {
        return false;
    }

    @Override
    public Map<Element, Integer> getElementCost(Entity caster) {
        HashMap<Element, Integer> cost = new HashMap<>();
        return cost;
    }

    @Override
    public void affectTarget(Entity actor, Entity target, Compass dir) {
        if (CombatLogic.castAttempt(actor, target, this)) {
            CombatLogic.castDamage(actor, target, this, getDamage(actor));
        }
    }

    @Override
    public Color getAnimationColor() {
        return Color.YELLOW;
    }

    @Override
    public String getAnimationChar() {
        return "@";
    }

    public void announceHitWithoutKill(Entity caster, Entity target) {
        Game.announceVis(target, caster, "You are not quite murdered.",
                target.getVisibleNameDefinite() + " is not quite murdered.",
                target.getVisibleNameDefinite() + " is not quite murdered.", null);
    }

    public void announceHitWithKill(Entity caster, Entity target) {
        Game.announceVis(target, caster, "You are murdered!",
                target.getVisibleNameDefinite() + " is murdered!",
                target.getVisibleNameDefinite() + " is murdered!", null);
    }




}
