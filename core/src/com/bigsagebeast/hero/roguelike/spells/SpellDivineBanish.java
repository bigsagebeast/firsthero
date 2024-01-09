package com.bigsagebeast.hero.roguelike.spells;

import com.bigsagebeast.hero.roguelike.game.Game;
import com.bigsagebeast.hero.roguelike.world.Entity;
import com.bigsagebeast.hero.util.Compass;
import com.bigsagebeast.hero.util.Util;

public class SpellDivineBanish extends Spell {
    @Override
    public TargetType getTargetType() {
        return TargetType.BOLT;
    }

    @Override
    public SpellType getSpellType() {
        return SpellType.DIVINE;
    }

    @Override
    public String getName() {
        return "Banish";
    }

    @Override
    public Float getRange(Entity caster) {
        return 1.0f;
    }

    @Override
    public int getCost(Entity caster) {
        return 500;
    }

    @Override
    public void affectTarget(Entity caster, Entity target, Compass dir) {
        target.destroy();
        // TODO: This had better never happen to the player...
        Game.announce("You focus your divine energy. " + Util.capitalize(target.getVisibleNameDefinite() + " is banished from this universe."));
    }
}
