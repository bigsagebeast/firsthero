package com.churchofcoyote.hero.roguelike.world.proc.environment;

import com.churchofcoyote.hero.roguelike.world.proc.Proc;

public class ProcObstructiveFeature extends Proc {
    public Boolean obstructMovement = Boolean.FALSE;
    public Boolean obstructVision = Boolean.FALSE;
    @Override
    public Boolean isObstructive() {
        return Boolean.TRUE;
    }

    @Override
    public Boolean isObstructiveToManipulators() {
        return Boolean.TRUE;
    }

    @Override
    public Boolean isObstructiveToVision() {
        return Boolean.TRUE;
    }


}
