package com.bigsagebeast.hero.roguelike.world.proc;

import com.bigsagebeast.hero.roguelike.game.Game;
import com.bigsagebeast.hero.roguelike.world.Entity;
import com.bigsagebeast.hero.roguelike.world.EntityTracker;
import com.bigsagebeast.hero.util.Point;
import com.bigsagebeast.hero.util.Util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ProcHasMinions extends Proc {
    public static final int MAX_MINIONS = 6;

    // For minions acquired OTHER than ProcSummonMinions
    public ArrayList<Integer> ownedEntities = new ArrayList<>();

    public void clean() {
        ownedEntities.removeIf(eid -> EntityTracker.get(eid) == null);
    }
}
