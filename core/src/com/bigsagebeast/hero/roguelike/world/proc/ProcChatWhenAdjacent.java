package com.bigsagebeast.hero.roguelike.world.proc;

import com.bigsagebeast.hero.GameLoop;
import com.bigsagebeast.hero.roguelike.world.Entity;

public class ProcChatWhenAdjacent extends Proc {
    // nb: only works when the player is the one who moves adjacent to this proc
    boolean triggered = false;
    @Override
    public void onPlayerMovesAdjacentTo(Entity entity) {
        if (!triggered) {
            triggered = true;
            GameLoop.chatModule.openStory(entity);
        }
    }
}
