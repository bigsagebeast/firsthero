package com.bigsagebeast.hero.roguelike.world.proc.effect;

import com.badlogic.gdx.graphics.Color;
import com.bigsagebeast.hero.enums.Stat;
import com.bigsagebeast.hero.roguelike.game.Game;
import com.bigsagebeast.hero.roguelike.world.Entity;
import com.bigsagebeast.hero.text.TextBlock;

public class ProcEffectOakStrength extends ProcTimedEffect {
    public ProcEffectOakStrength() {
    }

    @Override
    public void initialize(Entity entity) {
        Game.announceVis(entity, null,
                "Your muscles bulge, and bark grows on your skin.",
                null,
                entity.getVisibleNameDefinite() + "'s muscles bulge, and bark grows on their skin.",
                null);
        entity.getTopLevelContainer().recalculateSecondaryStats();
    }

    @Override
    public void expire(Entity entity) {
        Game.announceVis(entity, null,
                "Your Strength of Oak fades away.",
                null,
                entity.getVisibleNameDefinite() + "'s Strength of Oak fades away.",
                null);
        entity.getTopLevelContainer().recalculateSecondaryStats();
    }

    @Override
    public int getStatModifier(Entity entity, Entity actor, Stat stat) {
        if (turnsRemaining > 0) {
            if (stat == Stat.STRENGTH) {
                return 4 + (int)Stat.getScaling(actor.getStat(Stat.ARCANUM), 0.5f);
            } else if (stat == Stat.TOUGHNESS) {
                return 4 + (int)Stat.getScaling(actor.getStat(Stat.ARCANUM), 0.5f);
            }
        }
        return 0;
    }

    @Override
    public TextBlock getStatusBlock(Entity entity) {
        return new TextBlock("Strength", Color.WHITE);
    }
}
