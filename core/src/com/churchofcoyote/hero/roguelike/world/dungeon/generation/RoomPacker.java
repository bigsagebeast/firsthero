package com.churchofcoyote.hero.roguelike.world.dungeon.generation;

import com.churchofcoyote.hero.roguelike.game.Game;
import com.churchofcoyote.hero.roguelike.world.Terrain;
import com.churchofcoyote.hero.roguelike.world.dungeon.Level;
import com.churchofcoyote.hero.util.Compass;
import com.churchofcoyote.hero.util.Point;

import java.util.ArrayList;
import java.util.HashMap;

public class RoomPacker {
    private Terrain wall;
    private Terrain floor;
    private Terrain doorway;
    private int maxRooms = 15;

    private Level level;
    public int x, y, width, height;
    private ArrayList<PackRoom> packRooms = new ArrayList<>();
    private ArrayList<RoomSize> roomSizes = new ArrayList<>();
    public RoomPacker(Level level, int x, int y, int width, int height) {
        wall = Terrain.get("wall");
        floor = Terrain.get("dirt");
        doorway = Terrain.get("doorway");

        this.level = level;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;

        roomSizes.add(new RoomSize(3, 3));
        roomSizes.add(new RoomSize(3, 5));
        roomSizes.add(new RoomSize(5, 3));
        roomSizes.add(new RoomSize(5, 5));
        roomSizes.add(new RoomSize(5, 5));
        roomSizes.add(new RoomSize(5, 7));
        roomSizes.add(new RoomSize(7, 5));
        roomSizes.add(new RoomSize(7, 7));
    }

    public void generate() {
        packRooms.add(makeInitialRoom());
        for (int i=0; i<1000; i++) {
            if (packRooms.size() >= maxRooms) {
                break;
            }
            Compass dir = pickDirection();
            PackRoom existingRoom = packRooms.get(Game.random.nextInt(packRooms.size()));
            int x, y;
            Point doorLocation;
            switch (dir) {
                case NORTH:
                    x = existingRoom.x + Game.random.nextInt(existingRoom.width / 2) * 2;
                    y = existingRoom.y;
                    doorLocation = new Point(x, y - 1);
                    break;
                case SOUTH:
                    x = existingRoom.x + Game.random.nextInt(existingRoom.width / 2) * 2;
                    y = existingRoom.y + existingRoom.height - 1;
                    doorLocation = new Point(x, y + 1);
                    break;
                case WEST:
                    x = existingRoom.x;
                    y = existingRoom.y + Game.random.nextInt(existingRoom.height / 2) * 2;
                    doorLocation = new Point(x - 1, y);
                    break;
                default:
                    x = existingRoom.x + existingRoom.width - 1;
                    y = existingRoom.y + Game.random.nextInt(existingRoom.height / 2) * 2;
                    doorLocation = new Point(x + 1, y);
                    break;
            }
            PackRoom nextRoom = expandFrom(x, y, dir);
            if (roomWithinBounds(nextRoom)) {
                boolean intersected = false;
                for (PackRoom check : packRooms) {
                    if (check.intersect(nextRoom)) {
                        intersected = true;
                        break;
                    }
                }
                if (intersected) {
                    continue;
                }
                existingRoom.neighbors.put(nextRoom, true);
                nextRoom.neighbors.put(existingRoom, true);
                // only put the door down once - ignore it from the other direction
                existingRoom.doorLocation.put(nextRoom, doorLocation);
                packRooms.add(nextRoom);
            }
        }
        commit();
    }

    public void commit() {
        for (PackRoom packRoom : packRooms) {
            for (int x = packRoom.x; x < packRoom.x + packRoom.width; x++) {
                for (int y = packRoom.y; y < packRoom.y + packRoom.height; y++) {
                    level.cell(x,y).terrain = floor;
                }
            }
            for (RoomNode neighbor : packRoom.neighbors.keySet()) {
                if (packRoom.neighbors.get(neighbor) && packRoom.doorLocation.get(neighbor) != null) {
                    level.cell(packRoom.doorLocation.get(neighbor)).terrain = doorway;
                }
            }
        }
    }


    private Compass pickDirection() {
        switch (Game.random.nextInt(4)) {
            case 0:
                return Compass.NORTH;
            case 1:
                return Compass.SOUTH;
            case 2:
                return Compass.WEST;
            default:
                return Compass.EAST;
        }
    }

    private PackRoom makeInitialRoom() {
        PackRoom packRoom = new PackRoom();
        packRoom.x = 1;
        packRoom.y = 1;
        packRoom.width = 5;
        packRoom.height = 5;
        return packRoom;
    }


    private boolean roomWithinBounds(PackRoom room) {
        return room.x >= x + 1 && room.x + room.width < x + width - 1 && room.y >= y +1 && room.y + room.height < y + height - 1;
    }

    private PackRoom expandFrom(int x, int y, Compass dir) {
        // TODO start small, expand outward?
        PackRoom room = new PackRoom();
        RoomSize roomSize = roomSizes.get(Game.random.nextInt(roomSizes.size()));
        room.width = roomSize.width;
        room.height = roomSize.height;
        // TODO, iterate through the random outcomes to find one that's snug in a corner
        // If the room 1 cell to the left or right wouldn't fit, put this in a list of 'preferred' outcomes
        // otherwise, put this in a second list of outcomes, then choose from preferred if you can and second if not
        switch (dir) {
            case NORTH:
                room.x = x - Game.random.nextInt(room.width / 2) * 2;
                room.y = y - 1 - room.height;
                break;
            case SOUTH:
                room.x = x - Game.random.nextInt(room.width / 2) * 2;
                room.y = y + 2;
                break;
            case WEST:
                room.x = x - 1 - room.width;
                room.y = y - Game.random.nextInt(room.height / 2) * 2;
                break;
            case EAST:
                room.x = x + 2;
                room.y = y - Game.random.nextInt(room.height / 2) * 2;
                break;
        }
        return room;
    }

    private class PackRoom extends RoomNode {
        int x, y, width, height;
        // site of potential door even if one isn't opened yet
        public HashMap<RoomNode, Point> doorLocation = new HashMap<>();

        public boolean intersect(PackRoom other) {
            if (x + width < other.x || other.x + other.width < x) {
                return false;
            }
            if (y + height < other.y || other.y + other.height < y) {
                return false;
            }
            return true;
        }

        public void addNeighbor(PackRoom room, Point doorLocation) {

        }
    }

    private class RoomSize {
        public int width, height;
        public RoomSize(int width, int height) {
            this.width = width;
            this.height = height;
        }
    }
}
