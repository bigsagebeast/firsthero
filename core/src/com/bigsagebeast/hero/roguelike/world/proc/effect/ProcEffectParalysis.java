package com.bigsagebeast.hero.roguelike.world.proc.effect;

import com.badlogic.gdx.graphics.Color;
import com.bigsagebeast.hero.roguelike.game.Game;
import com.bigsagebeast.hero.roguelike.world.Entity;
import com.bigsagebeast.hero.text.TextBlock;

public class ProcEffectParalysis extends ProcTimedEffect {
    public ProcEffectParalysis() {
    }

    @Override
    public void initialize(Entity entity) {
        Game.announceVisLoud(entity, null,
                "You are paralyzed!",
                null,
                entity.getVisibleNameDefinite() + " is paralyzed!",
                null);
    }

    @Override
    public void expire(Entity entity) {
        Game.announceVisLoud(entity, null,
                "You are no longer paralyzed.",
                null,
                entity.getVisibleNameDefinite() + " is no longer paralyzed.",
                null);
    }

    @Override
    public TextBlock getStatusBlock(Entity entity) {
        return new TextBlock("Paralyzed", Color.YELLOW);
    }
}
