package com.bigsagebeast.hero.roguelike.spells;

import com.bigsagebeast.hero.enums.Stat;
import com.bigsagebeast.hero.roguelike.game.CombatLogic;
import com.bigsagebeast.hero.roguelike.game.EquipmentScaling;
import com.bigsagebeast.hero.util.Compass;
import com.bigsagebeast.hero.roguelike.world.Entity;

public class SpellMagicMissile extends Spell {
    public SpellMagicMissile() {
        scaling.put(Stat.ARCANUM, new EquipmentScaling());
        scaling.get(Stat.ARCANUM).damage = 0.5f;
    }

    @Override
    public SpellType getSpellType() {
        return SpellType.ARCANUM;
    }

    @Override
    public TargetType getTargetType() {
        return TargetType.BOLT;
    }

    @Override
    public String getName() {
        return "Magic Missile";
    }

    @Override
    public String getDescription() {
        return "Fires a bolt of pure magical energy. A middle-of-the-road, single-target spell with no special effects and no elemental costs.";
    }

    @Override
    public Float getBaseRange() {
        return 8.0f;
    }

    @Override
    public Float getBaseDamage() { return 8.0f; }

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
    public void affectTarget(Entity actor, Entity target, Compass dir) {
        if (CombatLogic.castAttempt(actor, target, this)) {
            CombatLogic.castDamage(actor, target, this, getDamage(actor));
        }
    }
}
