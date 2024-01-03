package com.bigsagebeast.hero.roguelike.world.proc.item.scroll;

import com.bigsagebeast.hero.roguelike.world.Entity;
import com.bigsagebeast.hero.roguelike.game.Game;
import com.bigsagebeast.hero.roguelike.world.proc.ImmutableProc;

public class ProcScrollMagicMap extends ImmutableProc {

    @Override
    public Boolean targetForRead(Entity entity) { return Boolean.TRUE; }

    @Override
    public Boolean preBeRead(Entity entity, Entity actor) {
        return Boolean.TRUE;
    }

    @Override
    public void postBeRead(Entity entity, Entity actor) {
        entity.identifyItemType();
        Game.getLevel().getCellStream().forEach(c -> c.explored = true);
        Game.announce("You feel more knowledgeable!");
    }



}
