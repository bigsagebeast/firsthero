package com.bigsagebeast.hero.roguelike.world.proc.effect;

import com.bigsagebeast.hero.roguelike.world.Entity;
import com.bigsagebeast.hero.roguelike.world.proc.Proc;

public class ProcTimedEffect extends Proc {
    public int turnsRemaining = 0;

    public void setDuration(Entity entity, int duration) {
        turnsRemaining = duration;
    }

    public void increaseDuration(Entity entity, int duration) {
        setDuration(entity, turnsRemaining + duration);
    }

    public void expire(Entity entity) {}

    @Override
    public void turnPassed(Entity entity) {
        turnsRemaining--;
        if (turnsRemaining == 0) {
            expire(entity);
            entity.removeProc(this);
        }
    }
}
