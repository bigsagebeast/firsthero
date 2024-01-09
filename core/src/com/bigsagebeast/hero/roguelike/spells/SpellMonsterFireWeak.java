package com.bigsagebeast.hero.roguelike.spells;

import com.badlogic.gdx.graphics.Color;
import com.bigsagebeast.hero.roguelike.game.CombatLogic;
import com.bigsagebeast.hero.roguelike.world.Element;
import com.bigsagebeast.hero.util.Compass;
import com.bigsagebeast.hero.roguelike.game.Game;
import com.bigsagebeast.hero.roguelike.world.Entity;

import java.util.HashMap;
import java.util.Map;

public class SpellMonsterFireWeak extends Spell {
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
        return "Monster Fire Weak";
    }

    @Override
    public Float getRange(Entity caster) {
        return 5.0f;
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
        return Color.valueOf("ff0000");
    }

    @Override
    public void announceCast(Entity caster, Entity target) {
        Game.announceVis(caster, null, "You breathe fire.",
                null,
                caster.getVisibleNameDefinite() + " breathes flame.",
                "You hear a puff of flame.");
    }

    @Override
    public void announceHitWithoutKill(Entity caster, Entity target) {
        Game.announceVis(target, caster, "You are burned.",
                target.getVisibleNameDefinite() + " is burned.",
                target.getVisibleNameDefinite() + " is burned.", null);
    }

    @Override
    public void announceHitWithKill(Entity caster, Entity target) {
        Game.announceVis(target, caster, "You are burned to death!",
                target.getVisibleNameDefinite() + " is burned to death!",
                target.getVisibleNameDefinite() + " is burned to death!", null);
    }




}
