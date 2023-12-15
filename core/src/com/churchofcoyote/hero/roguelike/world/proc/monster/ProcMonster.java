package com.churchofcoyote.hero.roguelike.world.proc.monster;

import com.churchofcoyote.hero.GameLoop;
import com.churchofcoyote.hero.roguelike.game.Game;
import com.churchofcoyote.hero.roguelike.world.DungeonGenerator;
import com.churchofcoyote.hero.roguelike.world.Entity;
import com.churchofcoyote.hero.roguelike.world.EntityTracker;
import com.churchofcoyote.hero.roguelike.world.Itempedia;
import com.churchofcoyote.hero.roguelike.world.ai.ChaseAndMeleeTactic;
import com.churchofcoyote.hero.roguelike.world.ai.Tactic;
import com.churchofcoyote.hero.roguelike.world.proc.ProcMover;

import java.util.List;

public class ProcMonster extends ProcMover {
    public Tactic tactic;
    public ProcMonster() {}
    public ProcMonster(Tactic tactic) {
        this.tactic = tactic;
    }

    public void act(Entity entity) {
        if (tactic == null) {
            throw new RuntimeException("No tactic found on ProcMonster " + entity.name);
        }
        if (!tactic.execute(entity, this)) {
            setDelay(entity, entity.getMoveCost());
        }
    }

    @Override
    public Boolean wantsMoverLos() { return Boolean.TRUE; }

    @Override
    public void handleMoverLos(Entity entity, List<Entity> movers) {
        // TODO: This can't be right...
        //targetEntityId = -1;
        for (Entity mover : movers) {
            if (mover == Game.getPlayerEntity()) {
                targetEntityId = mover.entityId;
            }
        }
    }

    @Override
    public void postBeKilled(Entity entity, Entity actor, Entity tool) {
        // Chance to drop an item
        if (Game.random.nextInt(5) == 0) {
            Entity loot = DungeonGenerator.spawnLoot(Game.getLevel());
            if (loot != null) {
                Game.getLevel().addEntityWithStacking(loot, entity.pos);
            }
        }
    }


}
