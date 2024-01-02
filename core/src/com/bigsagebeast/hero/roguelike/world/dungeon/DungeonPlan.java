package com.bigsagebeast.hero.roguelike.world.dungeon;

import java.util.ArrayList;

public class DungeonPlan {
    public ArrayList<DungeonPlanFloor> floors;

    public DungeonPlan(int numFloors) {
        for (int i=0; i<numFloors; i++) {
            floors.add(new DungeonPlanFloor());
        }
    }

    public DungeonPlanFloor floor(int floorNum) {
        return floors.get(floorNum);
    }
}
