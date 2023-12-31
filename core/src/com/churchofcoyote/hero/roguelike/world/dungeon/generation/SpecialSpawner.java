package com.churchofcoyote.hero.roguelike.world.dungeon.generation;

import com.churchofcoyote.hero.roguelike.game.Game;
import com.churchofcoyote.hero.roguelike.world.DungeonGenerator;
import com.churchofcoyote.hero.roguelike.world.Entity;
import com.churchofcoyote.hero.roguelike.world.dungeon.Level;
import com.churchofcoyote.hero.util.Point;

import java.util.ArrayList;
import java.util.List;

public class SpecialSpawner {
    public boolean isMover; // false = item
    public String key; // if omitted, find a random one by tags and level
    public String rule; // 'floor' or 'wall' for spawning location
    public ArrayList<String> tags = new ArrayList<>(); // all tags must be present
    public int percentChance = 100; // pct chance to spawn at all
    public int levelModifier = 0; // modify dungeon level by this when attempting to spawn
    public int quantity = 1; // if max is present, this is the minimum quantity
    public int quantityMax = -1; // -1 means "the same as min"

    // only for post-gen spawns
    public boolean spawnWhileVisible = false;
    public int spawnAverageTurns = 1000;

    public void spawnInRoom(Level level, int roomId) {
        if (quantityMax < 0) {
            quantityMax = quantity;
        }
        int trueQuantity = quantity + Game.random.nextInt(quantityMax - quantity + 1);
        List<Point> validLocations;
        if (rule == null) {
            validLocations = level.findEmptyTilesInRoom(roomId);
        } else if (rule.equals("wall")) {
            validLocations = level.getEmptyRoomMapAlongWall(roomId);
        } else if (rule.equals("floor")) {
            validLocations = level.getEmptyRoomMapOpenFloor(roomId);
        } else {
            throw new RuntimeException("Unknown spawning rule: " + rule);
        }
        if (validLocations == null) {
            System.out.println("ERR: Couldn't find an empty tile for special spawning in room");
            return;
        } else if (trueQuantity > validLocations.size()) {
            System.out.println("WARN: Not enough empty tiles for special spawning in room " + level.rooms.get(roomId).roomType.roomName);
            trueQuantity = validLocations.size();
        }
        for (int i=0; i < trueQuantity; i++) {
            if (percentChance < Game.random.nextInt(100)) {
                continue;
            }
            Point p = validLocations.remove(validLocations.size() - 1);
            Entity entity = null;
            if (key == null) {
                throw new RuntimeException("No entity key to spawn during special spawning");
            }
            if (isMover) {
                entity = Game.bestiary.create(key);
            } else {
                // item
                entity = Game.itempedia.create(key);
            }
            if (entity == null) {
                throw new RuntimeException("No entity to spawn during special spawning");
            }
            level.addEntityWithStacking(entity, p);
        }
    }
}
