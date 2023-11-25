package com.churchofcoyote.hero.roguelike.world.proc;

import com.churchofcoyote.hero.roguelike.game.Game;
import com.churchofcoyote.hero.roguelike.game.Visibility;
import com.churchofcoyote.hero.roguelike.world.BodyPart;
import com.churchofcoyote.hero.roguelike.world.Entity;

public class ProcStandardMessages extends Proc {

    protected ProcStandardMessages() {}
    public ProcStandardMessages(Entity e) {
        super(e);
    }

    @Override
    public void postDoUnequip(BodyPart bp, Entity target) {
        Visibility vis;
        if (entity == Game.getPlayerEntity())
            vis = Visibility.ACTOR;
        else {
            // TODO check vision
            vis = Visibility.VISIBLE;
        }

        Game.announceVis(vis, "You equip the " + target.name + ".",
                "You are equipped by " + entity.name + ".",
                entity.name + " equips the " + target.name + ".",
                null);
    }
}
