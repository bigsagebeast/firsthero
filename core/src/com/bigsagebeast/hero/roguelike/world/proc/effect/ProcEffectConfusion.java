package com.bigsagebeast.hero.roguelike.world.proc.effect;

import com.badlogic.gdx.graphics.Color;
import com.bigsagebeast.hero.roguelike.game.Game;
import com.bigsagebeast.hero.roguelike.world.Entity;
import com.bigsagebeast.hero.text.TextBlock;

public class ProcEffectConfusion extends ProcTimedEffect {
    public ProcEffectConfusion() {
    }

    @Override
    public void initialize(Entity entity) {
        Game.announceVisLoud(entity, null,
                "You are confused!",
                null,
                entity.getVisibleNameDefinite() + " is confused!",
                null);
    }

    @Override
    public void expire(Entity entity) {
        Game.announceVisLoud(entity, null,
                "You are no longer confused.",
                null,
                entity.getVisibleNameDefinite() + " is no longer confused.",
                null);
    }

    @Override
    public TextBlock getStatusBlock(Entity entity) {
        return new TextBlock("Confused", Color.WHITE);
    }
}
