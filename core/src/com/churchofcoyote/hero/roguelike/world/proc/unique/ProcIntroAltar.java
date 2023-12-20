package com.churchofcoyote.hero.roguelike.world.proc.unique;

import com.churchofcoyote.hero.GameLoop;
import com.churchofcoyote.hero.roguelike.game.Game;
import com.churchofcoyote.hero.roguelike.world.Entity;
import com.churchofcoyote.hero.roguelike.world.proc.Proc;

public class ProcIntroAltar extends Proc {
    public boolean seen = false;

    @Override
    public Boolean canPrayAt(Entity entity, Entity actor) {
        return Boolean.TRUE;
    }

    @Override
    public void prayAt(Entity entity, Entity actor) {
        GameLoop.popupModule.createPopup("You pray to the God of Heroes...", 5f, null, 1.5f);
    }

    @Override
    public void postBeSteppedOn(Entity entity, Entity actor) {
        if (actor == Game.getPlayerEntity()) {
            Game.announce("Press 'P' to pray.");
        }
    }

    @Override
    public void actPlayerLos(Entity entity) {
        if (!seen) {
            GameLoop.popupModule.createPopup("Pray at the mysterious altar", 5f, entity, 1.5f);
        }
        seen = true;
    }

    @Override
    public Float getJitter(Entity entity) { return 2f; }

}
