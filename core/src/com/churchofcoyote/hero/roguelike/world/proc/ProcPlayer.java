package com.churchofcoyote.hero.roguelike.world.proc;

import com.churchofcoyote.hero.roguelike.game.Game;
import com.churchofcoyote.hero.roguelike.world.Entity;

import java.util.List;

public class ProcPlayer extends ProcMover {

    public ProcPlayer() { super(); }

    @Override
    public Boolean preDoPickup(Entity entity, Entity target) { return Boolean.TRUE; }
    @Override
    public void postDoPickup(Entity entity, Entity target) {
        //Game.announce("You pick up the " + target.name + ".");
    }

    @Override
    public Boolean wantsMoverLos() { return Boolean.TRUE; }

    @Override
    public void handleMoverLos(Entity entity, List<Entity> movers) {

    }

    @Override
    public void postDoKill(Entity entity, Entity target, Entity tool) {
        entity.experience += target.experienceAwarded;
        if (target.peaceful) {
            Game.announce("If only talking was implemented.");
        }
        if (entity.experience >= entity.experienceToNext) {
            levelUp(entity);
        }
    }

    private void levelUp(Entity entity) {
        entity.level++;
        entity.experience -= entity.experienceToNext;
        entity.experienceToNext *= 2;
        entity.hitPoints += 15;
        entity.maxHitPoints += 15;
        entity.healingDelay = 300 / entity.maxHitPoints;

        Game.announce("You have reached level " + entity.level + "!");
    }

}
