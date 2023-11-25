package com.churchofcoyote.hero.roguelike.world.proc;

import com.churchofcoyote.hero.GameLoop;
import com.churchofcoyote.hero.roguelike.game.Game;
import com.churchofcoyote.hero.roguelike.world.Entity;
import com.churchofcoyote.hero.roguelike.world.ai.ChaseAndMeleeTactic;
import com.churchofcoyote.hero.roguelike.world.ai.Tactic;

import java.util.List;

public class ProcMonster extends ProcMover {
    public Tactic tactic;
    protected ProcMonster() {}
    public ProcMonster(Entity e, Tactic tactic) {
        super(e);
        this.tactic = tactic;
    }

    public void act() {
        tactic.execute(entity, this);
        setDelay(1000);
    }

    @Override
    public Boolean wantsMoverLos() { return Boolean.TRUE; }

    @Override
    public void handleMoverLos(List<ProcMover> movers) {
        // TODO: This can't be right...
        //targetEntityId = -1;
        for (ProcMover mover : movers) {
            if (mover.entity == Game.getPlayerEntity()) {
                targetEntityId = mover.entity.entityId;
            }
        }
    }


}
