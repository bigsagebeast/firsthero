package com.bigsagebeast.hero.roguelike.world.proc.intrinsic;

import com.bigsagebeast.hero.enums.StatusType;
import com.bigsagebeast.hero.roguelike.world.Entity;
import com.bigsagebeast.hero.roguelike.world.proc.Proc;

import java.util.Collections;
import java.util.List;

public class ProcResistStatus extends Proc {
    public StatusType status;
    public ProcResistStatus() {}

    @Override
    public List<StatusType> provideStatusResist(Entity entity) {
        return Collections.singletonList(status);
    }
}
