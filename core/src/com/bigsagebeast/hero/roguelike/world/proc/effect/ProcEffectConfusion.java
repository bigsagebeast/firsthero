package com.bigsagebeast.hero.roguelike.world.proc.effect;

import com.bigsagebeast.hero.roguelike.game.Game;
import com.bigsagebeast.hero.roguelike.world.Entity;

public class ProcEffectConfusion extends ProcTimedEffect {
    public ProcEffectConfusion() {
    }

    @Override
    public void initialize(Entity entity) {
        Game.announceVis(entity, null,
                "You are confused!",
                null,
                entity.getVisibleNameThe() + " is confused!",
                null);
    }

    @Override
    public void expire(Entity entity) {
        Game.announceVis(entity, null,
                "You are no longer confused.",
                null,
                entity.getVisibleNameThe() + " is no longer confused.",
                null);
    }
}
