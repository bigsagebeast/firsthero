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
    public String getDescription() {
        return "Casts a foe out of this universe entirely. You will get no reward from defeating them. May not work on enemies that are important to the narrative.";
    }

    @Override
    public Float getBaseRange() {
        return 1.0f;
    }

    @Override
    public int getBaseCost(Entity caster) {
        return 300;
    }

    @Override
    public void affectTarget(Entity caster, Entity target, Compass dir) {
        if (target.getPhenotype().tags.contains("plot")) {
            Game.announce("Nothing happens. This being is tethered to the narrative.");
        } else {
            target.destroy();
            // TODO: This had better never happen to the player...
            Game.announce("You focus your divine energy. " + Util.capitalize(target.getVisibleNameDefinite() + " is banished from this universe."));
        }
    }
}
