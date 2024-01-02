package com.bigsagebeast.hero.roguelike.world.dungeon;

import java.util.HashMap;

public class DungeonPlanFeature {
    public String name;
    public HashMap<String, Object> settings = new HashMap<>();
    public DungeonPlanFeature(String name) {
        this.name = name;
    }
    public void set(String key, Object val) {
        settings.put(key, val);
    }
    public Object get(String key) {
        return settings.get(key);
    }
}
