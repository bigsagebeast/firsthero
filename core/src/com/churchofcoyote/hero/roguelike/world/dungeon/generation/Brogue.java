package com.churchofcoyote.hero.roguelike.world.dungeon.generation;

import com.churchofcoyote.hero.roguelike.game.Game;
import com.churchofcoyote.hero.roguelike.world.Entity;
import com.churchofcoyote.hero.roguelike.world.dungeon.Level;
import com.churchofcoyote.hero.roguelike.world.Terrain;
import com.churchofcoyote.hero.roguelike.world.dungeon.Room;
import com.churchofcoyote.hero.roguelike.world.dungeon.RoomType;
import com.churchofcoyote.hero.roguelike.world.proc.environment.ProcDoor;
import com.churchofcoyote.hero.util.Compass;
import com.churchofcoyote.hero.util.Point;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static com.badlogic.gdx.math.MathUtils.random;

public class Brogue {
    private Level level;
    private BrogueGrid levelGrid;
    private Random random = new Random();

    private final int CIRCULAR_RADIUS_MIN = 3;
    private final int CIRCULAR_RADIUS_MAX = 7;

    private Terrain wall;
    private Terrain terrainDot;
    private Terrain terrainDirt1;
    private Terrain terrainDirt2;
    private Terrain doorway;

    public List<Room> rooms = new ArrayList<>();

    public Brogue(Level level) {
        wall = Terrain.get("wall");
        terrainDot = Terrain.get("dot");
        terrainDirt1 = Terrain.get("dirt1");
        terrainDirt2 = Terrain.get("dirt2");

        doorway = Terrain.get("doorway");
        this.level = level;
        levelGrid = new BrogueGrid(level.getWidth(), level.getHeight());

        for (int i=0; i<level.getWidth(); i++) {
            for (int j=0; j<level.getHeight(); j++) {
                levelGrid.cell[i][j] = level.cell(i, j);
            }
        }
        levelGrid.markAllAdjacentToOpen();
    }

