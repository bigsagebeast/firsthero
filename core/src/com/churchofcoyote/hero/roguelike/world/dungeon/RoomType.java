package com.churchofcoyote.hero.roguelike.world.dungeon;

import com.churchofcoyote.hero.roguelike.world.dungeon.generation.SpecialSpawner;

import java.util.ArrayList;
import java.util.List;

public class RoomType {
    public static RoomType GENERIC_UPSTAIR = new RoomType("Generic Room", "You enter a generic room.");
    public static RoomType GENERIC_DOWNSTAIR = new RoomType("Generic Room", "You enter a generic room.");
    public static RoomType BROGUE_GENERIC = new RoomType("Generic Room", "You enter a generic room.");
    public static RoomType SUBDUNGEON_UNASSIGNED = new RoomType("UNASSIGNED SUBDUNGEON", "ERR: UNASSIGNED SUBDUNGEON ROOM");

    public String roomName;
    public String entranceMessage;
    public List<SpecialSpawner> spawners = new ArrayList<>();

    public RoomType() {
    }

    public RoomType(String roomName, String entranceMessage) {
        this.roomName = roomName;
        this.entranceMessage = entranceMessage;
    }
}
