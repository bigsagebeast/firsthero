package com.bigsagebeast.hero.persistence;

import com.bigsagebeast.hero.roguelike.world.EntityTracker;
import com.bigsagebeast.hero.roguelike.game.Game;
import com.fasterxml.jackson.annotation.JsonAutoDetect;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class PersistentProfile {
    public int playerEntityId;
    public int lastCreatedEntityId;
    public String levelName;
    // TODO are both necessary?
    public long gameTime;
    public long gameLastTurnProc;

    public void load() {
        Game.getPlayer().setEntityId(playerEntityId);
        EntityTracker.lastCreated = lastCreatedEntityId;
        // level loading happens elsewhere
        Game.time = gameTime;
        Game.lastTurnProc = gameLastTurnProc;
    }

    public void save() {
        playerEntityId = Game.getPlayerEntity().entityId;
        lastCreatedEntityId = EntityTracker.lastCreated;
        levelName = Game.getLevel().getKey();
        gameTime = Game.time;
        gameLastTurnProc = Game.lastTurnProc;
    }
}
