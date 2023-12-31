package com.churchofcoyote.hero.roguelike.world.dungeon;

import com.churchofcoyote.hero.roguelike.world.dungeon.generation.SpecialSpawner;

import java.util.ArrayList;
import java.util.List;

public class RoomType {
    public static RoomType GENERIC_UPSTAIR = new RoomType(null, null);
    public static RoomType GENERIC_DOWNSTAIR = new RoomType(null, null);
    public static RoomType GENERIC_ROOM = new RoomType("Room", null);
    public static RoomType GENERIC_CAVERN = new RoomType("Cavern", null);
    public static RoomType UNDERGROUND_RIVER = new RoomType("Underground River", "You approach an underground river.");
    public static RoomType SUBDUNGEON_UNASSIGNED = new RoomType("UNASSIGNED SUBDUNGEON", "ERR: UNASSIGNED SUBDUNGEON ROOM");

    public static RoomType GENERIC_ANY = new RoomType("INVALID ROOM TYPE GENERIC_ANY", null, false); // just means "GENERIC_ROOM or GENERIC_CAVERN"
    public static RoomType ANY = new RoomType("ANY", null, false); // means 'select ANY room type at all' - risky?

    public static RoomType POOL = new RoomType("Reflecting Pool", "A pool ripples quietly in this room.");
    public static RoomType FORGE = new RoomType("Forge", "You enter the heat of an underground forge.");
    public static RoomType MOSSY = new RoomType(null, "Moss grows thick on the walls.");
    public static RoomType UNDERGROUND_GROVE = new RoomType("Grove", "You approach an underground grove.");

    public String roomName;
    public String entranceMessage;
    public boolean specialCorridors;
    public List<SpecialSpawner> spawners = new ArrayList<>();

    public RoomType() {
    }

    public RoomType(String roomName, String entranceMessage) {
        this(roomName, entranceMessage, false);
    }

    public RoomType(String roomName, String entranceMessage, boolean specialCorridors) {
        this.roomName = roomName;
        this.entranceMessage = entranceMessage;
        this.specialCorridors = specialCorridors;
    }
}
