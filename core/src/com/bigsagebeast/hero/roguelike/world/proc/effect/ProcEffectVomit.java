package com.bigsagebeast.hero.roguelike.world.proc.effect;

import com.badlogic.gdx.graphics.Color;
import com.bigsagebeast.hero.roguelike.game.Game;
import com.bigsagebeast.hero.roguelike.world.Entity;
import com.bigsagebeast.hero.text.TextBlock;

public class ProcEffectVomit extends ProcTimedEffect {
    public ProcEffectVomit() {
    }

    @Override
    public void initialize(Entity entity) {
        if (entity != Game.getPlayerEntity()) {
            return;
        }
        Game.announceBad("A wave of nausea washes over you.");
    }

    @Override
    public void expire(Entity entity) {
        if (entity != Game.getPlayerEntity()) {
            return;
        }
        // TODO: Paralysis
        Game.announceBad("You throw up.");

        // lower satiation by 1000, but not below 500
        float satiationChange = -1000;
        if (Game.getPlayer().satiation < 1500) {
            satiationChange = 500 - Game.getPlayer().satiation;
        }
        if (satiationChange > 0) {
            satiationChange = 0;
        }

        Game.getPlayer().changeSatiation(satiationChange);
        ProcEffectParalysis proc = new ProcEffectParalysis();
        proc.turnsRemaining = 3;
        entity.addProc(proc);
    }

    @Override
    public TextBlock getStatusBlock(Entity entity) {
        return new TextBlock("Nauseous", Color.ORANGE);
    }
}
