package com.bigsagebeast.hero.roguelike.world.proc.item.potion;

import com.bigsagebeast.hero.roguelike.game.Game;
import com.bigsagebeast.hero.roguelike.world.Entity;
import com.bigsagebeast.hero.roguelike.world.proc.ImmutableProc;

public class ProcPotionExtraHealing extends ImmutableProc {

    @Override
    public Boolean targetForQuaff(Entity entity) { return Boolean.TRUE; }

    @Override
    public Boolean preBeQuaffed(Entity entity, Entity actor) {
        return Boolean.TRUE;
    }

    @Override
    public void postBeQuaffed(Entity entity, Entity actor) {
        int healAmount = 0;
        switch (entity.getBeatitude()) {
            case CURSED:
                healAmount = 50;
                break;
            case UNCURSED:
                healAmount = 100;
                break;
            case BLESSED:
                healAmount = 1000;
                break;
        }
        actor.heal(healAmount);
        Game.announceVis(actor, null, "You feel much better!",
                null,
                actor.getVisibleNameDefinite() + " looks much better!",
                null);
        entity.identifyItemType();
    }
}
