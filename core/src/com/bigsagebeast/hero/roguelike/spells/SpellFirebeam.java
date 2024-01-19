package com.bigsagebeast.hero.roguelike.spells;

import com.badlogic.gdx.graphics.Color;
import com.bigsagebeast.hero.enums.Stat;
import com.bigsagebeast.hero.roguelike.game.CombatLogic;
import com.bigsagebeast.hero.roguelike.game.EquipmentScaling;
import com.bigsagebeast.hero.util.Compass;
import com.bigsagebeast.hero.roguelike.game.Game;
import com.bigsagebeast.hero.roguelike.world.Element;
import com.bigsagebeast.hero.roguelike.world.Entity;

import java.util.HashMap;
import java.util.Map;

public class SpellFirebeam extends Spell {
    public SpellFirebeam() {
        scaling.put(Stat.ARCANUM, new EquipmentScaling());
        scaling.get(Stat.ARCANUM).damage = 0.5f;
    }
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
        return "Firebeam";
    }

    @Override
    public String getDescription() {
        return "Casts forth a beam of fire that penetrates multiple enemies.";
    }

    @Override
    public Float getBaseRange() {
        return 3.0f;
    }

    @Override
    public Float getBaseDamage() { return 8.0f; }

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
        cost.put(Element.FIRE, 1);
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
        return Color.valueOf("ff0000");
    }

    @Override
    public String getAnimationChar() {
        return "#";
    }

    public void announceHitWithoutKill(Entity caster, Entity target) {
        Game.announceVis(target, caster, "You are burned.",
                target.getVisibleNameDefinite() + " is burned.",
                target.getVisibleNameDefinite() + " is burned.", null);
    }

    public void announceHitWithKill(Entity caster, Entity target) {
        Game.announceVis(target, caster, "You are burned to a crisp!",
                target.getVisibleNameDefinite() + " is burned to a crisp!",
                target.getVisibleNameDefinite() + " is burned to a crisp!", null);
    }
}
