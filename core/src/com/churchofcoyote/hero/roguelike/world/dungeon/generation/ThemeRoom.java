package com.churchofcoyote.hero.roguelike.world.dungeon.generation;

import com.churchofcoyote.hero.roguelike.world.dungeon.RoomType;

import java.util.ArrayList;
import java.util.List;

public class ThemeRoom {
    // Depth: 0 is first room only. 5 is deepest room only.  1-4 are quartiles.
    // softCap is -1 for unlimited
    // hardCap doesn't permit -1
    public RoomType type;
    public String key;
    public int softCap;
    public int hardCap;
    public int depth;
    public float priority;
    public Theme.ThemeLoopsPreferred loopsPreferred;

    // TODO affinity: Preferred ThemeRoom keys to spawn next to

    public ThemeRoom(RoomType type, String key, int softCap, int hardCap, int depth, float priority, Theme.ThemeLoopsPreferred loopsPreferred) {
        this.key = key;
        this.type = type;
        this.softCap = softCap;
        this.hardCap = hardCap;
        this.depth = depth;
        this.priority = priority;
        this.loopsPreferred = loopsPreferred;
    }
}
