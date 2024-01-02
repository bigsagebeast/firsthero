package com.bigsagebeast.hero.roguelike.world.proc.effect;

import com.bigsagebeast.hero.roguelike.world.Entity;
import com.bigsagebeast.hero.roguelike.world.proc.Proc;

public class ProcTimedEffect extends Proc {
    public int turnsRemaining = 0;

    public void setDuration(int duration) {
        turnsRemaining += duration;
    }

    public void increaseDuration(int duration) {
        turnsRemaining += duration;
    }

    @Override
    public void turnPassed(Entity entity) {
        turnsRemaining--;
        if (turnsRemaining == 0) {
            entity.removeProc(this);
        }
    }
}
