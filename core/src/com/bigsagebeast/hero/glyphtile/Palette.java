package com.bigsagebeast.hero.glyphtile;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Palette {

    private static HashMap<Integer, Integer> colors = new HashMap<Integer, Integer>();

    public static final int SOURCE_PRIMARY = 18;
    public static final int SOURCE_SECONDARY = 2;
    public static final int SOURCE_TERTIARY = 9;
    public static final int SOURCE_BACKGROUND = 0;

    public static final int COLOR_TRANSPARENT = 0;
    public static final int COLOR_BROWN = 1;
    public static final int COLOR_RED = 2;
    public static final int COLOR_ORANGE = 3;
    public static final int COLOR_YELLOW = 4;
    public static final int COLOR_TAN = 5;
    public static final int COLOR_CHARTREUSE = 6;
    public static final int COLOR_DARKGREEN = 7;
    public static final int COLOR_LIGHTGREEN = 8;
    public static final int COLOR_BLUE = 9;
    public static final int COLOR_CERULEAN = 10;
    public static final int COLOR_CYAN = 11;
    public static final int COLOR_SKYBLUE = 12;
    public static final int COLOR_PURPLE = 13;
    public static final int COLOR_PINK = 14;
    public static final int COLOR_FORESTGREEN = 15;
    public static final int COLOR_TEAL = 16;
    public static final int COLOR_GRAY = 17;
    public static final int COLOR_WHITE = 18;

    public static HashMap<String, Integer> stringMap = new HashMap<>();

    public static void initialize() {
        colors.put(0, 0);

        stringMap.put("transparent", 0);
        stringMap.put("brown", 1);
        stringMap.put("red", 2);
        stringMap.put("orange", 3);
        stringMap.put("yellow", 4);
        stringMap.put("tan", 5);
        stringMap.put("chartreuse", 6);
        stringMap.put("darkgreen", 7);
        stringMap.put("lightgreen", 8);
        stringMap.put("blue", 9);
        stringMap.put("cerulean", 10);
        stringMap.put("cyan", 11);
        stringMap.put("skyblue", 12);
        stringMap.put("purple", 13);
        stringMap.put("pink", 14);
        stringMap.put("forestgreen", 15);
        stringMap.put("teal", 16);
        stringMap.put("gray", 17);
        stringMap.put("white", 18);

        BufferedReader reader;
        try {
            reader = new BufferedReader(new InputStreamReader(Gdx.files.internal("tiles/palette.pal").read()));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        try {
            reader.readLine();
            reader.readLine();
            int entries = Integer.valueOf(reader.readLine());
            for (int i=1; i<=entries; i++) {
                String colorString = reader.readLine();
                String[] colorSplit = colorString.split(" ");
                int red = Integer.valueOf(colorSplit[0]);
                int green = Integer.valueOf(colorSplit[1]);
                int blue = Integer.valueOf(colorSplit[2]);
                colors.put(i, (red << 24) + (green << 16) + (blue << 8) + 0xff);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static int getColor(int index) {
        return colors.get(index);
    }
}
