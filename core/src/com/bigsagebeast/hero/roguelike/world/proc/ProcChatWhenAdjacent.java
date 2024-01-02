package com.bigsagebeast.hero.roguelike.world.proc;

import com.bigsagebeast.hero.GameLoop;
import com.bigsagebeast.hero.glyphtile.EntityGlyph;
import com.bigsagebeast.hero.roguelike.world.Bestiary;
import com.bigsagebeast.hero.roguelike.world.Entity;

public class ProcChatWhenAdjacent extends Proc {
    boolean triggered = false;
    @Override
    public void onAdjacentToPlayer(Entity entity) {
        if (!triggered) {
            triggered = true;
            String page = Bestiary.get(entity.phenotypeName).chatPage;
            GameLoop.CHAT_MODULE.openStory(page, "` " + entity.getVisibleName(), EntityGlyph.getGlyph(entity));
        }
    }
}
