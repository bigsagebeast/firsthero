package com.churchofcoyote.hero.persistence;

import com.churchofcoyote.hero.roguelike.game.Game;
import com.churchofcoyote.hero.roguelike.world.EntityTracker;
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
        levelName = Game.getLevel().getName();
        gameTime = Game.time;
        gameLastTurnProc = Game.lastTurnProc;
    }
}
