package com.bigsagebeast.hero.roguelike.world.dungeon;

import com.bigsagebeast.hero.roguelike.game.Game;
import com.bigsagebeast.hero.util.Util;

import java.util.ArrayList;

public class DungeonPlan {
    public ArrayList<DungeonPlanFloor> floors = new ArrayList<>();

    public DungeonPlan(int numFloors) {
        for (int i=0; i<numFloors; i++) {
            floors.add(new DungeonPlanFloor());
        }

        boolean[] hasWater = new boolean[numFloors];
        boolean[] hasFire = new boolean[numFloors];
        boolean[] hasNaturae = new boolean[numFloors];
        hasWater[0] = true;
        hasFire[0] = true;
        hasNaturae[0] = true;
        for (int i=1; i<numFloors; i++) {
            hasWater[i] = !hasWater[i - 1] || Game.random.nextInt(2) == 0;
            hasFire[i] = !hasFire[i - 1] || Game.random.nextInt(2) == 0;
            hasNaturae[i] = !hasNaturae[i - 1] || Game.random.nextInt(2) == 0;
            if (hasWater[i] == hasFire[i] && hasWater[i] == hasNaturae[i]) {
                int choice = Game.random.nextInt(3);
                if (choice == 0) {
                    hasWater[i] ^= true;
                } else if (choice == 1) {
                    hasFire[i] ^= true;
                } else {
                    hasNaturae[i] ^= true;
                }
            }
        }

        for (int i=0; i<numFloors; i++) {
            if (hasWater[i]) {
                int waterFeature = Game.random.nextInt(2);
                if (waterFeature == 0) {
                    floors.get(i).addFeature(new DungeonPlanFeature("river"));
                } else {
                    floors.get(i).addFeature(new DungeonPlanFeature("pool"));
                }
            }
            if (hasFire[i]) {
                floors.get(i).addFeature(new DungeonPlanFeature("forge"));
            }
            if (hasNaturae[i]) {
                int naturaeFeature = Game.random.nextInt(2);
                if (naturaeFeature == 0) {
                    floors.get(i).addFeature(new DungeonPlanFeature("grove"));
                } else {
                    floors.get(i).addFeature(new DungeonPlanFeature("mossy"));
                }
            }
        }

        floors.get(Util.randomBetween(0, 7)).addFeature(new DungeonPlanFeature("hp regen up"));
        floors.get(Util.randomBetween(0, 7)).addFeature(new DungeonPlanFeature("sp regen up"));
        floors.get(Util.randomBetween(0, 7)).addFeature(new DungeonPlanFeature("dp regen up"));

        floors.get(1).addFeature(new DungeonPlanFeature("goblin outpost"));
        floors.get(Util.randomBetween(2, 3)).addFeature(new DungeonPlanFeature("copper"));
        floors.get(Util.randomBetween(2, 3)).addFeature(new DungeonPlanFeature("goblin stronghold"));
        floors.get(Util.randomBetween(3, 4)).addFeature(new DungeonPlanFeature("rot spawner"));
        floors.get(Util.randomBetween(4, 5)).addFeature(new DungeonPlanFeature("tech corridor"));
    }

    public DungeonPlanFloor floor(int floorNum) {
        return floors.get(floorNum);
    }
}
