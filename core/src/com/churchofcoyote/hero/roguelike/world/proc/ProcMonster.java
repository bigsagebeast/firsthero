package com.churchofcoyote.hero.roguelike.world.proc;

import com.churchofcoyote.hero.GameLoop;
import com.churchofcoyote.hero.roguelike.world.Entity;
import com.churchofcoyote.hero.roguelike.world.ai.ChaseAndMeleeTactic;
import com.churchofcoyote.hero.roguelike.world.ai.Tactic;

public class ProcMonster extends ProcMover {
    public ProcMonster(Entity e) {
        super(e);
    }


    public void act() {
        Tactic tactic = new ChaseAndMeleeTactic();
        tactic.execute(entity, this);
        setDelay(1000);
    }

}
