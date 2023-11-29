package com.churchofcoyote.hero.roguelike.world.proc;

import com.churchofcoyote.hero.roguelike.game.Game;
import com.churchofcoyote.hero.roguelike.world.Entity;

import java.util.List;

public class ProcPlayer extends ProcMover {

    protected ProcPlayer() {}
    public ProcPlayer(Entity e) {
        super(e);
    }

    @Override
    public Boolean preDoPickup(Entity target) { return Boolean.TRUE; }
    @Override
    public void postDoPickup(Entity target) {
        //Game.announce("You pick up the " + target.name + ".");
    }

    @Override
    public Boolean wantsMoverLos() { return Boolean.TRUE; }

    @Override
    public void handleMoverLos(List<ProcMover> movers) {

    }

    @Override
    public void postDoKill(Entity target, Entity tool) {
        entity.experience += target.experienceAwarded;
        if (target.peaceful) {
            Game.announce("If only talking was implemented.");
        }
        if (entity.experience >= entity.experienceToNext) {
            levelUp();
        }
    }

    private void levelUp() {
        entity.level++;
        entity.experience -= entity.experienceToNext;
        entity.experienceToNext *= 2;
        entity.hitPoints += 15;
        entity.maxHitPoints += 15;
        entity.healingDelay = 300 / entity.maxHitPoints;

        Game.announce("You have reached level " + entity.level + "!");
    }

}
