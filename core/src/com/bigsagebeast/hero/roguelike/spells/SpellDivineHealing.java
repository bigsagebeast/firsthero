package com.bigsagebeast.hero.roguelike.spells;

import com.bigsagebeast.hero.roguelike.game.Game;
import com.bigsagebeast.hero.roguelike.world.Entity;
import com.bigsagebeast.hero.util.Compass;

public class SpellDivineHealing extends Spell {
    @Override
    public TargetType getTargetType() {
        return TargetType.PERSONAL;
    }

    @Override
    public SpellType getSpellType() {
        return SpellType.DIVINE;
    }

    @Override
    public String getName() {
        return "Divine Healing";
    }

    @Override
    public String getDescription() {
        return "Restores your hit points to maximum.";
    }

    @Override
    public int getBaseCost(Entity caster) {
        return 500;
    }

    @Override
    public void affectTarget(Entity caster, Entity target, Compass dir) {
        target.hitPoints = target.maxHitPoints;
        Game.announceVis(target, target, "Your divine radiance heals you!",
                null,
                target.getVisibleNameDefinite() + "'s divine radiance heals them!",
                null);
    }
}
