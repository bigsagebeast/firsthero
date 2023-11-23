package com.churchofcoyote.hero.roguelike.world.dungeon.generation;

import com.churchofcoyote.hero.glyphtile.Palette;
import com.churchofcoyote.hero.roguelike.game.Game;
import com.churchofcoyote.hero.roguelike.world.Bestiary;
import com.churchofcoyote.hero.roguelike.world.Entity;
import com.churchofcoyote.hero.roguelike.world.dungeon.Level;
import com.churchofcoyote.hero.roguelike.world.Terrain;
import com.churchofcoyote.hero.roguelike.world.dungeon.LevelCell;
import com.churchofcoyote.hero.util.Compass;
import com.churchofcoyote.hero.util.Point;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import static com.badlogic.gdx.math.MathUtils.random;

public class Brogue {
    private Level level;
    private Grid levelGrid;
    private Random random = new Random();

    private final int CIRCULAR_RADIUS_MIN = 2;
    private final int CIRCULAR_RADIUS_MAX = 6;

    private Terrain wall;
    private Terrain floor;
    private Terrain doorway;

    public Brogue() {
        wall = Terrain.get("wall");
        floor = Terrain.get("dirt");
        doorway = Terrain.get("doorway");
    }

    public Level generate() {
        level = new Level(60, 60);
        for (int i=0; i<40; i++) {
            for (int j=0; j<40; j++) {
                level.cell(i, j).terrain = wall;
            }
        }

        levelGrid = new Grid(60, 60);

        //Grid firstRoom = makeSymmetricalCross();
        Grid firstRoom = makeRectangularRoom();
        pasteGrid(firstRoom, 20, 20);

        for (int rooms=0; rooms<50; rooms++) {
            Grid room;
            int type = random.nextInt(3);
            if (type == 0) {
                room = makeCircularRoom();
            } else if (type == 1) {
                room = makeRectangularRoom();
            } else {
                room = makeSymmetricalCross();
            }

            //room = makeRectangularRoom();

            if (random.nextInt(3) == 0) {
                room = makeIntoHallwayRoom(room);
            }

            boolean success = false;
            for (int i=0; i<10000; i++) {
                int x = randomIntRange(0, level.getWidth() - room.width);
                int y = randomIntRange(0, level.getHeight() - room.height);
                Point p = findValidOverlap(room, x, y);
                if (p == null) continue;
                //System.out.println("Found on attempt " + i + "! " + x + ", " + y + ": grid size " + room.width + ", " + room.height);
                pasteGrid(room, x, y);
                levelGrid.cell[p.x][p.y].terrain = doorway;
                success = true;
                break;
            }
            if (!success) {
                //System.out.println("Couldn't add room" + ": grid size " + room.width + ", " + room.height);
            }
        }

        digPaths(10, 100, 20, 10);

        for (int i=0; i<levelGrid.width; i++) {
            for (int j=0; j<levelGrid.width; j++) {
                if (levelGrid.cell[i][j].terrain == Terrain.get("grass")) {
                    if (levelGrid.cell[i][j].temp == Boolean.TRUE) {
                        boolean redundant = false;
                        for (Compass dir : Compass.orthogonal) {
                            Point p = new Point(i+dir.getX(), j+dir.getY());
                            if (levelGrid.contains(p) && levelGrid.cell(p).terrain == Terrain.get("grass")) {
                                redundant = true;
                            }
                        }
                        if (!redundant) {
                            levelGrid.cell[i][j].terrain = doorway;
                        } else {
                            levelGrid.cell[i][j].terrain = floor;
                        }
                    } else {
                        levelGrid.cell[i][j].terrain = floor;
                    }
                }
            }
        }


        for (int i=0; i<levelGrid.width; i++) {
            for (int j=0; j<levelGrid.width; j++) {
                level.putCell(i, j, levelGrid.cell[i][j]);
            }
        }

        level.clearTemp();

        for (int i=0; i<level.getWidth(); i++) {
            for (int j=0; j<level.getHeight(); j++) {
                //level.cell(i, j).explored = true;
                level.cell(i, j).temp = null;
            }
        }


        for (int i=0; i<20; i++) {
            Point pos = findRandomPassable();
            //level.addEntity(Game.bestiary.create("goblin", null));
            Entity e = Game.bestiary.create("goblin", null);
            e.pos = pos;
            level.addEntity(e);
        }

        level.reinitialize();

        return level;
    }

