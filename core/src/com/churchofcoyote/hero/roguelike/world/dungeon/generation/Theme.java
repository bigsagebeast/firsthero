package com.churchofcoyote.hero.roguelike.world.dungeon.generation;

import com.churchofcoyote.hero.roguelike.world.dungeon.RoomType;

import java.util.ArrayList;

public class Theme {
    public ArrayList<ThemeRoom> rooms = new ArrayList<>();
    public String key;

    public static Theme goblinTheme;

    public void add(ThemeRoom room) {
        rooms.add(room);
    }

    // TODO validate
    // Only one each of ThemeRoom with depth 0 and 5


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
