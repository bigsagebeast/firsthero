package com.churchofcoyote.hero.roguelike.world.proc.item.book;

import com.churchofcoyote.hero.GameLoop;
import com.churchofcoyote.hero.roguelike.game.Game;
import com.churchofcoyote.hero.roguelike.world.Entity;
import com.churchofcoyote.hero.roguelike.world.Spellpedia;
import com.churchofcoyote.hero.roguelike.world.proc.Proc;

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
        entity.identifyItem();
        if (GameLoop.roguelikeModule.game.spellbook.hasSpell(spell)) {
            Game.announce("You already know that spell.");
        } else {
            GameLoop.roguelikeModule.game.spellbook.addSpell(spell);
            Game.announce("You learn how to cast " + Spellpedia.get(spell).getName() + "!");
        }
    }

}
