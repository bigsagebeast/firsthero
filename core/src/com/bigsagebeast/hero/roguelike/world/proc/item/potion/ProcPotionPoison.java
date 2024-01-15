package com.bigsagebeast.hero.roguelike.world.proc.item.potion;

import com.bigsagebeast.hero.roguelike.game.Game;
import com.bigsagebeast.hero.roguelike.world.Entity;
import com.bigsagebeast.hero.roguelike.world.proc.ImmutableProc;
import com.bigsagebeast.hero.roguelike.world.proc.effect.ProcEffectPoisoned;

public class ProcPotionPoison extends ImmutableProc {

    @Override
    public Boolean targetForQuaff(Entity entity) { return Boolean.TRUE; }

    @Override
    public Boolean preBeQuaffed(Entity entity, Entity actor) {
        return Boolean.TRUE;
    }

    @Override
    public void postBeQuaffed(Entity entity, Entity actor) {
        int strength = 5;
        if (entity != Game.getPlayerEntity()) {
            strength *= 2;
        }
        ProcEffectPoisoned effect = (ProcEffectPoisoned)actor.getProcByType(ProcEffectPoisoned.class);
        int duration = 0;
        switch (entity.getBeatitude()) {
            case CURSED: duration = 17; break;
            case UNCURSED: duration = 9; break;
            case BLESSED: duration = 5; break;
        }
        if (effect == null) {
            effect = new ProcEffectPoisoned();
            effect.strength = strength;
            effect.turnsRemaining = duration;
            actor.addProc(effect);
            effect.damageCountdown = 3;
        } else {
            effect.increaseDuration(actor, duration);
        }
        entity.identifyItemType();
    }
}
