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

public class SpellShockingGrasp extends Spell {
    public SpellShockingGrasp() {
        scaling.put(Stat.ARCANUM, new EquipmentScaling());
        scaling.get(Stat.ARCANUM).damage = 1f;
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
        return "Shocking Grasp";
    }

    @Override
    public String getDescription() {
        return "Electrifies one adjacent opponent for high damage that cannot be dodged.";
    }

    @Override
    public Float getBaseDamage() { return 12f; }

    @Override
    public Float getBaseRange() {
        return 1.0f;
    }

    @Override
    public int getCost(Entity caster) {
        return 7;
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
        cost.put(Element.LIGHTNING, 1);
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
        Game.announceVis(target, caster, "You are shocked.",
                target.getVisibleNameDefinite() + " is shocked.",
                target.getVisibleNameDefinite() + " is shocked.", null);
    }

    public void announceHitWithKill(Entity caster, Entity target) {
        Game.announceVis(target, caster, "You are shocked to death!",
                target.getVisibleNameDefinite() + " is shocked to death!",
                target.getVisibleNameDefinite() + " is shocked to death!", null);
    }




}
