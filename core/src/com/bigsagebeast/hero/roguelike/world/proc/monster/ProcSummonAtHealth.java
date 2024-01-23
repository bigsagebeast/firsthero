package com.bigsagebeast.hero.roguelike.world.proc.monster;

import com.bigsagebeast.hero.roguelike.game.Game;
import com.bigsagebeast.hero.roguelike.world.Bestiary;
import com.bigsagebeast.hero.roguelike.world.Entity;
import com.bigsagebeast.hero.roguelike.world.proc.Proc;
import com.bigsagebeast.hero.util.Point;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

// For passively spawned minions
public class ProcSummonAtHealth extends Proc {
    public String message;
    public String messageFailed;
    public String listen;
    public String listenFailed;
    public boolean aroundPlayer;
    public boolean reached;
    public float threshold = 0.5f;
    public int quantity = 1;
    public String minionKey;

    @Override
    public void turnPassed(Entity entity) {
        if (reached || entity.summoned) {
            return;
        }
        if (entity.maxHitPoints * threshold < entity.hitPoints) {
            return;
        }
        reached = true;

        boolean summonedAny = false;
        // TODO tags instead?
        for (int i=0; i<quantity; i++) {
            Entity minion = Bestiary.create(minionKey);
            minion.summoned = true;
            Collection<Point> spawnPoints;
            if (aroundPlayer) {
                // TODO: This should be around the target instead of the player, probably
                spawnPoints = Game.getLevel().surroundingTiles(Game.getPlayerEntity().pos);
            } else {
                spawnPoints = Game.getLevel().surroundingTiles(entity.pos);
            }
            spawnPoints.removeIf(p -> !Game.getLevel().cell(p).terrain.isSpawnable());
            spawnPoints.removeIf(p -> !Game.getLevel().getMoversOnTile(p).isEmpty());
            if (spawnPoints.isEmpty()) {
                break;
            }
            summonedAny = true;
            List<Point> pointList = new ArrayList(spawnPoints);
            Point spawnPoint = pointList.get(Game.random.nextInt(pointList.size()));
            Game.getLevel().addEntityWithStacking(minion, spawnPoint);
            if (message == null) {
                Game.announceSeen(minion,minion.getVisibleNameIndefiniteOrSpecific() + " appears!");
            }
        }
        if (summonedAny && message != null) {
            Game.announceVisLoud(entity, null, null, null, message, listen);
        }
        if (!summonedAny && messageFailed != null) {
            Game.announceVisLoud(entity, null, null, null, messageFailed, listenFailed);
        }
        Game.turn();
    }
}
