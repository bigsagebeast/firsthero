package com.bigsagebeast.hero.story;

import java.util.HashMap;

public class Story {
    HashMap<String, Place> places;

    public Story() {
        Place dungeon = new Place("dungeon");
        places.put(dungeon.key, dungeon);
    }
}
