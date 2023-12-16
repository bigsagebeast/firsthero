package com.churchofcoyote.hero.roguelike.world.dungeon.generation;

import com.churchofcoyote.hero.util.Compass;

import java.util.Random;

public class CellularAutomata {
    private Random random;
    int width, height;
    boolean[][] cells;
    boolean[][] floodFill;
    private static final int tries = 100;

    public static boolean[][] generateOutput(int width, int height, float initial, int birth, int survival, int runs, int minCount) {
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
        cells = generateGrid();
        floodFill = generateGrid();
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
                cells[i][j] = random.nextFloat() < chance;
            }
        }
    }

    public void iterate8Squares(int birth, int survival) {
        boolean[][] nextGen = generateGrid();
        for (int i=0; i<width; i++) {
            for (int j=0; j<height; j++) {
                int count = 0;
                for (Compass dir : Compass.points()) {
                    count += (!withinBounds(i+dir.getX(), j+dir.getY()) || cells[i+dir.getX()][j+dir.getY()]) ? 1 : 0;
                }
                if (cells[i][j]) {
                    nextGen[i][j] = count >= birth;
                } else {
                    nextGen[i][j] = count >= survival;
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
                if (!cells[i][j]) {
                    // should make it stop iterating here
                    startX = i;
                    startY = j;
                }
            }
        }
        if (cells[startX][startY]) {
            return false;
        }

        recurseFlood(startX, startY);

        int openCount = 0;
        for (int i=0; i<width; i++) {
            for (int j=0; j<height; j++) {
                if (!cells[i][j]) {
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
        if (!withinBounds(x, y) || cells[x][y] || floodFill[x][y]) {
            return;
        }
        floodFill[x][y] = true;
        recurseFlood(x-1, y);
        recurseFlood(x, y-1);
        recurseFlood(x+1, y);
        recurseFlood(x-1, y+1);
    }
}
