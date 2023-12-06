package com.churchofcoyote.hero.roguelike.world.proc.item;

import com.churchofcoyote.hero.roguelike.game.Game;
import com.churchofcoyote.hero.roguelike.world.Entity;
import com.churchofcoyote.hero.roguelike.world.proc.Proc;

public class ProcCorpse extends Proc {

    int age = 0;

    public ProcCorpse() {}
    public ProcCorpse(Entity e) {super(e);}

    public void turnPassed() {
        age++;
        if (age > 100) {
            entity.destroy();
            Game.announceVis(entity, entity, null, null,
                    "You see " + entity.getVisibleNameSingularOrVague() + " rot away.", null);
        }
    }
}
