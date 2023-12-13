package com.churchofcoyote.hero.roguelike.world.proc.item.scroll;

import com.churchofcoyote.hero.GameLoop;
import com.churchofcoyote.hero.dialogue.DialogueBox;
import com.churchofcoyote.hero.roguelike.game.Game;
import com.churchofcoyote.hero.roguelike.world.Entity;
import com.churchofcoyote.hero.roguelike.world.ItemCategory;
import com.churchofcoyote.hero.roguelike.world.Itempedia;
import com.churchofcoyote.hero.roguelike.world.dungeon.LevelCell;
import com.churchofcoyote.hero.roguelike.world.proc.ImmutableProc;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class ProcScrollMagicMap extends ImmutableProc {

    @Override
    public Boolean targetForRead(Entity entity) { return Boolean.TRUE; }

    @Override
    public Boolean preBeRead(Entity entity, Entity actor) {
        return Boolean.TRUE;
    }

    @Override
    public void postBeRead(Entity entity, Entity actor) {
        entity.identifyItem();
        Game.getLevel().getCellStream().forEach(c -> c.explored = true);
        Game.announce("You feel more knowledgeable!");
    }



}
