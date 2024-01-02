package com.bigsagebeast.hero.glyphtile;

public class BlockJoin {
    public static final int NONE = 0;
    public static final int N = 1;
    public static final int W = 2;
    public static final int NW = 3;
    public static final int E = 4;
    public static final int NE = 5;
    public static final int WE = 6;
    public static final int NWE = 7;
    public static final int S = 8;
    public static final int NS = 9;
    public static final int WS = 10;
    public static final int NWS = 11;
    public static final int ES = 12;
    public static final int NES = 13;
    public static final int WES = 14;
    public static final int NWES = 15;

    public static final int SIZE = 16;

    public static int calculate(boolean north, boolean west, boolean east, boolean south) {
        return (north ? 1 : 0) | (west ? 2 : 0) | (east ? 4 : 0) | (south ? 8 : 0);
    }
}
