package com.churchofcoyote.hero.roguelike.world.proc;

import com.churchofcoyote.hero.GameLoop;
import com.churchofcoyote.hero.roguelike.game.Game;
import com.churchofcoyote.hero.roguelike.world.Entity;
import com.churchofcoyote.hero.roguelike.world.ai.ChaseAndMeleeTactic;
import com.churchofcoyote.hero.roguelike.world.ai.Tactic;

import java.util.List;

public class ProcMonster extends ProcMover {
    public ProcMonster(Entity e) {
        super(e);
    }

    public void act() {
        Tactic tactic = new ChaseAndMeleeTactic();
        tactic.execute(entity, this);
        setDelay(1000);
    }

    @Override
    public Boolean wantsMoverLos() { return Boolean.TRUE; }

    @Override
    public void handleMoverLos(List<ProcMover> movers) {
        target = null;
        for (ProcMover mover : movers) {
            if (mover.entity == Game.getPlayerEntity()) {
                target = mover.entity;
            }
        }
    }


}
