package com.bigsagebeast.hero.roguelike.world.proc.effect;

import com.badlogic.gdx.graphics.Color;
import com.bigsagebeast.hero.roguelike.game.Game;
import com.bigsagebeast.hero.roguelike.world.Entity;
import com.bigsagebeast.hero.text.TextBlock;

public class ProcEffectRegeneration extends ProcTimedEffect {

    @Override
    public void turnPassed(Entity entity) {
        super.turnPassed(entity);
        entity.heal(entity.healingRate * 2);
        if (turnsRemaining <= 0) {
            Game.announceVis(entity, null, "You feel the effects of regeneration wane.", null, null, null);
        }
    }

    @Override
    public TextBlock getStatusBlock(Entity entity) {
        return new TextBlock("Regen", Color.RED);
    }
}