    public void generate() {

        //Grid firstRoom = makeSymmetricalCross();
        //BrogueGrid firstRoom = makeRectangularRoom();
        //pasteGrid(firstRoom, 20, 20);
        BrogueGrid river = makeRiver();
        river = river.shrink();
        river.markAllAdjacentToOpen();
        //pasteGrid(river, 21, 0);
        for (int i=0; i<10000; i++) {
            int x = randomIntRange(0, level.getWidth() - river.width);
            if (!isObstructed(river, x, 0)) {
                pasteGrid(river, x, 0);
                break;
            }
        }

        for (int rooms=0; rooms<50; rooms++) {
            BrogueGrid room;
            int type = random.nextInt(8);
            if (type == 0) {
                room = makeCircularRoom();
            } else if (type == 1) {
                room = makeRectangularRoom();
            } else if (type == 2) {
                room = makeSymmetricalCross();
            } else {
                room = makeCavern();
            }

            if (random.nextInt(3) == 0) {
                room = makeIntoHallwayRoom(room);
            }
            room = room.shrink();

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
            for (int j=0; j<levelGrid.height; j++) {
                if (levelGrid.cell[i][j].terrain == Terrain.get("grass")) {
                    if (levelGrid.cell[i][j].temp == CellMatching.EXTERIOR_VALID) {
                        int adjacentTunnels = 0;
                        boolean redundant = false;
                        for (Compass dir : Compass.orthogonal) {
                            Point p = new Point(i+dir.getX(), j+dir.getY());
                            if (levelGrid.contains(p) && levelGrid.cell(p).terrain == Terrain.get("grass")) {
                                adjacentTunnels++;
                            }
                            if (levelGrid.contains(p) && levelGrid.cell(p).terrain == doorway) {
                                redundant = true;
                            }
                        }
                        if (!redundant && adjacentTunnels <= 1) {
                            levelGrid.cell[i][j].terrain = doorway;
                        } else {
                            levelGrid.cell[i][j].terrain = terrainDot;
                        }
                    } else {
                        levelGrid.cell[i][j].terrain = terrainDot;
                    }
                }
            }
        }

        for (int i=0; i<levelGrid.width; i++) {
            for (int j=0; j<levelGrid.height; j++) {
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

        for (int i=0; i<level.getWidth(); i++) {
            for (int j=0; j<level.getHeight(); j++) {
                if (level.cell(i, j).terrain == doorway) {
                    if (level.getItemsOnTile(new Point(i, j)).stream().anyMatch(item -> item.containsProc(ProcDoor.class))) {
                        // TODO brogue should only add doors to its own doorways!
                        continue;
                    }
                    Entity door = Game.itempedia.create("feature.door");
                    level.addEntityWithStacking(door, new Point(i, j));
                    if (Game.random.nextInt(2) == 0) {
                        ((ProcDoor)door.getProcByType(ProcDoor.class)).close(door);
                    } else {
                        ((ProcDoor)door.getProcByType(ProcDoor.class)).open(door);
                    }
                }
            }
        }

        level.reinitialize();
    }

    private float randomFloatRange(float min, float max) {
        return min + (random.nextFloat() * (max - min));
    }

    private int randomIntRange(int min, int max) {
        return min + random.nextInt(max - min + 1);
    }

    private void pasteGrid(BrogueGrid grid, int x, int y) {
        if (!level.withinBounds(x, y) || !level.withinBounds(x + grid.width-1, y + grid.height-1)) {
            throw new RuntimeException("Grid won't fit on level");
        }
        grid.room.roomId = level.rooms.size();
        level.rooms.add(grid.room);
        grid.room.centerPoint = new Point(grid.roomCenter.x + x, grid.roomCenter.y + y);
        rooms.add(grid.room);
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

    private BrogueGrid makeCircularRoom() {
        int radius = randomIntRange(CIRCULAR_RADIUS_MIN, CIRCULAR_RADIUS_MAX);
        BrogueGrid grid = new BrogueGrid(3 + (radius * 2), 3 + (radius * 2));
        int roomId = level.rooms.size();
        int center = (1 + radius);

        for (int i=0; i<1+(radius*2); i++) {
            for (int j=0; j<1+(radius*2); j++) {
                if (((i-center)*(i-center))+((j-center)*(j-center)) < (radius*radius)) {
                    grid.cell[i][j].terrain = terrainDot;
                    grid.cell[i][j].temp = CellMatching.INTERIOR;
                    grid.cell[i][j].roomId = roomId;
                } else {
                    grid.cell[i][j].terrain = wall;
                }
            }
        }
        grid.roomCenter = new Point(1+radius, 1+radius);
        grid.markAllAdjacentToOpen();
        return grid;
    }

    private BrogueGrid makeRectangularRoom() {
        int width = randomIntRange(4, 12);
        int height = randomIntRange(4, 12);
        int roomId = level.rooms.size();
        BrogueGrid grid = new BrogueGrid(width+2, height+2);
        for (int i=1; i<width+1; i++) {
            for (int j=1; j<height+1; j++) {
                grid.cell[i][j].terrain = terrainDot;
                grid.cell[i][j].temp = CellMatching.INTERIOR;
                grid.cell[i][j].roomId = roomId;
            }
        }
        grid.roomCenter = new Point(width/2, height/2);
        grid.markAllAdjacentToOpen();
        return grid;
    }

    private BrogueGrid makeSymmetricalCross() {
        int majorWidth = randomIntRange(7, 10);
        int minorWidth = randomIntRange(3, majorWidth-4);
        if (majorWidth % 2 != minorWidth % 2) {
            minorWidth--;
        }
        BrogueGrid grid = new BrogueGrid(majorWidth+2, majorWidth+2);
        int roomId = level.rooms.size();

        for (int x=1; x<majorWidth-1; x++) {
            for (int y=1; y<majorWidth-1; y++) {
                if (x >= (majorWidth / 2 - minorWidth / 2) && x < ((majorWidth + 1) / 2) + (minorWidth / 2) ||
                    y >= (majorWidth / 2 - minorWidth / 2) && y < ((majorWidth + 1) / 2) + (minorWidth / 2)) {
                    grid.cell[x+1][y+1].terrain = terrainDot;
                    grid.cell[x+1][y+1].temp = CellMatching.INTERIOR;
                    grid.cell[x+1][y+1].roomId = roomId;
                }
            }
        }
        grid.roomCenter = new Point(majorWidth/2, minorWidth/2);
        grid.markAllAdjacentToOpen();

        return grid;
    }

    private BrogueGrid makeCavern() {
        int width = randomIntRange(7, 25);
        int height = randomIntRange(7, 25);
        AutomataStatus[][] gridBoolean = CellularAutomata.generateOutput(width, height, 0.45f, 4, 5, 4, 20);

        /*
        CellularAutomata automata = new CellularAutomata(width, height);
        automata.fillInitial(.45f);
        for (int i=0; i<(width/2); i++) {
            for (int j=0; j<(height/2); j++) {
                automata.cells[i][j] = true;
            }
        }
        for (int i = 0; i < 4; i++) {
            automata.iterate8Squares(4, 5);
        }
        boolean[][] gridBoolean = automata.cells;
         */

        if (gridBoolean == null) {
            throw new RuntimeException("Failed to generate cavern");
        }
        BrogueGrid grid = new BrogueGrid(width+2, height+2);
        grid.room.roomType = RoomType.GENERIC_CAVERN;
        int roomId = level.rooms.size();
        for (int i=0; i<width; i++) {
            for (int j=0; j<height; j++) {
                if (!gridBoolean[i][j].isWall) {
                    grid.cell[i + 1][j + 1].terrain = terrainDot;
                }
                grid.cell[i+1][j+1].roomId = roomId;
            }
        }
        grid.roomCenter = new Point((width/2)+1, (height/2)+1);
        grid.markAllAdjacentToOpen();
        return grid;
    }

    private BrogueGrid makeIntoHallwayRoom(BrogueGrid grid) {
        int hallwayLength = randomIntRange(2, 8);
        ArrayList<Point> validStartPoints = new ArrayList<>();
        ArrayList<Compass> validStartDirections = new ArrayList<>();
        for (int i=0; i<grid.width; i++) {
            for (int j=0; j<grid.height; j++) {
                if (grid.cell[i][j].temp != CellMatching.EXTERIOR_VALID) continue;
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
                if (grid.cell[i][j].temp == CellMatching.EXTERIOR_VALID) {
                    grid.cell[i][j].temp = CellMatching.EXTERIOR_INVALID;
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
            grid.cell[currentX][currentY].terrain = terrainDot;
            grid.cell[currentX][currentY].temp = CellMatching.INTERIOR;
            if (grid.cell[currentX - 1][currentY].terrain == wall) { grid.cell[currentX - 1][currentY].temp = CellMatching.EXTERIOR_INVALID; }
            if (grid.cell[currentX][currentY - 1].terrain == wall) { grid.cell[currentX][currentY - 1].temp = CellMatching.EXTERIOR_INVALID; }
            if (grid.cell[currentX + 1][currentY].terrain == wall) { grid.cell[currentX + 1][currentY].temp = CellMatching.EXTERIOR_INVALID; }
            if (grid.cell[currentX][currentY + 1].terrain == wall) { grid.cell[currentX][currentY + 1].temp = CellMatching.EXTERIOR_INVALID; }
        }
        grid.cell[start.x][start.y].terrain = doorway;
        int finalX = start.x + ((hallwayLength-1) * dir.getX());
        int finalY = start.y + ((hallwayLength-1) * dir.getY());

        if (grid.cell[finalX - 1][finalY].terrain == wall) { grid.cell[finalX - 1][finalY].temp = CellMatching.EXTERIOR_VALID; }
        if (grid.cell[finalX][finalY - 1].terrain == wall) { grid.cell[finalX][finalY - 1].temp = CellMatching.EXTERIOR_VALID; }
        if (grid.cell[finalX + 1][finalY].terrain == wall) { grid.cell[finalX + 1][finalY].temp = CellMatching.EXTERIOR_VALID; }
        if (grid.cell[finalX][finalY + 1].terrain == wall) { grid.cell[finalX][finalY + 1].temp = CellMatching.EXTERIOR_VALID; }

        return grid;
    }

    private Point findValidOverlap(BrogueGrid grid, int x, int y) {
        if (x < 0 || y < 0 || x+grid.width > levelGrid.width || y+grid.height > levelGrid.height) {
            throw new RuntimeException("Invalid grid check position");
        }
        ArrayList<Point> overlaps = new ArrayList<>();
        for (int i=0; i<grid.width; i++) {
            for (int j=0; j<grid.height; j++) {
                if ((grid.cell[i][j].temp == CellMatching.INTERIOR && level.cell(i+x, j+y).temp != null) ||
                        (levelGrid.cell[i+x][j+y].temp == CellMatching.INTERIOR && grid.cell[i][j].temp != null)) {
                    return null;
                }
                else if (grid.cell[i][j].temp == CellMatching.EXTERIOR_VALID && levelGrid.cell[i+x][j+y].temp == CellMatching.EXTERIOR_VALID) {
                    overlaps.add(new Point(i+x,j+y));
                }
            }
        }
        if (overlaps.isEmpty()) {
            return null;
        }
        return overlaps.get(random(overlaps.size()-1));
    }

    private boolean isObstructed(BrogueGrid grid, int x, int y) {
        if (x < 0 || y < 0 || x+grid.width > levelGrid.width || y+grid.height > levelGrid.height) {
            throw new RuntimeException("Invalid grid check position");
        }
        for (int i=0; i<grid.width; i++) {
            for (int j=0; j<grid.height; j++) {
                if ((grid.cell[i][j].temp == CellMatching.INTERIOR && level.cell(i+x, j+y).temp != null) ||
                        (levelGrid.cell[i+x][j+y].temp == CellMatching.INTERIOR && grid.cell[i][j].temp != null)) {
                    return true;
                }
            }
        }
        return false;
    }

    private void digPaths(int wantedPaths, int allowedTries, int minDistance, int minSaved) {
        int successes = 0;
        int lastSuccess = 0;
        for (int i=0; i<allowedTries; i++) {
            Point from = findRandomPassableInGenericRoom();
            Point to = findRandomPassableInGenericRoom();
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

    private Point findRandomPassableInGenericRoom() {
        int x, y;
        Room room;
        do {
            x = random.nextInt(levelGrid.width);
            y = random.nextInt(levelGrid.height);
            room = levelGrid.cell[x][y].roomId < 0 ? null : level.rooms.get(levelGrid.cell[x][y].roomId);
        } while (!levelGrid.cell[x][y].terrain.isPassable() || room == null || room.roomType.specialCorridors);
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

    private BrogueGrid makeRiver() {
        int gridWidth = 26;
        int riverLeftShore = 11;
        int riverLeftShoreMin = 8;
        int riverLeftShoreMax = 14;
        int riverWidth = 5;
        int riverWidthMin = 4;
        int riverWidthMax = 6;
        int riverBankWidth = 1;
        int riverCavernWidth = 4;
        BrogueGrid riverGrid = new BrogueGrid(gridWidth, levelGrid.height);
        riverGrid.room.roomType = RoomType.UNDERGROUND_RIVER;
        riverGrid.room.roomId = level.rooms.size();
        CellularAutomata automata = new CellularAutomata(gridWidth, levelGrid.height);
        automata.cells = automata.generateStatus();
        for (int j=0; j<levelGrid.height; j++) {
            switch (random.nextInt(4)) {
                case 0:
                    if (riverLeftShore > riverLeftShoreMin) {
                        riverLeftShore--;
                    }
                    break;
                case 1:
                    if (riverLeftShore < riverLeftShoreMax) {
                        riverLeftShore++;
                    }
                    break;
            }
            switch (random.nextInt(4)) {
                case 0:
                    if (riverWidth > riverWidthMin) {
                        riverWidth--;
                    }
                    break;
                case 1:
                    if (riverWidth < riverWidthMax) {
                        riverWidth++;
                    }
                    break;
            }
            int riverRightShore = riverLeftShore + riverWidth;
            int riverLeftBank = riverLeftShore - riverBankWidth;
            int riverRightBank = riverRightShore + riverBankWidth;
            int riverLeftCavern = riverLeftBank - riverCavernWidth;
            int riverRightCavern = riverRightBank + riverCavernWidth;
            for (int i=0; i<riverGrid.width; i++) {
                if (j == 0 || j == riverGrid.height - 1) {
                    automata.cells[i][j] = AutomataStatus.ALWAYS_TRUE;
                } else {
                    if (i >= riverLeftShore && i < riverRightShore) {
                        automata.cells[i][j] = AutomataStatus.ALWAYS_FALSE;
                        riverGrid.cell[i][j].terrain = Terrain.get("water");
                    } else if (i >= riverLeftBank && i < riverRightBank) {
                        automata.cells[i][j] = AutomataStatus.ALWAYS_FALSE;
                        riverGrid.cell[i][j].terrain = random.nextInt(2) == 0 ? terrainDirt1 : terrainDirt2;
                    } else if (i >= riverLeftCavern && i < riverRightCavern) {
                        automata.cells[i][j] = AutomataStatus.RANDOM;
                    } else {
                        automata.cells[i][j] = AutomataStatus.ALWAYS_TRUE;
                    }
                }
            }
        }
        automata.fillInitial(.45f);
        for (int i = 0; i < 4; i++) {
            automata.iterate8Squares(4, 5);
        }
        for (int i=0; i<gridWidth; i++) {
            for (int j=0; j<levelGrid.height; j++) {
                riverGrid.cell[i][j].roomId = riverGrid.room.roomId;
                if (riverGrid.cell[i][j].terrain == wall) {
                    if (automata.cells[i][j].isWall) {
                        riverGrid.cell[i][j].terrain = wall;
                    } else {
                        riverGrid.cell[i][j].terrain = terrainDot;
                    }
                }
            }
        }
        riverGrid.roomCenter = new Point(riverLeftShore + riverWidth / 2, levelGrid.height / 2);
        return riverGrid;
    }
}
