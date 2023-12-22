package com.churchofcoyote.hero.roguelike.world.dungeon;

import java.util.ArrayList;

public class DungeonPlanFloor {
    public ArrayList<DungeonPlanFeature> features = new ArrayList<>();
    public DungeonPlanRestrictions restrictions;

    public DungeonPlanFloor() {
    }

    public void addFeature(DungeonPlanFeature feature) {
        features.add(feature);
    }
}
