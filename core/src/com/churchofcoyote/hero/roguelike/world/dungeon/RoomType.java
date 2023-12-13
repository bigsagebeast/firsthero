package com.churchofcoyote.hero.roguelike.world.dungeon;

public class RoomType {
    public static RoomType GENERIC_UPSTAIR = new RoomType("Generic Room", "You enter a generic room.");
    public static RoomType GENERIC_DOWNSTAIR = new RoomType("Generic Room", "You enter a generic room.");
    public static RoomType BROGUE_GENERIC = new RoomType("Generic Room", "You enter a generic room.");
    public static RoomType SUBDUNGEON_UNASSIGNED = new RoomType("UNASSIGNED SUBDUNGEON", "ERR: UNASSIGNED SUBDUNGEON ROOM");

    public String roomName;
    public String entranceMessage;

    public RoomType() {
    }

    public RoomType(String roomName, String entranceMessage) {
        this.roomName = roomName;
        this.entranceMessage = entranceMessage;
    }
}
