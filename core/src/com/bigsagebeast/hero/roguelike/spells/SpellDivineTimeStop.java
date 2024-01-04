package com.bigsagebeast.hero.roguelike.spells;

import com.bigsagebeast.hero.roguelike.game.Game;
import com.bigsagebeast.hero.roguelike.world.Entity;
import com.bigsagebeast.hero.util.Compass;

public class SpellDivineTimeStop extends Spell {
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
        return "Time Stop";
    }

    @Override
    public float getRange(Entity caster) {
        return 0;
    }

    @Override
    public int getCost(Entity caster) {
        return 500;
    }

    @Override
    public void affectTarget(Entity caster, Entity target, Compass dir) {
        target.getMover().nextAction -= Game.ONE_TURN * 6;
        Game.announceVis(target, target, "Everything around you slows to a crawl.",
                null,
                target.getVisibleNameDefinite() + " moves with a divine, blinding speed.",
                null);
    }
}
