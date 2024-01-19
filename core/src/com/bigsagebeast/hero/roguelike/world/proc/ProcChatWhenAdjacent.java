package com.bigsagebeast.hero.roguelike.world.proc;

import com.bigsagebeast.hero.GameLoop;
import com.bigsagebeast.hero.roguelike.game.Profile;
import com.bigsagebeast.hero.roguelike.world.Entity;

public class ProcChatWhenAdjacent extends Proc {
    // nb: only works when the player is the one who moves adjacent to this proc
    boolean triggered = false;
    String ignoreIfSet;
    @Override
    public void onPlayerMovesAdjacentTo(Entity entity) {
        if (ignoreIfSet != null) {
            // TODO This doesn't jive with 'isSet' exactly.
            if (Profile.getInt(ignoreIfSet) != 0) {
                return;
            }
        }
        if (!triggered) {
            triggered = true;
            GameLoop.chatModule.openStory(entity);
        }
    }
}