    private float randomFloatRange(float min, float max) {
        return min + (random.nextFloat() * (max - min));
    }

    private int randomIntRange(int min, int max) {
        return min + random.nextInt(max - min + 1);
    }

    private void pasteGrid(Grid grid, int x, int y) {
        if (!level.withinBounds(x, y) || !level.withinBounds(x + grid.width-1, y + grid.height-1)) {
            throw new RuntimeException("Grid won't fit on level");
        }
        for (int i=0; i<grid.width; i++) {
            for (int j=0; j<grid.height; j++) {
                //if (grid.cell[i][j].temp != null) {
                if (grid.cell[i][j].terrain != wall) {
                    levelGrid.cell[x + i][y + j] = grid.cell[i][j];
                }
            }
        }
        levelGrid.markAllAdjacentToOpen();
    }

    private Grid makeCircularRoom() {
        int radius = randomIntRange(CIRCULAR_RADIUS_MIN, CIRCULAR_RADIUS_MAX);
        Grid grid = new Grid(3 + (radius * 2), 3 + (radius * 2));
        int center = (1 + radius);

        for (int i=0; i<1+(radius*2); i++) {
            for (int j=0; j<1+(radius*2); j++) {
                if (((i-center)*(i-center))+((j-center)*(j-center)) < (radius*radius)) {
                    grid.cell[i][j].terrain = floor;
                    grid.cell[i][j].temp = Boolean.FALSE;
                } else {
                    grid.cell[i][j].terrain = wall;
                }
            }
        }
        grid.markAllAdjacentToOpen();
        return grid;
    }

    private Grid makeRectangularRoom() {
        int width = randomIntRange(3, 12);
        int height = randomIntRange(3, 12);
        Grid grid = new Grid(width+2, height+2);
        for (int i=1; i<width+1; i++) {
            for (int j=1; j<height+1; j++) {
                grid.cell[i][j].terrain = floor;
                grid.cell[i][j].temp = Boolean.FALSE;
            }
        }
        grid.markAllAdjacentToOpen();
        return grid;
    }

    private Grid makeSymmetricalCross() {
        int majorWidth = randomIntRange(7, 10);
        int minorWidth = randomIntRange(3, majorWidth-4);
        if (majorWidth % 2 != minorWidth % 2) {
            minorWidth--;
        }
        Grid grid = new Grid(majorWidth+2, majorWidth+2);

        for (int x=1; x<majorWidth-1; x++) {
            for (int y=1; y<majorWidth-1; y++) {
                if (x >= (majorWidth / 2 - minorWidth / 2) && x < ((majorWidth + 1) / 2) + (minorWidth / 2) ||
                    y >= (majorWidth / 2 - minorWidth / 2) && y < ((majorWidth + 1) / 2) + (minorWidth / 2)) {
                    grid.cell[x+1][y+1].terrain = floor;
                    grid.cell[x+1][y+1].temp = Boolean.FALSE;
                }
            }
        }
        grid.markAllAdjacentToOpen();

        return grid;
    }

