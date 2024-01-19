package com.bigsagebeast.hero.roguelike.world.proc.monster;

import com.bigsagebeast.hero.GameLoop;
import com.bigsagebeast.hero.roguelike.game.Game;
import com.bigsagebeast.hero.roguelike.world.Entity;
import com.bigsagebeast.hero.roguelike.world.Itempedia;
import com.bigsagebeast.hero.roguelike.world.proc.Proc;
import com.bigsagebeast.hero.util.Util;

public class ProcDropOnDeath extends Proc {
    public String key = null;
    public int chance = 100;
    public int minCount = 1;
    public int maxCount = 1;
    public void postBeKilled(Entity entity, Entity actor, Entity tool) {
        if (key == null) {
            GameLoop.error("Drop on death with null itemKey");
            return;
        }
        if (chance == 0) {
            GameLoop.warn("Drop on death with 0 percent chance");
            return;
        }
        if (Game.random.nextInt(100) >= chance) {
            return;
        }
        int count = Util.randomBetween(minCount, maxCount);
        if (count <= 0) {
            return;
        }
        Entity spawn = Itempedia.createWithRandomBeatitude(key);
        spawn.getItem().quantity = count;
        Game.getLevel().addEntityWithStacking(spawn, entity.pos);
    }
}
