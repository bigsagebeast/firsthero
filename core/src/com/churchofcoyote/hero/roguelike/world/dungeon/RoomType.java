package com.churchofcoyote.hero.roguelike.world.dungeon;

public class RoomType {
    public static RoomType BROGUE_GENERIC = new RoomType("Generic Room", "You enter a generic room.");

    public String roomName;
    public String entranceMessage;

    public RoomType() {
    }

    public RoomType(String roomName, String entranceMessage) {
        this.roomName = roomName;
        this.entranceMessage = entranceMessage;
    }
}
