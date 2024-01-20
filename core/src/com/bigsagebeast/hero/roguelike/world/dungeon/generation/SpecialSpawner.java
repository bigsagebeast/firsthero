package com.bigsagebeast.hero.roguelike.world.dungeon.generation;

import com.bigsagebeast.hero.GameLoop;
import com.bigsagebeast.hero.roguelike.world.*;
import com.bigsagebeast.hero.util.Point;
import com.bigsagebeast.hero.roguelike.game.Game;
import com.bigsagebeast.hero.roguelike.world.dungeon.Level;
import com.bigsagebeast.hero.util.Util;

import java.util.ArrayList;
import java.util.List;

public class SpecialSpawner {
    public boolean isMover; // false = item
    public String key; // if omitted, find a random one by tags and level
    public String rule; // 'floor' or 'wall' for spawning location
    public List<String> tags = new ArrayList<>(); // all tags must be present
    public int percentChance = 100; // pct chance to spawn at all
    public int threatModifier = 0; // modify dungeon level by this when attempting to spawn
    public int quantity = 1; // if max is present, this is the minimum quantity
    public int quantityMax = -1; // -1 means "the same as min"
    public boolean summoned; // does the entity count as summoned?
    public List<LoadProc> loadProcs = new ArrayList<>();

    public ArrayList<Integer> ownedEntities = new ArrayList<>();

    // only for post-gen spawns
    public boolean regen; // continue to spawn
    public boolean spawnNearPlayer = false; // can spawn even while player is present
    public int spawnAverageTurns = -1; // spawn every N turns, +/- 50%
    public int spawnMTTH = -1; // alternately, use a percentage chance based on average turns to happen (only if spawnAverageTurns is -1)
    public int spawnWait = -1; // stores how many turns until the next spawn, -1 is uninitialized

    public void spawnInRoomAtGen(Level level, int roomId) {
        if (quantityMax < 0) {
            quantityMax = quantity;
        }
        int trueQuantity;
        if (regen) {
            trueQuantity = quantity; // for regen rooms, quantity is the starting amount, max is how many can be generated over time
        } else {
            trueQuantity = quantity + Game.random.nextInt(quantityMax - quantity + 1);
        }
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
            GameLoop.error("Couldn't find an empty tile for special spawning in room");
            return;
        } else if (trueQuantity > validLocations.size()) {
            GameLoop.warn("Not enough empty tiles for special spawning in room " + level.rooms.get(roomId).roomType.roomName);
            trueQuantity = validLocations.size();
        }
        for (int i=0; i < trueQuantity; i++) {
            if (percentChance < Game.random.nextInt(100)) {
                continue;
            }
            Point p = validLocations.remove(validLocations.size() - 1);
            Entity entity = null;
            if (key == null) {
                if (isMover) {
                    if (tags.isEmpty()) {
                        throw new RuntimeException("No entity key to spawn during special spawning");
                    }
                    key = DungeonGenerator.getAllowedMonster(tags, level.getMinThreat() + threatModifier,
                            level.getMaxThreat() + threatModifier, level, false);
                    if (key == null) {
                        //System.out.println("WARN: Special spawning failed to find an appropriate entity.");
                        return;
                    }
                } else {
                    // TODO: Do something with this threat modifier range?
                    // At present, it will start within the named range, and then work lower if it has to
                    key = DungeonGenerator.getAllowedItem(tags, level.threat + threatModifier,
                            level.threat + threatModifier, level);
                    if (key == null) {
                        key = DungeonGenerator.getAllowedItem(tags, 1,
                                level.threat + threatModifier, level);
                    }
                    if (key == null) {
                        return;
                    }
                }
            }
            if (isMover) {
                entity = Bestiary.create(key);
            } else {
                // item
                entity = Itempedia.createWithRandomBeatitude(key);
            }
            if (entity == null) {
                throw new RuntimeException("No entity to spawn during special spawning: " + key + " " + isMover);
            }
            entity.summoned = summoned;
            level.addEntityWithStacking(entity, p);
            for (LoadProc lp : loadProcs) {
                lp.apply(entity);
            }
            ownedEntities.add(entity.entityId);
        }
    }


    public void spawnInRoomPostGen(Level level, int roomId) {
        if (!regen) {
            return;
        }

        if (spawnAverageTurns > -1) {
            if (spawnWait > 0) {
                spawnWait--;
                return;
            } else if (spawnWait < 0) {
                spawnWait = (int) (spawnAverageTurns * (0.5f + Game.random.nextFloat()));
                return;
            } else {
                spawnWait = (int) (spawnAverageTurns * (0.5f + Game.random.nextFloat()));
            }
        } else if (spawnMTTH > 0) {
            if (!Util.testMTTH(spawnMTTH)) {
                return;
            }
        } else {
            throw new RuntimeException("SpecialSpawner for regen had neither spawnAverageTurns nor spawnMTTH");
        }

        if (quantityMax < 0) {
            quantityMax = quantity;
        }
        ownedEntities.removeIf(eid -> EntityTracker.get(eid) == null);
        if (ownedEntities.size() >= quantityMax) {
            return;
        }

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
        if (validLocations == null || validLocations.isEmpty()) {
            GameLoop.error("Couldn't find an empty tile for special regen spawning in room");
            return;
        }
        if (!spawnNearPlayer) {
            validLocations.removeIf(p -> p.distance(Game.getPlayerEntity().pos) <= 10 || level.cell(p).visible());
        }
        if (validLocations.isEmpty()) {
            return;
        }
        Point p = validLocations.remove(validLocations.size() - 1);
        Entity entity = null;
        if (key == null) {
            if (isMover) {
                if (tags.isEmpty()) {
                    throw new RuntimeException("No entity key to spawn during special spawning");
                }
                key = DungeonGenerator.getAllowedMonster(tags, level.getMinThreat() + threatModifier,
                        level.getMaxThreat() + threatModifier, level, false);
                if (key == null) {
                    //System.out.println("WARN: Special spawning failed to find an appropriate entity.");
                    return;
                }
            } else {
                throw new RuntimeException("Unsupported special spawning of item");
            }
        }
        if (isMover) {
            entity = Bestiary.create(key);
        } else {
            // item
            entity = Itempedia.create(key);
        }
        if (entity == null) {
            throw new RuntimeException("No entity to spawn during special regen spawning");
        }
        level.addEntityWithStacking(entity, p);
        for (LoadProc lp : loadProcs) {
            lp.apply(entity);
        }
        if (level.cell(p).visible()) {
            Game.announce(entity.getVisibleNameIndefiniteOrSpecific() + " pops into existence!");
        }
        ownedEntities.add(entity.entityId);
    }

    public SpecialSpawner clone() {
        SpecialSpawner clone = new SpecialSpawner();
        clone.isMover = isMover;
        clone.key = key;
        clone.rule = rule;
        clone.tags = tags;
        clone.percentChance = percentChance;
        clone.summoned = summoned;
        clone.threatModifier = threatModifier;
        clone.quantity = quantity;
        clone.quantityMax = quantityMax;
        clone.regen = regen;
        clone.spawnNearPlayer = spawnNearPlayer;
        clone.spawnAverageTurns = spawnAverageTurns;
        clone.spawnMTTH = spawnMTTH;
        clone.spawnWait = spawnWait;
        clone.loadProcs.addAll(loadProcs);
        return clone;
    }

    // common settings to make code shorter
    public static SpecialSpawner newRegen() {
        SpecialSpawner spawner = new SpecialSpawner();
        spawner.regen = true;
        spawner.isMover = true;
        spawner.quantity = 0;
        spawner.quantityMax = 1;
        return spawner;
    }
}
