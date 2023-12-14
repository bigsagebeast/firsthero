package com.churchofcoyote.hero.roguelike.world.dungeon.generation;

import com.churchofcoyote.hero.roguelike.world.Terrain;
import com.churchofcoyote.hero.roguelike.world.dungeon.Level;
import com.churchofcoyote.hero.util.Compass;
import com.churchofcoyote.hero.util.Point;

import java.util.List;

public class Generator {
    public Level level;

    private Terrain wall;
    private Terrain uncarveable;
    private Terrain floor;
    private Terrain doorway;

    public Level generate(String name, int width, int height) {
        level = new Level(name, width, height);

        wall = Terrain.get("wall");
        uncarveable = Terrain.get("uncarveable");
        floor = Terrain.get("dirt");
        doorway = Terrain.get("doorway");

        for (int i=0; i<width; i++) {
            for (int j=0; j<height; j++) {
                level.cell(i, j).terrain = wall;
            }
        }

        RoomPacker roomPacker = new RoomPacker(level, 0, 0, 30, 20, 8, Compass.SOUTH);
        roomPacker.generate();

        SubDungeonAssigner assigner = new SubDungeonAssigner(roomPacker.firstNode, Theme.goblinTheme);
        assigner.assign();

        Brogue brogue = new Brogue(level);
        brogue.generate();

        List<Point> astarPoints = AStarLevel.path(level, roomPacker.rooms.get(0).centerPoint, brogue.rooms.get(0).centerPoint, 1000.0f);
        for (Point p : astarPoints) {
            if (!level.cell(p).terrain.isPassable()) {
                level.cell(p).terrain = floor;
            }
        }


        for (int i=0; i<width; i++) {
            for (int j=0; j<height; j++) {
                if (level.cell(i, j).terrain == uncarveable) {
                    level.cell(i, j).terrain = wall;
                }
            }
        }

        return level;
    }
}
