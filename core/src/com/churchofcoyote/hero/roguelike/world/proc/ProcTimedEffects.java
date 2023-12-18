package com.churchofcoyote.hero.roguelike.world.proc;

import com.churchofcoyote.hero.roguelike.world.Entity;

public class ProcTimedEffects extends Proc {

    private int healingTimer;

    public ProcTimedEffects() { super(); }
    @Override
    public void turnPassed(Entity entity) {
        if (entity.getMover() != null) {
            if (++healingTimer >= entity.healingDelay) {
                entity.heal(entity.healingRate);
                entity.spellPoints = Math.min(entity.maxSpellPoints, entity.spellPoints+1);
                healingTimer = 0;
            }
        }
    }
}
