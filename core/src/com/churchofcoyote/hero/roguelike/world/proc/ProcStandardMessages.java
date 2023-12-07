package com.churchofcoyote.hero.roguelike.world.proc;

import com.churchofcoyote.hero.roguelike.game.Game;
import com.churchofcoyote.hero.roguelike.game.Visibility;
import com.churchofcoyote.hero.roguelike.world.BodyPart;
import com.churchofcoyote.hero.roguelike.world.Entity;

public class ProcStandardMessages extends Proc {

    public ProcStandardMessages() { super(); }

    @Override
    public void postDoEquip(Entity entity, BodyPart bp, Entity target) {
        Visibility vis;
        if (entity == Game.getPlayerEntity())
            vis = Visibility.ACTOR;
        else {
            // TODO check vision
            vis = Visibility.VISIBLE;
        }

        Game.announceVis(vis, "You equip " + target.getVisibleNameThe() + ".",
                "You are equipped by " + entity.getVisibleNameThe() + ".",
                entity.getVisibleNameThe() + " equips " + target.getVisibleNameThe() + ".",
                null);
    }

    @Override
    public void postDoUnequip(Entity entity, BodyPart bp, Entity target) {
        Visibility vis;
        if (entity == Game.getPlayerEntity())
            vis = Visibility.ACTOR;
        else {
            // TODO check vision
            vis = Visibility.VISIBLE;
        }

        Game.announceVis(vis, "You unequip " + target.getVisibleNameThe() + ".",
                "You are unequipped by " + entity.getVisibleNameThe() + ".",
                entity.getVisibleNameThe() + " unequips " + target.getVisibleNameThe() + ".",
                null);
    }
}
