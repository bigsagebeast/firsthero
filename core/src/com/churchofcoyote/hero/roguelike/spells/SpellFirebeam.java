package com.churchofcoyote.hero.roguelike.spells;

import com.badlogic.gdx.graphics.Color;
import com.churchofcoyote.hero.roguelike.game.CombatLogic;
import com.churchofcoyote.hero.roguelike.game.Game;
import com.churchofcoyote.hero.roguelike.world.Element;
import com.churchofcoyote.hero.roguelike.world.Entity;

import java.util.HashMap;
import java.util.Map;

public class SpellFirebeam extends Spell {
    @Override
    public TargetType getTargetType() {
        return TargetType.LINE;
    }

    @Override
    public String getName() {
        return "Firebeam";
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
        HashMap<Element, Integer> cost = new HashMap<>();
        cost.put(Element.FIRE, 1);
        return cost;
    }

    @Override
    public void affectTarget(Entity actor, Entity target) {
        if (CombatLogic.castAttempt(actor, target, this)) {
            CombatLogic.castDamage(actor, target, this, 10);
        }
    }

    @Override
    public Color getAnimationColor() {
        return Color.valueOf("ff0000");
    }

    @Override
    public boolean isAnimationStars() {
        return true;
    }

    public void announceHitWithoutKill(Entity caster, Entity target) {
        Game.announceVis(target, caster, "You are burned.",
                target.getVisibleNameThe() + " is burned.",
                target.getVisibleNameThe() + " is burned.", null);
    }

    public void announceHitWithKill(Entity caster, Entity target) {
        Game.announceVis(target, caster, "You are burned to a crisp!",
                target.getVisibleNameThe() + " is burned to a crisp!",
                target.getVisibleNameThe() + " is burned to a crisp!", null);
    }




}
