package com.churchofcoyote.hero.roguelike.world.dungeon.generation;

import com.churchofcoyote.hero.roguelike.game.Game;
import com.churchofcoyote.hero.roguelike.world.LevelTransition;
import com.churchofcoyote.hero.roguelike.world.Terrain;
import com.churchofcoyote.hero.roguelike.world.dungeon.Level;
import com.churchofcoyote.hero.roguelike.world.dungeon.LevelCell;
import com.churchofcoyote.hero.roguelike.world.dungeon.Room;
import com.churchofcoyote.hero.roguelike.world.dungeon.RoomType;
import com.churchofcoyote.hero.util.Compass;
import com.churchofcoyote.hero.util.Point;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class Generator {
    public Level level;

    private Terrain wall;
    private Terrain uncarveable;
    private Terrain floor;
    private Terrain doorway;

    public Level generate(String name, int width, int height) {
        level = new Level(name, width, height);

        wall = Terrain.get("wall");
        uncarveable = Terrain.get("uncarveable");
        floor = Terrain.get("dirt");
        doorway = Terrain.get("doorway");

        for (int i=0; i<width; i++) {
            for (int j=0; j<height; j++) {
                level.cell(i, j).terrain = wall;
            }
        }

        RoomPacker roomPacker = new RoomPacker(level, 0, 0, 30, 20, 8, Compass.SOUTH);
        roomPacker.generate();

        SubDungeonAssigner assigner = new SubDungeonAssigner(roomPacker.firstNode, Themepedia.get("goblin.stronghold"));
        assigner.assign();

        Brogue brogue = new Brogue(level);
        brogue.generate();

        List<Point> astarPoints = AStarLevel.path(level, roomPacker.rooms.get(0).centerPoint, brogue.rooms.get(0).centerPoint, 1000.0f);
        for (Point p : astarPoints) {
            if (!level.cell(p).terrain.isPassable()) {
                level.cell(p).terrain = floor;
            }
        }

        for (int i=0; i<width; i++) {
            for (int j=0; j<height; j++) {
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

        List<Room> genericRooms = level.rooms.stream().filter(r -> r.roomType == RoomType.GENERIC).collect(Collectors.toList());
        if (genericRooms.size() < 2) {
            throw new RuntimeException("Not enough rooms for an upstair and a downstair!");
        }

        for (Room room : level.rooms) {
            if (room.roomType != null) {
                for (SpecialSpawner spawner : room.roomType.spawners) {
                    spawner.spawnInRoom(level, room.roomId);
                }
            }
        }

        return level;
    }

    public void addUpstairTo(String levelKey) {
        Room stairRoom;
        List<Room> genericRooms = level.rooms.stream().filter(r -> r.roomType == RoomType.GENERIC).collect(Collectors.toList());
        if (genericRooms.isEmpty()) {
            System.out.println("Not enough rooms to generate a stair cleanly!");
            stairRoom = level.rooms.get(Game.random.nextInt(level.rooms.size()));
        } else {
            Collections.shuffle(genericRooms);
            stairRoom = genericRooms.get(0);
            stairRoom.roomType = RoomType.GENERIC_UPSTAIR;
        }
        Point stairPoint = findEmptyPointInRoom(stairRoom);
        level.cell(stairPoint).terrain = Terrain.get("upstair");
        level.addTransition(new LevelTransition("up", stairPoint, level.getName(), levelKey));
    }

    public void addDownstairTo(String levelKey) {
        Room stairRoom;
        List<Room> genericRooms = level.rooms.stream().filter(r -> r.roomType == RoomType.GENERIC).collect(Collectors.toList());
        if (genericRooms.isEmpty()) {
            System.out.println("Not enough rooms to generate a stair cleanly!");
            stairRoom = level.rooms.get(Game.random.nextInt(level.rooms.size()));
        } else {
            Collections.shuffle(genericRooms);
            stairRoom = genericRooms.get(0);
            stairRoom.roomType = RoomType.GENERIC_DOWNSTAIR;
        }
        Point stairPoint = findEmptyPointInRoom(stairRoom);
        level.cell(stairPoint).terrain = Terrain.get("downstair");
        level.addTransition(new LevelTransition("down", stairPoint, level.getName(), levelKey));
    }

    private Point findEmptyPointInRoom(Room room) {
        // TODO update this when changing upstair and downstair to features
        List<Point> points = level.roomMap.get(room.roomId).stream()
                .filter(p -> level.getEntitiesOnTile(p).isEmpty())
                .filter(p -> level.cell(p).terrain != Terrain.get("upstair"))
                .filter(p -> level.cell(p).terrain != Terrain.get("downstair"))
                .collect(Collectors.toList());
        if (points == null) {
            throw new RuntimeException("Failed to find an empty point in room " + room);
        }
        return points.get(Game.random.nextInt(points.size()));
    }
}
