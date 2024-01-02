package com.bigsagebeast.hero.roguelike.world.proc.monster;

import com.bigsagebeast.hero.roguelike.game.Game;
import com.bigsagebeast.hero.roguelike.world.DungeonGenerator;
import com.bigsagebeast.hero.roguelike.world.Entity;
import com.bigsagebeast.hero.roguelike.world.ai.Tactic;
import com.bigsagebeast.hero.roguelike.world.proc.ProcMover;

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
        if (!entity.summoned) {
            // Chance to drop an item
            if (Game.random.nextInt(5) == 0) {
                Entity loot = DungeonGenerator.spawnLoot(Game.getLevel());
                if (loot != null) {
                    Game.getLevel().addEntityWithStacking(loot, entity.pos);
                }
            }
        }
    }


}