    private Grid makeIntoHallwayRoom(Grid grid) {
        int hallwayLength = randomIntRange(3, 8);
        ArrayList<Point> validStartPoints = new ArrayList<>();
        ArrayList<Compass> validStartDirections = new ArrayList<>();
        for (int i=0; i<grid.width; i++) {
            for (int j=0; j<grid.height; j++) {
                if (grid.cell[i][j].temp != Boolean.TRUE) continue;
                if (i - 1 >= 0 && grid.cell[i - 1][j].terrain != wall) { validStartPoints.add(new Point(i, j)); validStartDirections.add(Compass.EAST); }
                if (j - 1 >= 0 && grid.cell[i][j - 1].terrain != wall) { validStartPoints.add(new Point(i, j)); validStartDirections.add(Compass.SOUTH); }
                if (i + 1 < grid.width && grid.cell[i + 1][j].terrain != wall) { validStartPoints.add(new Point(i, j)); validStartDirections.add(Compass.WEST); }
                if (j + 1 < grid.height && grid.cell[i][j + 1].terrain != wall) { validStartPoints.add(new Point(i, j)); validStartDirections.add(Compass.NORTH); }
            }
        }
        if (validStartPoints.isEmpty()) {
            return grid;
        }
        for (int i=0; i<grid.width; i++) {
            for (int j = 0; j < grid.height; j++) {
                if (grid.cell[i][j].temp == Boolean.TRUE) {
                    grid.cell[i][j].temp = Boolean.FALSE;
                }
            }
        }
        grid = grid.grow(hallwayLength+1);
        int choose = random.nextInt(validStartPoints.size());
        Point start = validStartPoints.get(choose);
        start.x += hallwayLength+1;
        start.y += hallwayLength+1;
        Compass dir = validStartDirections.get(choose);
        for (int i=0; i<hallwayLength; i++) {
            int currentX = start.x + (dir.getX() * i);
            int currentY = start.y + (dir.getY() * i);
            grid.cell[currentX][currentY].terrain = floor;
            if (grid.cell[currentX - 1][currentY].terrain == wall) { grid.cell[currentX - 1][currentY].temp = Boolean.FALSE; }
            if (grid.cell[currentX][currentY - 1].terrain == wall) { grid.cell[currentX][currentY - 1].temp = Boolean.FALSE; }
            if (grid.cell[currentX + 1][currentY].terrain == wall) { grid.cell[currentX + 1][currentY].temp = Boolean.FALSE; }
            if (grid.cell[currentX][currentY + 1].terrain == wall) { grid.cell[currentX][currentY + 1].temp = Boolean.FALSE; }
        }
        grid.cell[start.x][start.y].terrain = doorway;
        int finalX = start.x + ((hallwayLength-1) * dir.getX());
        int finalY = start.y + ((hallwayLength-1) * dir.getY());

        if (grid.cell[finalX - 1][finalY].terrain == wall) { grid.cell[finalX - 1][finalY].temp = Boolean.TRUE; }
        if (grid.cell[finalX][finalY - 1].terrain == wall) { grid.cell[finalX][finalY - 1].temp = Boolean.TRUE; }
        if (grid.cell[finalX + 1][finalY].terrain == wall) { grid.cell[finalX + 1][finalY].temp = Boolean.TRUE; }
        if (grid.cell[finalX][finalY + 1].terrain == wall) { grid.cell[finalX][finalY + 1].temp = Boolean.TRUE; }

        return grid;
    }

    private Point findValidOverlap(Grid grid, int x, int y) {
        if (x < 0 || y < 0 || x+grid.width > levelGrid.width || y+grid.height > levelGrid.height) {
            throw new RuntimeException("Invalid grid check position");
        }
        ArrayList<Point> overlaps = new ArrayList<>();
        for (int i=0; i<grid.width; i++) {
            for (int j=0; j<grid.height; j++) {
                if ((grid.cell[i][j].temp == Boolean.FALSE && level.cell(i+x, j+y).temp != null) ||
                        (levelGrid.cell[i+x][j+y].temp == Boolean.FALSE && grid.cell[i][j].temp != null)) {
                    return null;
                }
                else if (grid.cell[i][j].temp == Boolean.TRUE && levelGrid.cell[i+x][j+y].temp == Boolean.TRUE) {
                    overlaps.add(new Point(i+x,j+y));
                }
            }
        }
        if (overlaps.isEmpty()) {
            return null;
        }
        return overlaps.get(random(overlaps.size()-1));
    }

