package com.bigsagebeast.hero.roguelike.world.proc;

import com.bigsagebeast.hero.roguelike.world.Entity;

public class ProcEveryTurn extends Proc {

    private int healingTimer;
    private int spRegenTimer;
    private float dpAccumulator;

    public ProcEveryTurn() { super(); }
    @Override
    public void turnPassed(Entity entity) {
        if (entity.getMover() != null) {
            if (++healingTimer >= entity.healingDelay) {
                entity.heal(entity.healingRate);
                entity.spellPoints = Math.min(entity.maxSpellPoints, entity.spellPoints+1);
                healingTimer = 0;
            }
            if (++spRegenTimer >= entity.spRegenDelay) {
                entity.spellPoints = Math.min(entity.maxSpellPoints, entity.spellPoints+1);
                spRegenTimer = 0;
            }

            dpAccumulator += entity.maxDivinePoints / 500.0f;
            while (dpAccumulator >= 1.0f) {
                dpAccumulator--;
                entity.divinePoints = Math.min(entity.divinePoints + 1, entity.maxDivinePoints);
            }
        }
    }
}
