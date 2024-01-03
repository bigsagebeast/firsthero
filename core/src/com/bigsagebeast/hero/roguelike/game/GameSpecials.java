package com.bigsagebeast.hero.roguelike.game;

import com.bigsagebeast.hero.roguelike.world.Entity;
import com.bigsagebeast.hero.util.Point;

public class GameSpecials {
    public static void teleportRandomly(Entity entity) {
        Point destination = null;
        for (int i=0; i<5; i++) {
            destination = Game.getLevel().findOpenTile();
            if (destination != entity.pos) break;
        }
        if (destination == null || destination.equals(entity.pos)) {
            System.out.println("WARN: Couldn't find a teleport destination!");
        } else {
            entity.pos = destination;
        }
    }

    public static void blink(Entity entity) {
        Point destination = Game.getLevel().findOpenTileWithinRange(entity.pos, 8, false);
        if (destination == null) {
            System.out.println("WARN: Couldn't find a blink destination!");
        } else {
            entity.pos = destination;
        }
    }
}
