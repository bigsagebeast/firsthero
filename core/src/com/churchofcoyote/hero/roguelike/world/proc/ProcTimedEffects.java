package com.churchofcoyote.hero.roguelike.world.proc;

import com.churchofcoyote.hero.roguelike.world.Entity;

public class ProcTimedEffects extends Proc {

    private int healingTimer;

    public ProcTimedEffects(Entity e) {
        super(e);
    }
    @Override
    public void turnPassed() {
        if (entity.getMover() != null) {
            if (++healingTimer > entity.healingDelay) {
                entity.heal(entity.healingRate);
                healingTimer = 0;
            }
        }
    }
}
