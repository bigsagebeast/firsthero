package com.bigsagebeast.hero.roguelike.world.enums;

import com.bigsagebeast.hero.glyphtile.Palette;
import com.bigsagebeast.hero.glyphtile.PaletteEntry;

public enum Alignment {

    AVATAR("Avatar", "A", new PaletteEntry(Palette.COLOR_GRAY, Palette.COLOR_PURPLE, Palette.COLOR_PINK)),
    CONTROL("Control", "C", new PaletteEntry(Palette.COLOR_GRAY, Palette.COLOR_BLUE, Palette.COLOR_SKYBLUE)),
    HIERARCHY("Hierarchy", "H", new PaletteEntry(Palette.COLOR_GRAY, Palette.COLOR_LIGHTGREEN, Palette.COLOR_CHARTREUSE)),
    POWER("Power", "P", new PaletteEntry(Palette.COLOR_GRAY, Palette.COLOR_BROWN, Palette.COLOR_RED));

    public String name;
    public String symbol;
    public PaletteEntry palette;


    Alignment(String name, String symbol, PaletteEntry palette) {
        this.name = name;
        this.symbol = symbol;
        this.palette = palette;
    }
}
