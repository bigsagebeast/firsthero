package com.bigsagebeast.hero.roguelike.world.proc;

import com.bigsagebeast.hero.roguelike.game.Game;
import com.bigsagebeast.hero.roguelike.world.Entity;
import com.bigsagebeast.hero.roguelike.world.EntityTracker;
import com.bigsagebeast.hero.util.Point;
import com.bigsagebeast.hero.util.Util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ProcSummonMinions extends Proc {
    public int spawnMTTH = 1;
    public ArrayList<Integer> ownedEntities = new ArrayList<>();
    public int quantity = 1;
    public String minionKey;

    @Override
    public void turnPassed(Entity entity) {
        if (!Util.testMTTH(spawnMTTH)) {
            return;
        }
        ownedEntities.removeIf(eid -> EntityTracker.get(eid) == null);
        if (ownedEntities.size() >= quantity) {
            return;
        }
        // TODO tags instead?
        Entity minion = Game.bestiary.create(minionKey);
        ownedEntities.add(minion.entityId);
        minion.summoned = true;
        Collection<Point> spawnPoints = Game.getLevel().surroundingTiles(entity.pos);
        // TODO some things can surely spawn on water...
        spawnPoints.removeIf(p -> !Game.getLevel().cell(p).terrain.isSpawnable());
        spawnPoints.removeIf(p -> !Game.getLevel().getMoversOnTile(p).isEmpty());
        if (spawnPoints.isEmpty()) {
            return;
        }
        List<Point> pointList = new ArrayList(spawnPoints);
        Point spawnPoint = pointList.get(Game.random.nextInt(pointList.size()));
        Game.getLevel().addEntityWithStacking(minion, spawnPoint);
        Game.announceVis(minion, null, null, null,
                minion.getVisibleNameSingularOrSpecific() + " appears!", null);
    }
}
