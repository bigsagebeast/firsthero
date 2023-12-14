package com.churchofcoyote.hero.roguelike.world.dungeon.generation;

import com.churchofcoyote.hero.roguelike.world.dungeon.RoomType;

import java.util.ArrayList;

public class Theme {
    public ArrayList<ThemeRoom> rooms = new ArrayList<>();

    public static Theme goblinTheme;

    static {
        goblinTheme = new Theme();
        goblinTheme.add(new ThemeRoom(new RoomType("Throne Room", "You enter a goblin treasure vault!"),
                "goblin.treasureroom", 1, 1, 5, 1.0f, ThemeLoopsPreferred.NEVER));
        goblinTheme.add(new ThemeRoom(new RoomType("Throne Room", "You enter a dirty goblin throne room."),
                "goblin.throneroom", 1, 1, 4, 4.0f, ThemeLoopsPreferred.NEVER));
        goblinTheme.add(new ThemeRoom(new RoomType("Goblin Entrance", "You step into a goblin stronghold."),
                "goblin.entrance", 1, 1, 0, 1.0f, ThemeLoopsPreferred.OKAY));
        goblinTheme.add(new ThemeRoom(new RoomType("Goblin Barracks", "Tattered furs cover the ground."),
                "goblin.barracks", 2, -1, 1, 1.0f, ThemeLoopsPreferred.OKAY));
        goblinTheme.add(new ThemeRoom(new RoomType("Goblin Guard Post", "Poor barricades mark the entrances to this guard post."),
                "goblin.guardpost", 2, -1, 2, 1.0f, ThemeLoopsPreferred.PREFERRED));
        goblinTheme.add(new ThemeRoom(new RoomType("Goblin Armory", "Broken and rusted scraps of metal lean against the walls of this goblin armory."),
                "goblin.armory", 2, -1, 3, 1.0f, ThemeLoopsPreferred.OKAY));
        goblinTheme.add(new ThemeRoom(new RoomType("Goblin Kitchen", "The stench of cooked carcasses surrounds you."),
                "goblin.kitchen", 1, -1, 2, 1.0f, ThemeLoopsPreferred.OKAY));
        goblinTheme.add(new ThemeRoom(new RoomType("Goblin Chapel", "Desecrated religious icons are painted on the walls."),
                "goblin.chapel", 1, 1, 2, 1.0f, ThemeLoopsPreferred.OKAY));
    }

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
