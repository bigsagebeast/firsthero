package com.bigsagebeast.hero.roguelike.world.dungeon.generation;

import com.bigsagebeast.hero.util.Compass;
import com.bigsagebeast.hero.util.Point;

import java.util.ArrayList;
import java.util.Random;

public class CellularAutomata {
    private Random random;
    int width, height;
    AutomataStatus[][] cells;
    boolean[][] floodFill;
    private static final int tries = 100;

    public static AutomataStatus[][] generateOutput(int width, int height, float initial, int birth, int survival, int runs, int minCount) {
        for (int r=0; r<tries; r++) {
            CellularAutomata automata = new CellularAutomata(width, height);
            automata.fillInitial(initial);
            for (int i = 0; i < runs; i++) {
                automata.iterate8Squares(birth, survival);
            }
            if (automata.testFlood(minCount)) {
                return automata.cells;
            }
        }
        return null;
    }

    public CellularAutomata(int width, int height) {
        this.width = width;
        this.height = height;
        random = new Random(); // TODO allow for stochastic generation by passing a seed
        cells = generateStatus();
        floodFill = generateGrid();
    }

    // for randomly expanding a cell into its surroundings
    public void iterateGrowth(float chance) {
        ArrayList<Point> candidates = new ArrayList<>();
        for (int i=0; i<width; i++) {
            for (int j=0; j<height; j++) {
                if (cells[i][j] == AutomataStatus.RANDOM) {
                    boolean hasNeighbor = false;
                    for (Compass dir : Compass.points()) {
                        if (withinBounds(i+dir.getX(), j+dir.getY()) && cells[i+dir.getX()][j+dir.getY()] == AutomataStatus.TRUE) {
                            hasNeighbor = true;
                        }
                    }
                    if (hasNeighbor) {
                        candidates.add(new Point(i, j));
                    }
                }
            }
        }
        for (Point p : candidates) {
            if (random.nextFloat() < chance) {
                cells[p.x][p.y] = AutomataStatus.TRUE;
            }
        }
    }


    public AutomataStatus[][] generateStatus() {
        AutomataStatus[][] result = new AutomataStatus[width][];
        for (int i=0; i<width; i++) {
            result[i] = new AutomataStatus[height];
            for (int j=0; j<height; j++) {
                result[i][j] = AutomataStatus.RANDOM;
            }
        }
        return result;
    }

    private boolean[][] generateGrid() {
        boolean[][] result = new boolean[width][];
        for (int i=0; i<width; i++) {
            result[i] = new boolean[height];
        }
        return result;
    }

    public void fillInitial(float chance) {
        for (int i=0; i<width; i++) {
            for (int j=0; j<height; j++) {
                if (cells[i][j] == AutomataStatus.RANDOM) {
                    cells[i][j] = random.nextFloat() < chance ? AutomataStatus.TRUE : AutomataStatus.FALSE;
                }
            }
        }
    }

    public void iterate8Squares(int birth, int survival) {
        AutomataStatus[][] nextGen = generateStatus();
        for (int i=0; i<width; i++) {
            for (int j=0; j<height; j++) {
                int count = 0;
                for (Compass dir : Compass.points()) {
                    count += (!withinBounds(i+dir.getX(), j+dir.getY()) || cells[i+dir.getX()][j+dir.getY()].isWall) ? 1 : 0;
                }
                if (cells[i][j].isImmutable) {
                    nextGen[i][j] = cells[i][j];
                } else {
                    if (cells[i][j].isWall) {
                        nextGen[i][j] = count >= birth ? AutomataStatus.TRUE : AutomataStatus.FALSE;
                    } else {
                        nextGen[i][j] = count >= survival ? AutomataStatus.TRUE : AutomataStatus.FALSE;
                    }
                }
            }
        }
        cells = nextGen;
    }

    private boolean withinBounds(int x, int y) {
        return x >= 0 && x < width && y >= 0 && y < height;
    }

    private boolean testFlood(int minCount) {
        // find starting open point
        floodFill = generateGrid();
        int startX = 0;
        int startY = 0;
        for (int i=0; i<width; i++) {
            for (int j=0; j<height; j++) {
                if (!cells[i][j].isWall) {
                    // should make it stop iterating here
                    startX = i;
                    startY = j;
                }
            }
        }
        if (cells[startX][startY].isWall) {
            return false;
        }

        recurseFlood(startX, startY);

        int openCount = 0;
        for (int i=0; i<width; i++) {
            for (int j=0; j<height; j++) {
                if (!cells[i][j].isWall) {
                    openCount++;
                    if (!floodFill[i][j]) {
                        return false;
                    }
                }
            }
        }
        return (openCount >= minCount);
    }

    private void recurseFlood(int x, int y) {
        if (!withinBounds(x, y) || cells[x][y].isWall || floodFill[x][y]) {
            return;
        }
        floodFill[x][y] = true;
        recurseFlood(x-1, y);
        recurseFlood(x, y-1);
        recurseFlood(x+1, y);
        recurseFlood(x-1, y+1);
    }
}
