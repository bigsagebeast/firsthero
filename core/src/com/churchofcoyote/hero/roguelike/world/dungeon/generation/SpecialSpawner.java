package com.churchofcoyote.hero.roguelike.world.dungeon.generation;

import com.churchofcoyote.hero.roguelike.game.Game;
import com.churchofcoyote.hero.roguelike.world.Entity;
import com.churchofcoyote.hero.roguelike.world.dungeon.Level;
import com.churchofcoyote.hero.util.Point;

import java.util.ArrayList;

public class SpecialSpawner {
    public boolean isMover; // false = item
    public String key; // if omitted, find a random one by tags and level
    public ArrayList<String> tags = new ArrayList<>(); // all tags must be present
    public int percentChance = 100; // pct chance to spawn at all
    public int levelModifier = 0; // modify dungeon level by this when attempting to spawn
    public int quantity = 1; // if max is present, this is the minimum quantity
    public int quantityMax = -1; // -1 means "the same as min"

    public void spawnInRoom(Level level, int roomId) {
        if (quantityMax < 0) {
            quantityMax = quantity;
        }
        int trueQuantity = quantity + Game.random.nextInt(quantityMax - quantity + 1);
        for (int i=0; i < trueQuantity; i++) {
            if (percentChance < Game.random.nextInt(100)) {
                continue;
            }
            Point p = level.findEmptyTileInRoom(roomId);
            if (p == null) {
                System.out.println("ERR: Couldn't find an empty tile for special spawning in room");
                continue;
            }
            Entity entity = null;
            if (isMover) {
                if (key != null) {
                    entity = Game.bestiary.create(key);
                } else {

                }
            } else {
                // item
                if (key != null) {

                } else {

                }
            }
            if (entity == null) {
                throw new RuntimeException("No entity to spawn during special spawning");
            }
            level.addEntityWithStacking(entity, p);
        }
    }
}
