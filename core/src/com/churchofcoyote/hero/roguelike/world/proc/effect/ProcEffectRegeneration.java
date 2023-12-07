package com.churchofcoyote.hero.roguelike.world.proc.effect;

import com.churchofcoyote.hero.roguelike.game.Game;
import com.churchofcoyote.hero.roguelike.world.Entity;

public class ProcEffectRegeneration extends ProcTimedEffect {

    @Override
    public void turnPassed(Entity entity) {
        super.turnPassed(entity);
        entity.heal(entity.healingRate * 2);
        if (turnsRemaining <= 0) {
            Game.announceVis(entity, null, "You feel the effects of regeneration wane.", null, null, null);
        }
    }
}
