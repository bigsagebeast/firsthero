package com.bigsagebeast.hero.roguelike.world.proc.effect;

import com.bigsagebeast.hero.roguelike.game.Game;
import com.bigsagebeast.hero.roguelike.world.Entity;

public class ProcEffectParalysis extends ProcTimedEffect {
    public ProcEffectParalysis() {
    }

    @Override
    public void initialize(Entity entity) {
        Game.announceVis(entity, null,
                "You are paralyzed!",
                null,
                entity.getVisibleNameDefinite() + " is paralyzed!",
                null);
    }

    @Override
    public void expire(Entity entity) {
        Game.announceVis(entity, null,
                "You are no longer paralyzed.",
                null,
                entity.getVisibleNameDefinite() + " is no longer paralyzed.",
                null);
    }
}
