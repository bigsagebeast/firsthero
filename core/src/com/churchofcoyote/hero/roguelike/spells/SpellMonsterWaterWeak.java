package com.churchofcoyote.hero.roguelike.spells;

import com.badlogic.gdx.graphics.Color;
import com.churchofcoyote.hero.roguelike.game.CombatLogic;
import com.churchofcoyote.hero.roguelike.game.Game;
import com.churchofcoyote.hero.roguelike.world.Element;
import com.churchofcoyote.hero.roguelike.world.Entity;
import com.churchofcoyote.hero.util.Compass;

import java.util.HashMap;
import java.util.Map;

public class SpellMonsterWaterWeak extends Spell {
    @Override
    public TargetType getTargetType() {
        return TargetType.BEAM;
    }

    @Override
    public String getName() {
        return "Monster Water Weak";
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
        return Color.valueOf("0000ff");
    }

    @Override
    public boolean isAnimationStars() {
        return false;
    }

    @Override
    public void announceCast(Entity caster, Entity target) {
        Game.announceVis(caster, null, "You breathe water.",
                null,
                caster.getVisibleNameThe() + " breathes water.",
                "You hear rushing water.");
    }

    @Override
    public void announceHitWithoutKill(Entity caster, Entity target) {
        Game.announceVis(target, caster, "You are flooded by water.",
                target.getVisibleNameThe() + " is flooded by water.",
                target.getVisibleNameThe() + " is flooded by water.", null);
    }

    @Override
    public void announceHitWithKill(Entity caster, Entity target) {
        Game.announceVis(target, caster, "You are drowned!",
                target.getVisibleNameThe() + " is drowned!",
                target.getVisibleNameThe() + " is drowned!", null);
    }




}
