package com.churchofcoyote.hero.roguelike.world.proc.item.potion;

import com.churchofcoyote.hero.roguelike.game.Game;
import com.churchofcoyote.hero.roguelike.world.Entity;
import com.churchofcoyote.hero.roguelike.world.proc.ImmutableProc;

public class ProcPotionAcid extends ImmutableProc {

    @Override
    public Boolean targetForQuaff(Entity entity) { return Boolean.TRUE; }

    @Override
    public Boolean preBeQuaffed(Entity entity, Entity actor) {
        return Boolean.TRUE;
    }

    @Override
    public void postBeQuaffed(Entity entity, Entity actor) {
        int hurtAmount = 10 + Game.random.nextInt(10);
        actor.hurt(hurtAmount);
        Game.announceVis(actor, null, "It burns!",
                null,
                actor.getVisibleNameThe() + " is burned!",
                null);
        entity.identifyItem();
    }
}