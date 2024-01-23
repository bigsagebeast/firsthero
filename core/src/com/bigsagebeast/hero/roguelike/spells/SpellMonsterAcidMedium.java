package com.bigsagebeast.hero.roguelike.spells;

import com.badlogic.gdx.graphics.Color;
import com.bigsagebeast.hero.enums.DamageType;
import com.bigsagebeast.hero.roguelike.game.CombatLogic;
import com.bigsagebeast.hero.roguelike.game.Game;
import com.bigsagebeast.hero.roguelike.world.Element;
import com.bigsagebeast.hero.roguelike.world.Entity;
import com.bigsagebeast.hero.util.Compass;

import java.util.HashMap;
import java.util.Map;

public class SpellMonsterAcidMedium extends Spell {
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
        return "Monster Acid Medium";
    }

    @Override
    public Float getRange(Entity caster) {
        return 5.0f;
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
        return true;
    }

    @Override
    public Map<Element, Integer> getElementCost(Entity caster) {
        return new HashMap<>();
    }

    @Override
    public void affectTarget(Entity actor, Entity target, Compass dir) {
        if (CombatLogic.castAttempt(actor, target, this)) {
            CombatLogic.castDamage(actor, target, this, 10, DamageType.ACID);
        }
    }

    @Override
    public Color getAnimationColor() {
        return Color.valueOf("aaff00");
    }

    @Override
    public void announceCast(Entity caster, Entity target) {
        Game.announceVis(caster, null, "You spit acid.",
                null,
                caster.getVisibleNameDefinite() + " spits acid.",
                "You hear wet splashing.");
    }

    @Override
    public void announceHitWithoutKill(Entity caster, Entity target) {
        Game.announceVis(target, caster, "You are burned by the acid.",
                target.getVisibleNameDefinite() + " is burned by the acid.",
                target.getVisibleNameDefinite() + " is burned by the acid.", null);
    }

    @Override
    public void announceHitWithKill(Entity caster, Entity target) {
        Game.announceVis(target, caster, "You are melted to death!",
                target.getVisibleNameDefinite() + " is melted to death!",
                target.getVisibleNameDefinite() + " is melted to death!", null);
    }




}
