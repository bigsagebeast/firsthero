package com.bigsagebeast.hero.roguelike.world.dungeon.generation;

import com.bigsagebeast.hero.GameLoop;
import com.bigsagebeast.hero.roguelike.world.Entity;
import com.bigsagebeast.hero.roguelike.world.Itempedia;
import com.bigsagebeast.hero.roguelike.world.dungeon.*;
import com.bigsagebeast.hero.roguelike.world.proc.environment.ProcStairs;
import com.bigsagebeast.hero.util.Compass;
import com.bigsagebeast.hero.util.Point;
import com.bigsagebeast.hero.roguelike.game.Game;
import com.bigsagebeast.hero.roguelike.world.Terrain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class Generator {
    public Level level;

    private Terrain wall;
    private Terrain uncarveable;
    private Terrain floor;
    private Terrain doorway;

    public Level generate(String name, DungeonPlanFloor planFloor, int width, int height, int threat) {
        level = new Level(name, width, height);
        level.threat = threat;

        wall = Terrain.get("wall");
        uncarveable = Terrain.get("uncarveable");
        floor = Terrain.get("dot");
        doorway = Terrain.get("doorway");

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                if (i == 0 || j == 0 || i == width - 1 || j == height - 1) {
                    level.cell(i, j).terrain = uncarveable;
                } else {
                    level.cell(i, j).terrain = wall;
                }
            }
        }

        RoomPacker roomPacker = null;
        for (DungeonPlanFeature feature : planFloor.features) {
            if (feature.name.equals("goblin.outpost") || feature.name.equals("goblin.stronghold") || feature.name.equals("goblin.endboss")) {
                Theme theme = Themepedia.get(feature.name);
                int coinflip = Game.random.nextInt(2);
                switch (Game.random.nextInt(4)) {
                    case 0:
                        if (coinflip == 0)
                            roomPacker = new RoomPacker(level, 1, 1, 29, 19, theme.totalRooms, Compass.SOUTH);
                        else
                            roomPacker = new RoomPacker(level, 1, 1, 29, 19, theme.totalRooms, Compass.EAST);
                        break;
                    case 1:
                        if (coinflip == 0)
                            roomPacker = new RoomPacker(level, width - 30, 1, 29, 19, theme.totalRooms, Compass.SOUTH);
                        else
                            roomPacker = new RoomPacker(level, width - 30, 1, 29, 19, theme.totalRooms, Compass.WEST);
                        break;
                    case 2:
                        if (coinflip == 0)
                            roomPacker = new RoomPacker(level, 1, height - 20, 29, 19, theme.totalRooms, Compass.NORTH);
                        else
                            roomPacker = new RoomPacker(level, 1, height - 20, 29, 19, theme.totalRooms, Compass.EAST);
                        break;
                    default:
                        if (coinflip == 0)
                            roomPacker = new RoomPacker(level, width - 30, height - 20, 29, 19, theme.totalRooms, Compass.NORTH);
                        else
                            roomPacker = new RoomPacker(level, width - 30, height - 20, 29, 19, theme.totalRooms, Compass.WEST);
                        break;
                }
                roomPacker.generate();

                SubDungeonAssigner assigner = new SubDungeonAssigner(roomPacker.firstNode, theme);
                assigner.assign();
            }
        }


        boolean makeRiver = false;
        for (DungeonPlanFeature feature : planFloor.features) {
            if (feature.name.equals("river")) {
                makeRiver = true;
                break;
            }
        }
        Brogue brogue = new Brogue(level);
        brogue.makeRiver = makeRiver;
        brogue.generate();

        if (roomPacker != null) {
            List<Point> astarPoints = AStarLevel.path(level, roomPacker.rooms.get(0).centerPoint, brogue.rooms.get(0).centerPoint, 1000.0f);
            for (Point p : astarPoints) {
                if (!level.cell(p).terrain.isPassable()) {
                    level.cell(p).terrain = floor;
                }
            }
        }

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                Point p = new Point(i, j);
                LevelCell cell = level.cell(p);
                int roomId = cell.roomId;
                if (roomId > -1) {
                    if (level.roomMap.get(roomId) == null) {
                        level.roomMap.put(roomId, new ArrayList<>());
                    }
                    level.roomMap.get(roomId).add(p);
                }
                if (cell.terrain == uncarveable) {
                    cell.terrain = wall;
                }
            }
        }

        addFancyTiles();

        List<Room> genericRooms = level.rooms.stream().filter(r -> r.roomType == RoomType.GENERIC_ROOM).collect(Collectors.toList());
        if (genericRooms.size() < 2) {
            genericRooms.addAll(level.rooms.stream().filter(r -> r.roomType == RoomType.GENERIC_CAVERN).collect(Collectors.toList()));
            if (genericRooms.size() < 2) {
                GameLoop.warn("Not enough rooms, retrying");
                return null;
            }
        }

        for (Room room : level.rooms) {
            if (room.roomType != null) {
                for (SpecialSpawner spawner : room.roomType.spawners) {
                    spawner.spawnInRoomAtGen(level, room.roomId);
                }
            }
        }

        for (DungeonPlanFeature feature : planFloor.features) {
            switch (feature.name) {
                case "grove":
                    retryAddSpecialFeature(RoomType.UNDERGROUND_GROVE, RoomType.GENERIC_CAVERN);
                    break;
                case "mossy":
                    // put moss in caverns if it can, anywhere if it can't
                    if (!retryAddSpecialFeature(RoomType.MOSSY, RoomType.GENERIC_CAVERN)) {
                        retryAddSpecialFeature(RoomType.MOSSY, RoomType.GENERIC_ANY);
                    }
                    break;
                case "forge":
                    retryAddSpecialFeature(RoomType.FORGE, RoomType.GENERIC_ROOM);
                    break;
                case "pool":
                    retryAddSpecialFeature(RoomType.POOL, RoomType.GENERIC_ROOM);
                    break;
                case "copper":
                    retryAddSpecialFeature(RoomType.FRACTAL_COPPER, RoomType.GENERIC_ANY);
                    break;
                case "rot spawner":
                    retryAddSpecialFeature(RoomType.ROT_SPAWNER, RoomType.GENERIC_ROOM);
                    break;
                case "hp regen up":
                    retryAddSpecialFeature(RoomType.HP_REGEN_UP, RoomType.GENERIC_ANY);
                    break;
                case "sp regen up":
                    retryAddSpecialFeature(RoomType.SP_REGEN_UP, RoomType.GENERIC_ANY);
                    break;
                case "dp regen up":
                    retryAddSpecialFeature(RoomType.DP_REGEN_UP, RoomType.GENERIC_ANY);
                    break;
            }
        }

        return level;
    }

    // this is mostly to get around addSpecialFeature picking a too-small room
    public boolean retryAddSpecialFeature(RoomType roomType, RoomType replacing) {
        int retries = 100;
        for (int i=0; i<retries; i++) {
            if (addSpecialFeature(roomType, replacing)) {
                return true;
            }
        }
        return false;
    }

    // returning 'false' should mean that no changes were committed
    public boolean addSpecialFeature(RoomType roomType, RoomType replacing) {
        List<Room> candidates = null;
        if (replacing == RoomType.GENERIC_ANY) {
            candidates = level.rooms.stream().filter(r -> r.roomType == RoomType.GENERIC_ROOM || r.roomType == RoomType.GENERIC_CAVERN).collect(Collectors.toList());
        } else {
            candidates = level.rooms.stream().filter(r -> r.roomType == replacing).collect(Collectors.toList());
        }
        if (candidates == null || candidates.isEmpty()) {
            return false;
        }
        Collections.shuffle(candidates);
        int roomId = candidates.get(0).roomId;

        if (roomType == RoomType.FORGE) {
            List<Point> openFloorTiles = level.getEmptyRoomMapOpenFloor(roomId);
            if (openFloorTiles.isEmpty()) {
                return false;
            }
            Point forgePoint = openFloorTiles.get(0);
            Entity forge = Itempedia.create("feature.forge");
            level.addEntityWithStacking(forge, forgePoint);
        } else if (roomType == RoomType.POOL) {
            List<Point> openFloorTiles = level.getEmptyRoomMapOpenFloor(roomId);
            if (openFloorTiles.isEmpty()) {
                return false;
            }
            Point poolPoint = openFloorTiles.get(0);
            Entity pool = Itempedia.create("feature.pool");
            level.addEntityWithStacking(pool, poolPoint);
        } else if (roomType == RoomType.MOSSY) {
            List<Point> wallFloorTiles = level.getEmptyRoomMapAlongWall(roomId);
            Collections.shuffle(wallFloorTiles);
            int mossyTiles = 4 + Game.random.nextInt(4);
            for (int i = 0; i < mossyTiles && i < wallFloorTiles.size(); i++) {
                level.addEntityWithStacking(Itempedia.create("feature.moss"), wallFloorTiles.get(i));
            }
        } else if (roomType == RoomType.UNDERGROUND_GROVE) {
            List<Point> groveStarts = level.getEmptyRoomMapOpenFloor(roomId);
            if (groveStarts.size() < 10) {
                return false;
            }
            Collections.shuffle(groveStarts);
            Point groveStart = groveStarts.get(0);
            Point roomUpperLeft = level.getRoomUpperLeft(roomId);
            Point roomLowerRight = level.getRoomLowerRight(roomId);
            CellularAutomata growth = new CellularAutomata(
                    roomLowerRight.x-roomUpperLeft.x+1, roomLowerRight.y-roomUpperLeft.y+1);
            for (int i=0; i<growth.width; i++) {
                for (int j=0; j<growth.height; j++) {
                    if (level.cell(roomUpperLeft.x+i, roomUpperLeft.y+j).terrain.isPassable()) {
                        growth.cells[i][j] = AutomataStatus.RANDOM;
                    } else {
                        growth.cells[i][j] = AutomataStatus.ALWAYS_FALSE;
                    }
                }
            }
            growth.cells[groveStart.x-roomUpperLeft.x][groveStart.y-roomUpperLeft.y] = AutomataStatus.TRUE;
            for (int i=0; i<3; i++) {
                growth.iterateGrowth(0.75f);
            }
            List<Point> grassCells = new ArrayList<>();
            for (int i=0; i<growth.width; i++) {
                for (int j=0; j<growth.height; j++) {
                    if (growth.cells[i][j] == AutomataStatus.TRUE) {
                        // TODO more varied grass types
                        Point p = new Point(roomUpperLeft.x+i, roomUpperLeft.y+j);
                        level.cell(p).terrain = Terrain.get("grass");
                        grassCells.add(p);
                    }
                }
            }
            List<Point> validTreeCells = grassCells.stream().filter(groveStarts::contains).collect(Collectors.toList());
            Collections.shuffle(validTreeCells);
            for (int i=0; i<3 && i < validTreeCells.size(); i++) {
                Entity tree = Itempedia.create("feature.tree");
                level.addEntityWithStacking(tree, validTreeCells.get(i));
            }
        } else if (roomType == RoomType.FRACTAL_COPPER) {

        } else {
            //throw new RuntimeException("No handling rules for roomtype " + (roomType.roomName == null ? "unnamed" : roomType.roomName));
        }
        // set room type by default (unless we returned true early)
        level.rooms.get(roomId).setRoomType(roomType);
        return true;
    }

    public void addUpstairTo(String levelKey) {
        Room stairRoom;
        List<Room> genericRooms = level.rooms.stream().filter(r -> r.roomType == RoomType.GENERIC_ROOM).collect(Collectors.toList());
        if (genericRooms.size() == 0) {
            genericRooms = level.rooms.stream().filter(r -> r.roomType == RoomType.GENERIC_CAVERN).collect(Collectors.toList());
        }
        if (genericRooms.isEmpty()) {
            GameLoop.warn("Not enough rooms to generate a stair cleanly!");
            stairRoom = level.rooms.get(Game.random.nextInt(level.rooms.size()));
        } else {
            Collections.shuffle(genericRooms);
            stairRoom = genericRooms.get(0);
            stairRoom.setRoomType(RoomType.GENERIC_UPSTAIR);
        }
        Point stairPoint = findEmptyPointInRoom(stairRoom);

        Entity stairsUp = Itempedia.create("feature.stairsUp");
        ProcStairs procStairs = (ProcStairs)stairsUp.getProcByType(ProcStairs.class);
        if (levelKey.equals("out")) {
            procStairs.upSpecialMessage = "You can't leave the dungeon until your quest is complete!";
        }
        procStairs.upToMap = levelKey;
        level.addEntityWithStacking(stairsUp, stairPoint);
    }

    public void addDownstairTo(String levelKey) {
        Room stairRoom;
        List<Room> genericRooms = level.rooms.stream().filter(r -> r.roomType == RoomType.GENERIC_ROOM).collect(Collectors.toList());
        if (genericRooms.size() == 0) {
            genericRooms = level.rooms.stream().filter(r -> r.roomType == RoomType.GENERIC_CAVERN).collect(Collectors.toList());
        }
        if (genericRooms.isEmpty()) {
            GameLoop.warn("Not enough rooms to generate a stair cleanly!");
            stairRoom = level.rooms.get(Game.random.nextInt(level.rooms.size()));
        } else {
            Collections.shuffle(genericRooms);
            stairRoom = genericRooms.get(0);
            stairRoom.setRoomType(RoomType.GENERIC_DOWNSTAIR);
        }
        Point stairPoint = findEmptyPointInRoom(stairRoom);

        Entity stairsDown = Itempedia.create("feature.stairsDown");
        ProcStairs procStairs = (ProcStairs)stairsDown.getProcByType(ProcStairs.class);
        procStairs.downToMap = levelKey;
        level.addEntityWithStacking(stairsDown, stairPoint);
    }

    private Point findEmptyPointInRoom(Room room) {
        // TODO update this when changing upstair and downstair to features
        if (!level.roomMap.containsKey(room.roomId)) {
            throw new RuntimeException("Room id " + room.roomId + " is not in the room map");
        }
        List<Point> points = level.roomMap.get(room.roomId).stream()
                .filter(p -> level.cell(p).terrain.isSpawnable())
                .filter(p -> level.getEntitiesOnTile(p).isEmpty())
                .collect(Collectors.toList());
        if (points == null) {
            throw new RuntimeException("Failed to find an empty point in room " + room);
        }
        return points.get(Game.random.nextInt(points.size()));
    }

    private void addFancyTiles() {
        for (Room room : level.rooms) {
            if (room.roomType == RoomType.GENERIC_CAVERN) {
                for (Point p : level.getEmptyRoomMapAlongWall(room.roomId)) {
                    level.cell(p).terrain = Game.random.nextInt(2) == 0 ? Terrain.get("dirt1") : Terrain.get("dirt2");
                }
            }
        }
        Terrain terrainDirt1 = Terrain.get("dirt1");
        Terrain terrainDirt2 = Terrain.get("dirt2");
        Terrain cavern = Terrain.get("cavernwall");
        for (int i=0; i<level.getWidth(); i++) {
            for (int j=0; j<level.getHeight(); j++) {
                if (level.cell(i, j).terrain == wall) {
                    for (Point p : level.surroundingTiles(new Point(i, j))) {
                        if (level.cell(p).terrain == terrainDirt1 || level.cell(p).terrain == terrainDirt2) {
                            level.cell(i, j).terrain = cavern;
                            break;
                        }
                    }
                }
            }
        }

    }
}
