package com.bigsagebeast.hero.roguelike.world.proc.item.book;

import com.bigsagebeast.hero.GameLoop;
import com.bigsagebeast.hero.roguelike.world.Spellpedia;
import com.bigsagebeast.hero.roguelike.game.Game;
import com.bigsagebeast.hero.roguelike.world.Entity;
import com.bigsagebeast.hero.roguelike.world.proc.Proc;

public class ProcBookSpell extends Proc {

    public String spell;

    @Override
    public Boolean targetForRead(Entity entity) { return Boolean.TRUE; }

    @Override
    public Boolean preBeRead(Entity entity, Entity actor) {
        return Boolean.TRUE;
    }

    @Override
    public void postBeRead(Entity entity, Entity actor) {
        entity.identifyItemType();
        if (Game.spellbook.hasSpell(spell)) {
            Game.announce("You already know that spell.");
        } else {
            Game.spellbook.addSpell(spell);
            Game.announceGood("You learn how to cast " + Spellpedia.get(spell).getName() + "!");
        }
    }

}
