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

        Brogue brogue = new Brogue(level);
        brogue.generate();

        return level;
    }
}
