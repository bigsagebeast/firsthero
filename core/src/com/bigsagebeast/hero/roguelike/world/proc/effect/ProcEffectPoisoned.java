package com.bigsagebeast.hero.roguelike.world.proc.effect;

import com.badlogic.gdx.graphics.Color;
import com.bigsagebeast.hero.enums.DamageType;
import com.bigsagebeast.hero.roguelike.game.Game;
import com.bigsagebeast.hero.roguelike.world.Entity;
import com.bigsagebeast.hero.text.TextBlock;
import com.bigsagebeast.hero.util.Util;

public class ProcEffectPoisoned extends ProcTimedEffect {
    public int strength;
    public int damageCountdown;
    public static final int MAX_TURNS_BETWEEN = 10;
    public static final int MIN_TURNS_BETWEEN = 5;

    public ProcEffectPoisoned() {
    }

    @Override
    public void turnPassed(Entity entity) {
        super.turnPassed(entity);
        if (--damageCountdown == 0) {
            damageCountdown = Util.randomBetween(MIN_TURNS_BETWEEN, MAX_TURNS_BETWEEN);
            Game.announceVis(entity, null,
                    "The poison wracks your body!",
                    null,
                    "The poison wracks " + entity.getVisibleNameDefinite() + "'s body!",
                    null);
            entity.hurt(strength, DamageType.POISON, true);
            if (entity == Game.getPlayerEntity()) {
                Game.interrupt();
            }
        }
    }

    @Override
    public void initialize(Entity entity) {
        damageCountdown = Util.randomBetween(MIN_TURNS_BETWEEN, MAX_TURNS_BETWEEN);
        Game.announceVis(entity, null,
                "You are poisoned!",
                null,
                entity.getVisibleNameDefinite() + " is poisoned!",
                null);
    }

    @Override
    public void setDuration(Entity entity, int duration) {
        if (duration > turnsRemaining) {
            Game.announceVis(entity, null,
                    "The poison intensifies!",
                    null,
                    entity.getVisibleNameDefinite() + " is more heavily poisoned!",
                    null);
        } else if (duration < turnsRemaining) {
            Game.announceVis(entity, null,
                    "The poison weakens.",
                    null,
                    entity.getVisibleNameDefinite() + " is less heavily poisoned.",
                    null);
        }
        super.setDuration(entity, duration);
    }

    @Override
    public void expire(Entity entity) {
        Game.announceVis(entity, null,
                "The poison leaves your system.",
                null,
                entity.getVisibleNameDefinite() + " is no longer poisoned.",
                null);
    }

    @Override
    public TextBlock getStatusBlock(Entity entity) {
        return new TextBlock("Poisoned", Color.LIME);
    }
}
