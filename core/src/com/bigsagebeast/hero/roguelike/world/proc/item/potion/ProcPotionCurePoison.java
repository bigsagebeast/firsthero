package com.bigsagebeast.hero.roguelike.world.proc.item.potion;

import com.bigsagebeast.hero.roguelike.game.Game;
import com.bigsagebeast.hero.roguelike.world.Entity;
import com.bigsagebeast.hero.roguelike.world.proc.ImmutableProc;
import com.bigsagebeast.hero.roguelike.world.proc.effect.ProcEffectPoisoned;

public class ProcPotionCurePoison extends ImmutableProc {

    @Override
    public Boolean targetForQuaff(Entity entity) { return Boolean.TRUE; }

    @Override
    public Boolean preBeQuaffed(Entity entity, Entity actor) {
        return Boolean.TRUE;
    }

    @Override
    public void postBeQuaffed(Entity entity, Entity actor) {
        ProcEffectPoisoned effect = (ProcEffectPoisoned)actor.getProcByType(ProcEffectPoisoned.class);
        switch (entity.getBeatitude()) {
            case CURSED:
                Game.announceVis(actor, null, "Something was wrong with that potion.", null, "Something was wrong with that potion.", null);
                if (effect == null) {
                    effect = new ProcEffectPoisoned();
                    effect.strength = 5;
                    effect.turnsRemaining = 25;
                    actor.addProc(effect);
                    effect.damageCountdown = 3;
                } else {
                    effect.increaseDuration(actor, 25);
                }
                break;
            case UNCURSED:
            case BLESSED:
                if (effect != null) {
                    effect.expireEarly(actor);
                }
                break;
        }
        if (effect != null) {
            Game.announceVis(actor, null, "Nothing happens.", null, "Nothing happens.", null);
            entity.identifyItemType();
        }
    }
}
