package com.churchofcoyote.hero.roguelike.world.proc.item.potion;

import com.churchofcoyote.hero.roguelike.game.Game;
import com.churchofcoyote.hero.roguelike.world.Entity;
import com.churchofcoyote.hero.roguelike.world.proc.ImmutableProc;

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
