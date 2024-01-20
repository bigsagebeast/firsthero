package com.bigsagebeast.hero.roguelike.world.proc.effect;

import com.badlogic.gdx.graphics.Color;
import com.bigsagebeast.hero.enums.Stat;
import com.bigsagebeast.hero.roguelike.game.Game;
import com.bigsagebeast.hero.roguelike.world.Entity;
import com.bigsagebeast.hero.text.TextBlock;

public class ProcEffectBerserk extends ProcTimedEffect {
    public ProcEffectBerserk() {
    }

    @Override
    public void initialize(Entity entity) {
        Game.announceVis(entity, null,
                "You rage with berserk fury.",
                null,
                entity.getVisibleNameDefinite() + " rages with berserk fury.",
                null);
        entity.getTopLevelContainer().recalculateSecondaryStats();
    }

    @Override
    public void expire(Entity entity) {
        Game.announceVis(entity, null,
                "You are no longer raging.",
                null,
                entity.getVisibleNameDefinite() + "is no longer raging.",
                null);
        entity.getTopLevelContainer().recalculateSecondaryStats();
    }

    @Override
    public int getStatModifier(Entity entity, Entity actor, Stat stat) {
        if (stat == Stat.AGILITY) {
            return 8;
        }
        return 0;
    }

    @Override
    public int providePenetrationBonus(Entity entity) {
        return 4;
    }

    @Override
    public TextBlock getStatusBlock(Entity entity) {
        return new TextBlock("Berserk", Color.RED);
    }
}
