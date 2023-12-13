package com.churchofcoyote.hero.roguelike.world.dungeon.generation;

import com.churchofcoyote.hero.roguelike.world.dungeon.RoomType;

import java.util.ArrayList;

public class Theme {
    public ArrayList<ThemeRoom> rooms = new ArrayList<>();

    public void add(ThemeRoom room) {
        rooms.add(room);
    }

    public class ThemeRoom {
        // Rooms are added in decreasing priority, aim for 0-4?
        // Depth: 0 is first room only. 5 is deepest room only.  1-4 are quartiles.
        // First pass: From high to low priority, add up to soft cap
        // Second pass: Randomly select a room that's not at hard cap
        // Behavior within the same priority is undefined
        // softCap is -1 for unlimited
        // hardCap doesn't permit -1
        RoomType type;
        public String key;
        public int priority;
        public int softCap;
        public int hardCap;
        public int depth;
        public ThemeLoopsPreferred loopsPreferred;

        // TODO affinity: Preferred ThemeRoom keys to spawn next to

        public ThemeRoom(RoomType type, String key, int priority, int softCap, int hardCap, int depth, ThemeLoopsPreferred loopsPreferred) {
            this.key = key;
            this.type = type;
            this.priority = priority;
            this.softCap = softCap;
            this.hardCap = hardCap;
            this.depth = depth;
            this.loopsPreferred = loopsPreferred;
        }
    }

    // DEAD_END: Spawn ONLY in a dead end, omit if none available
    // NEVER: Don't add loops
    // OKAY: Spawn loops only if next to PREFERRED
    // PREFERRED: Spawn loops if next to PREFERRED or OKAY
    public enum ThemeLoopsPreferred {
        DEAD_END,
        NEVER,
        OKAY,
        PREFERRED
    }
}
