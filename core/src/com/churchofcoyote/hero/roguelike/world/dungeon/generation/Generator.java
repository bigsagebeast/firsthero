package com.churchofcoyote.hero.roguelike.world.dungeon.generation;

import com.churchofcoyote.hero.roguelike.world.Terrain;
import com.churchofcoyote.hero.roguelike.world.dungeon.Level;

public class Generator {
    public Level level;

    private Terrain wall;
    private Terrain floor;
    private Terrain doorway;

    public Level generate(String name, int width, int height) {
        level = new Level(name, width, height);

        wall = Terrain.get("wall");
        floor = Terrain.get("dirt");
        doorway = Terrain.get("doorway");

        for (int i=0; i<width; i++) {
            for (int j=0; j<height; j++) {
                level.cell(i, j).terrain = wall;
            }
        }

        RoomPacker roomPacker = new RoomPacker(level, 0, 0, 40, 20);
        roomPacker.generate();

        //Brogue brogue = new Brogue(level);
        //brogue.generate();

        return level;
    }
}
