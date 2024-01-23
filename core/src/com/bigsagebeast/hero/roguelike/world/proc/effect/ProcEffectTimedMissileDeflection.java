package com.bigsagebeast.hero.roguelike.world.proc.effect;

import com.badlogic.gdx.graphics.Color;
import com.bigsagebeast.hero.enums.Stat;
import com.bigsagebeast.hero.roguelike.game.Game;
import com.bigsagebeast.hero.roguelike.world.Entity;
import com.bigsagebeast.hero.text.TextBlock;

public class ProcEffectTimedMissileDeflection extends ProcTimedEffect {
    // no implementation, checked for by class reference

    @Override
    public void initialize(Entity entity) {
        Game.announceVisGood(entity, null,
                "You spin your blade in the air, conjuring forth a whirlwind to deflect projectiles.",
                null,
                entity.getVisibleNameDefinite() + " spins their blade in the air, conjuring forth a whirlwind to deflect projectiles.",
                null);
        entity.getTopLevelContainer().recalculateSecondaryStats();
    }

    @Override
    public void expire(Entity entity) {
        Game.announceVisLoud(entity, null,
                "Your deflection fades away.",
                null,
                entity.getVisibleNameDefinite() + "'s deflection fades away.",
                null);
        entity.getTopLevelContainer().recalculateSecondaryStats();
    }

    @Override
    public TextBlock getStatusBlock(Entity entity) {
        return new TextBlock("Deflection", Color.WHITE);
    }
}
