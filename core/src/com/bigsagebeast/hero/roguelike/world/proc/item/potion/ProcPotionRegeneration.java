package com.bigsagebeast.hero.roguelike.world.proc.item.potion;

import com.bigsagebeast.hero.roguelike.game.Game;
import com.bigsagebeast.hero.roguelike.world.Entity;
import com.bigsagebeast.hero.roguelike.world.proc.ImmutableProc;
import com.bigsagebeast.hero.roguelike.world.proc.effect.ProcEffectRegeneration;

public class ProcPotionRegeneration extends ImmutableProc {

    @Override
    public Boolean targetForQuaff(Entity entity) { return Boolean.TRUE; }

    @Override
    public Boolean preBeQuaffed(Entity entity, Entity actor) {
        return Boolean.TRUE;
    }

    @Override
    public void postBeQuaffed(Entity entity, Entity actor) {
        ProcEffectRegeneration effect = (ProcEffectRegeneration)actor.getProcByType(ProcEffectRegeneration.class);
        if (effect == null) {
            effect = new ProcEffectRegeneration();
            actor.addProc(effect);
        }
        switch (entity.getBeatitude()) {
            case CURSED: effect.increaseDuration(5); break;
            case UNCURSED: effect.increaseDuration(10); break;
            case BLESSED: effect.increaseDuration(20); break;
        }
        Game.announceVis(actor, null, "You start to regenerate more quickly!",
                null,
                actor.getVisibleNameDefinite() + " starts to regenerate more quickly!",
                null);
        entity.identifyItemType();
    }
}
