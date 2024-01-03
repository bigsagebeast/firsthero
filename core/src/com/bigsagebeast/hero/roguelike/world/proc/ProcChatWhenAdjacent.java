package com.bigsagebeast.hero.roguelike.world.proc;

import com.bigsagebeast.hero.GameLoop;
import com.bigsagebeast.hero.roguelike.world.Entity;

public class ProcChatWhenAdjacent extends Proc {
    boolean triggered = false;
    @Override
    public void onAdjacentToPlayer(Entity entity) {
        if (!triggered) {
            triggered = true;
            GameLoop.CHAT_MODULE.openStory(entity);
        }
    }
}
