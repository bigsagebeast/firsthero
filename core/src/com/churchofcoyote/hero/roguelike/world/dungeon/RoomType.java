package com.churchofcoyote.hero.roguelike.world.dungeon;

import com.churchofcoyote.hero.roguelike.world.dungeon.generation.SpecialSpawner;

import java.util.ArrayList;
import java.util.List;

public class RoomType {
    public static RoomType GENERIC_UPSTAIR = new RoomType("Generic Room", "You enter a generic room.", false);
    public static RoomType GENERIC_DOWNSTAIR = new RoomType("Generic Room", "You enter a generic room.", false);
    public static RoomType GENERIC = new RoomType("Generic Room", "You enter a generic room.", false);
    public static RoomType SUBDUNGEON_UNASSIGNED = new RoomType("UNASSIGNED SUBDUNGEON", "ERR: UNASSIGNED SUBDUNGEON ROOM", true);

    public String roomName;
    public String entranceMessage;
    public boolean specialCorridors;
    public List<SpecialSpawner> spawners = new ArrayList<>();

    public RoomType() {
    }

    public RoomType(String roomName, String entranceMessage, boolean specialCorridors) {
        this.roomName = roomName;
        this.entranceMessage = entranceMessage;
        this.specialCorridors = specialCorridors;
    }
}
