package com.churchofcoyote.hero.roguelike.world.dungeon.generation;

import java.util.HashMap;
import java.util.Map;

public class Themepedia {
    public static Map<String, Theme> map = new HashMap<>();

    public static Theme get(String key) {
        return map.get(key);
    }

    public static void put(String key, Theme theme) {
        map.put(key, theme);
    }
}
