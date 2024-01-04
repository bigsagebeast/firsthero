package com.bigsagebeast.hero.roguelike.spells;

import com.badlogic.gdx.graphics.Color;
import com.bigsagebeast.hero.roguelike.game.CombatLogic;
import com.bigsagebeast.hero.roguelike.world.Element;
import com.bigsagebeast.hero.util.Compass;
import com.bigsagebeast.hero.roguelike.game.Game;
import com.bigsagebeast.hero.roguelike.world.Entity;

import java.util.HashMap;
import java.util.Map;

public class SpellMonsterSparkWeak extends Spell {
    @Override
    public SpellType getSpellType() {
        return SpellType.MONSTER;
    }

    @Override
    public TargetType getTargetType() {
        return TargetType.BEAM;
    }

    @Override
    public String getName() {
        return "Monster Spark Weak";
    }

    @Override
    public float getRange(Entity caster) {
        return 5;
    }

    @Override
    public int getCost(Entity caster) {
        return 5;
    }

    @Override
    public Map<Element, Integer> getElementCost(Entity caster) {
        return new HashMap<>();
    }

    @Override
    public void affectTarget(Entity actor, Entity target, Compass dir) {
        if (CombatLogic.castAttempt(actor, target, this)) {
            CombatLogic.castDamage(actor, target, this, 4);
        }
    }

    @Override
    public Color getAnimationColor() {
        return Color.valueOf("ffff00");
    }

    @Override
    public boolean isAnimationStars() {
        return false;
    }

    @Override
    public void announceCast(Entity caster, Entity target) {
        Game.announceVis(caster, null, "You breathe sparks.",
                null,
                caster.getVisibleNameDefinite() + " breathes sparks.",
                "You hear crackling.");
    }

    @Override
    public void announceHitWithoutKill(Entity caster, Entity target) {
        Game.announceVis(target, caster, "You are zapped.",
                target.getVisibleNameDefinite() + " is zapped.",
                target.getVisibleNameDefinite() + " is zapped.", null);
    }

    @Override
    public void announceHitWithKill(Entity caster, Entity target) {
        Game.announceVis(target, caster, "You are zapped to death!",
                target.getVisibleNameDefinite() + " is zapped to death!",
                target.getVisibleNameDefinite() + " is zapped to death!", null);
    }




}
