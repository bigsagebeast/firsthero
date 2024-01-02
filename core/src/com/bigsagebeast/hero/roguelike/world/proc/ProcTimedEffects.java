package com.bigsagebeast.hero.roguelike.world.proc;

import com.bigsagebeast.hero.roguelike.world.Entity;

public class ProcTimedEffects extends Proc {

    private int healingTimer;
    private int spRegenTimer;

    public ProcTimedEffects() { super(); }
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
        }
    }
}
