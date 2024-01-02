package com.bigsagebeast.hero.roguelike.spells;

import com.bigsagebeast.hero.roguelike.game.CombatLogic;
import com.bigsagebeast.hero.util.Compass;
import com.bigsagebeast.hero.roguelike.world.Entity;

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
