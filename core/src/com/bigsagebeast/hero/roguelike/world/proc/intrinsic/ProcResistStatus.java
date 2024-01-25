package com.bigsagebeast.hero.roguelike.world.proc.intrinsic;

import com.bigsagebeast.hero.enums.StatusType;
import com.bigsagebeast.hero.roguelike.world.Entity;
import com.bigsagebeast.hero.roguelike.world.proc.Proc;

import java.util.Collections;
import java.util.List;

public class ProcResistStatus extends Proc {
    public StatusType status;
    public ProcResistStatus() {}
    public ProcResistStatus(StatusType status) {
        this.status = status;
    }

    @Override
    public List<StatusType> provideStatusResist(Entity entity) {
        return Collections.singletonList(status);
    }

    @Override
    public int getDescriptionPriority(Entity entity) {
        return 1;
    }

    @Override
    public String getIdenDescription(Entity entity) {
        // TODO pluralize
        return "It prevents the effects of " + status.description + ".";
    }

    @Override
    public Proc clone(Entity entity) {
        return new ProcResistStatus(status);
    }
}