    private void digPaths(int wantedPaths, int allowedTries, int minDistance, int minSaved) {
        int successes = 0;
        int lastSuccess = 0;
        for (int i=0; i<allowedTries; i++) {
            Point from = findRandomPassable();
            Point to = findRandomPassable();
            boolean success = considerAndDig(from, to, minDistance, minSaved);
            if (success) {
                successes++;
                lastSuccess = i;
            }
            if (successes >= allowedTries)
            {
                //System.out.println("Last success on " + lastSuccess);
                return;
            }
        }
        //System.out.println("Gave up after " + successes + ", last success on " + lastSuccess);
    }

    private Point findRandomPassable() {
        int x, y;
        do {
            x = random.nextInt(levelGrid.width);
            y = random.nextInt(levelGrid.height);
        } while (!levelGrid.cell[x][y].terrain.isPassable());
        return new Point(x, y);
    }

    private boolean considerAndDig(Point from, Point to, int minDistance, int minSaved) {
        List<Point> passablePath = AStarBrogue.path(levelGrid, from, to, false);
        if (passablePath.size() < minDistance) {
            return false;
        }

        List<Point> diggablePath = AStarBrogue.path(levelGrid, from, to, true);
        if (passablePath.size() - diggablePath.size() < minSaved) {
            return false;
        }
        for (Point p : diggablePath) {
            if (!levelGrid.cell(p).terrain.isPassable()) {
                levelGrid.cell(p).terrain = Terrain.get("grass");
            }
        }
        return true;
    }

    public class Grid {
        public LevelCell[][] cell;
        public int width, height;
        public Grid(int width, int height) {
            this.width = width;
            this.height = height;
            if (width <= 0 || height <= 0) {
                throw new RuntimeException("Undersized grid");
            }
            cell = new LevelCell[width][];
            for (int i=0; i<width; i++) {
                cell[i] = new LevelCell[height];
                for (int j=0; j<height; j++) {
                    cell[i][j] = new LevelCell();
                    cell[i][j].terrain = wall;
                }
            }
        }

        public void markAllAdjacentToOpen() {
            for (int i=0; i<width; i++) {
                for (int j=0; j<height; j++) {
                    int count = 0;
                    boolean hasDiagonal = false;
                    if (cell[i][j].temp == Boolean.FALSE) {
                        continue;
                    }
                    if (cell[i][j].terrain.isPassable())
                    {
                        cell[i][j].temp = Boolean.FALSE;
                        continue;
                    }
                    if (cell[i][j].terrain == wall) {
                        for (Compass dir : Compass.diagonals()) {
                            if (i + dir.getX() >= 0 && i + dir.getX() < width &&
                                j + dir.getY() >= 0 && j + dir.getY() < height &&
                                cell[i + dir.getX()][j + dir.getY()].terrain.isPassable()) {
                                hasDiagonal = true;
                            }
                        }
                        if (i - 1 >= 0 && cell[i - 1][j].terrain.isPassable()) count++;
                        if (j - 1 >= 0 && cell[i][j - 1].terrain.isPassable()) count++;
                        if (i + 1 < width && cell[i + 1][j].terrain.isPassable()) count++;
                        if (j + 1 < height && cell[i][j + 1].terrain.isPassable()) count++;
                    }
                    if (count == 1) {
                        cell[i][j].temp = Boolean.TRUE;
                        //cell[i][j].terrain = Terrain.get("mountain");
                    } else if (count > 1 || hasDiagonal) {
                        cell[i][j].temp = Boolean.FALSE;
                    }
                }
            }
        }

        public Grid grow(int margin) {
            Grid newGrid = new Grid(width+(margin*2), height+(margin*2));
            for (int i=0; i<width; i++) {
                for (int j=0; j<height; j++) {
                    newGrid.cell[i+margin][j+margin] = cell[i][j];
                }
            }
            return newGrid;
        }

        public boolean contains(Point p) {
            return p.x >= 0 && p.x < width && p.y >= 0 && p.y < height;
        }

        public LevelCell cell(Point p) {
            return cell[p.x][p.y];
        }
    }
}
