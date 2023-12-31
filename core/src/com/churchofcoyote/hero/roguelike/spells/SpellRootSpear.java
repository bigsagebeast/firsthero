package com.churchofcoyote.hero.roguelike.spells;

import com.churchofcoyote.hero.roguelike.game.CombatLogic;
import com.churchofcoyote.hero.roguelike.world.Element;
import com.churchofcoyote.hero.roguelike.world.Entity;
import com.churchofcoyote.hero.util.Compass;

import java.util.HashMap;
import java.util.Map;

public class SpellRootSpear extends Spell {
    @Override
    public TargetType getTargetType() {
        return TargetType.BOLT;
    }

    @Override
    public String getName() {
        return "Root Spear";
    }

    @Override
    public float getRange(Entity caster) {
        return 8;
    }

    @Override
    public int getCost(Entity caster) {
        return 5;
    }

    @Override
    public Map<Element, Integer> getElementCost(Entity caster) {
        HashMap<Element, Integer> cost = new HashMap<>();
        cost.put(Element.NATURAE, 2);
        return cost;
    }

    @Override
    public void affectTarget(Entity actor, Entity target, Compass dir) {
        if (CombatLogic.castAttempt(actor, target, this)) {
            CombatLogic.castDamage(actor, target, this, 12);
        }
    }
}
