package com.bigsagebeast.hero.roguelike.world.proc.effect;

import com.bigsagebeast.hero.roguelike.game.Game;
import com.bigsagebeast.hero.roguelike.world.Entity;

public class ProcEffectSlow extends ProcTimedEffect {
    public ProcEffectSlow() {
    }

    @Override
    public void initialize(Entity entity) {
        Game.announceVis(entity, null,
                "You are slowed!",
                null,
                entity.getVisibleNameDefinite() + " is slowed.",
                null);
    }

    @Override
    public void expire(Entity entity) {
        Game.announceVis(entity, null,
                "You are no longer slowed.",
                null,
                entity.getVisibleNameDefinite() + " is no longer slowed.",
                null);
    }

    @Override
    public float getSpeedMultiplier(Entity entity, Entity actor) {
        return 0.5f;
    }
}
