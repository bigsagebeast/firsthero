package com.bigsagebeast.hero.roguelike.world.proc.effect;

import com.bigsagebeast.hero.roguelike.game.Game;
import com.bigsagebeast.hero.roguelike.world.Entity;

public class ProcEffectVomit extends ProcTimedEffect {
    public ProcEffectVomit() {
    }

    @Override
    public void initialize(Entity entity) {
        if (entity != Game.getPlayerEntity()) {
            return;
        }
        Game.announce("A wave of nausea washes over you.");
    }

    @Override
    public void expire(Entity entity) {
        if (entity != Game.getPlayerEntity()) {
            return;
        }
        // TODO: Paralysis
        Game.announce("You throw up.");
        Game.getPlayer().changeSatiation(-1000);
        ProcEffectParalysis proc = new ProcEffectParalysis();
        proc.turnsRemaining = 3;
        entity.addProc(proc);
    }
}
