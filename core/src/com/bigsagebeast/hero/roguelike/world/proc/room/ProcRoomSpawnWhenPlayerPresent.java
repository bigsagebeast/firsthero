package com.bigsagebeast.hero.roguelike.world.proc.room;

import com.bigsagebeast.hero.GameLoop;
import com.bigsagebeast.hero.roguelike.game.Game;
import com.bigsagebeast.hero.roguelike.world.Bestiary;
import com.bigsagebeast.hero.roguelike.world.Entity;
import com.bigsagebeast.hero.roguelike.world.proc.Proc;
import com.bigsagebeast.hero.util.Point;

public class ProcRoomSpawnWhenPlayerPresent extends Proc {

    public int maxSpawnTimer = 20;
    public int spawnTimer = 3;
    public String moverKey = null;
    public String spawnMessage = null;

    @Override
    public void turnPassed(Entity entity) {
        if (entity != Game.getPlayerEntity()) {
            return;
        }
        if (moverKey == null) {
            GameLoop.error("No mover key in ProcRoomSpawnWhenPlayerPresent for room " + roomId + "!");
            return;
        }
        if (--spawnTimer <= 0) {
            spawnTimer = maxSpawnTimer;
            Entity e = Bestiary.create(moverKey);
            e.summoned = true;
            Point pos = Game.getLevel().findEmptyTileInRoomForMover(roomId, e);
            if (pos == null) {
                e.destroy();
                return;
            }
            Game.getLevel().addEntityWithStacking(e, pos);
            if (spawnMessage != null) {
                Game.announceSeen(e, spawnMessage);
            }
        }
    }
}
