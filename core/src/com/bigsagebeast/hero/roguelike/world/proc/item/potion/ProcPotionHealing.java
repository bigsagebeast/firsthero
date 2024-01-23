package com.bigsagebeast.hero.roguelike.world.proc.item.potion;

import com.bigsagebeast.hero.roguelike.world.Entity;
import com.bigsagebeast.hero.roguelike.world.proc.ImmutableProc;
import com.bigsagebeast.hero.roguelike.game.Game;

public class ProcPotionHealing extends ImmutableProc {

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
                healAmount = 10 + Game.random.nextInt(5);
                break;
            case UNCURSED:
                healAmount = 15 + Game.random.nextInt(10);
                break;
            case BLESSED:
                healAmount = 20 + Game.random.nextInt(10);
                break;
        }
        actor.heal(healAmount);
        Game.announceVisLoud(actor, null, "You feel better!",
                null,
                actor.getVisibleNameDefinite() + " looks better!",
                null);
        entity.identifyItemType();
    }
}
