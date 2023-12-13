package com.churchofcoyote.hero.roguelike.world.dungeon.generation;

import com.churchofcoyote.hero.roguelike.world.dungeon.Room;

import java.util.HashMap;

public class RoomNode {
    // boolean = true for connected, false for unconnected
    public HashMap<RoomNode, Boolean> neighbors = new HashMap<>();
    public Room room;
}
