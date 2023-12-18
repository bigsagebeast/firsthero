package com.churchofcoyote.hero.roguelike.spells;

import com.churchofcoyote.hero.roguelike.game.CombatLogic;
import com.churchofcoyote.hero.roguelike.world.Entity;
import com.churchofcoyote.hero.util.Compass;

public class SpellMagicMissile extends Spell {
    @Override
    public TargetType getTargetType() {
        return TargetType.BOLT;
    }

    @Override
    public String getName() {
        return "Magic Missile";
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
    public void affectTarget(Entity actor, Entity target, Compass dir) {
        if (CombatLogic.castAttempt(actor, target, this)) {
            CombatLogic.castDamage(actor, target, this, 10);
        }
    }
}
