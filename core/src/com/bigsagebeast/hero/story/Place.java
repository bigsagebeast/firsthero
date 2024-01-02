package com.bigsagebeast.hero.story;

import com.bigsagebeast.hero.roguelike.world.dungeon.DungeonPlan;
import com.bigsagebeast.hero.roguelike.world.dungeon.DungeonPlanFeature;

public class Place {
    String key;
    public Place(String key) {
        this.key = key;
    }
    public DungeonPlan dungeonPlan;

    public void generateDungeonPlan() {
        int floors = 3;
        dungeonPlan = new DungeonPlan(floors);

        for (int i=0; i<floors; i++) {
            DungeonPlanFeature upStair = new DungeonPlanFeature("upStair");
            if (i == 0) {
                upStair.set("destination", "out");
            } else {
                upStair.set("destination", key + "." + i);
            }
            dungeonPlan.floor(i).addFeature(upStair);

            if (i < floors-1) {
                DungeonPlanFeature downStair = new DungeonPlanFeature("downStair");
                upStair.set("destination", key + "." + (i + 2));
                dungeonPlan.floor(i).addFeature(downStair);
            }
        }
    }

}
