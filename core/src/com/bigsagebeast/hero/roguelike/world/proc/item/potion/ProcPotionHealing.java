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
        int healAmount = 15 + Game.random.nextInt(10);
        actor.heal(healAmount);
        Game.announceVis(actor, null, "You feel better!",
                null,
                actor.getVisibleNameThe() + " looks better!",
                null);
        entity.identifyItem();
    }
}
