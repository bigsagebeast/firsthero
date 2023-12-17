package com.churchofcoyote.hero.roguelike.world.dungeon.generation;

import com.churchofcoyote.hero.roguelike.world.Terrain;
import com.churchofcoyote.hero.roguelike.world.dungeon.LevelCell;
import com.churchofcoyote.hero.roguelike.world.dungeon.Room;
import com.churchofcoyote.hero.roguelike.world.dungeon.RoomType;
import com.churchofcoyote.hero.util.Compass;
import com.churchofcoyote.hero.util.Point;

public class BrogueGrid {
    public LevelCell[][] cell;
    public int width, height;
    public Room room;
    public Point roomCenter;

    private Terrain wall;
    private Terrain floor;
    private Terrain doorway;
    private Terrain uncarveable;

    public BrogueGrid(int width, int height) {
        wall = Terrain.get("wall");
        floor = Terrain.get("dot");
        doorway = Terrain.get("doorway");
        uncarveable = Terrain.get("uncarveable");

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
        room = new Room(RoomType.GENERIC_ROOM, roomCenter);
    }

    public void markAllAdjacentToOpen() {
        for (int i=0; i<width; i++) {
            for (int j=0; j<height; j++) {
                if (cell[i][j].terrain == uncarveable) {
                    cell[i][j].temp = CellMatching.EXTERIOR_INVALID;
                    continue;
                }
                int count = 0;
                boolean hasDiagonal = false;
                if (cell[i][j].temp == CellMatching.INTERIOR) {
                    continue;
                }
                if (cell[i][j].terrain.isPassable())
                {
                    cell[i][j].temp = CellMatching.INTERIOR;
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
                    cell[i][j].temp = CellMatching.EXTERIOR_VALID;
                } else if (count > 1 || hasDiagonal) {
                    cell[i][j].temp = CellMatching.EXTERIOR_INVALID;
                }
            }
        }
    }

    public BrogueGrid grow(int margin) {
        BrogueGrid newGrid = new BrogueGrid(width+(margin*2), height+(margin*2));
        newGrid.roomCenter = new Point(roomCenter.x+margin, roomCenter.y+margin);
        for (int i=0; i<width; i++) {
            for (int j=0; j<height; j++) {
                newGrid.cell[i+margin][j+margin] = cell[i][j];
            }
        }
        return newGrid;
    }

    public BrogueGrid shrink() {
        int leftMargin = -1;
        int rightMargin = -1;
        int topMargin = -1;
        int bottomMargin = -1;
        boolean found = false;
        for (int x=0; x<width; x++) {
            for (int y=0; y<height && !found; y++) {
                if (cell[x][y].terrain.isPassable()) {
                    found = true;
                }
            }
            if (found) {
                break;
            }
            leftMargin++;
        }
        found = false;
        for (int x=width-1; x>=0; x--) {
            for (int y=0; y<height && !found; y++) {
                if (cell[x][y].terrain.isPassable()) {
                    found = true;
                }
            }
            if (found) {
                break;
            }
            rightMargin++;
        }
        found = false;
        for (int y=0; y<height; y++) {
            for (int x=0; x<width && !found; x++) {
                if (cell[x][y].terrain.isPassable()) {
                    found = true;
                }
            }
            if (found) {
                break;
            }
            topMargin++;
        }
        found = false;
        for (int y=height-1; y>=0; y--) {
            for (int x=0; x<width && !found; x++) {
                if (cell[x][y].terrain.isPassable()) {
                    found = true;
                }
            }
            if (found) {
                break;
            }
            bottomMargin++;
        }

        for (int i=0; i<width; i++) {
            for (int j=0; j<height; j++) {
            }
        }

        if (leftMargin + rightMargin > width || topMargin + bottomMargin > height) {
            throw new RuntimeException("Grid was empty!");
        }

        if (topMargin < 0 || bottomMargin < 0 || leftMargin < 0 || rightMargin < 0) {
            throw new RuntimeException("No margin!");
        }

        BrogueGrid newGrid = new BrogueGrid(width - leftMargin - rightMargin, height - topMargin - bottomMargin);
        for (int i = leftMargin; i < width - rightMargin; i++) {
            for (int j = topMargin; j < height - bottomMargin; j++) {
                newGrid.cell[i-leftMargin][j-topMargin] = cell[i][j];
            }
        }
        newGrid.roomCenter = new Point(roomCenter.x - leftMargin, roomCenter.y - topMargin);
        return newGrid;
    }

    public boolean contains(Point p) {
        return p.x >= 0 && p.x < width && p.y >= 0 && p.y < height;
    }

    public LevelCell cell(Point p) {
        return cell[p.x][p.y];
    }
}
