package com.bigsagebeast.hero.glyphtile;

public class PaletteEntry {
    public int primary;
    public int secondary;
    public int tertiary;
    public int background;
    // deserialization
    private PaletteEntry() {}
    public PaletteEntry(int primary, int secondary, int tertiary, int background) {
        this.primary = primary;
        this.secondary = secondary;
        this.tertiary = tertiary;
        this.background = background;
    }
    public PaletteEntry(int primary, int secondary, int tertiary) {
        this(primary, secondary, tertiary, Palette.COLOR_TRANSPARENT);
    }
    public PaletteEntry(int primary, int secondary) {
        this(primary, secondary, Palette.COLOR_TRANSPARENT, Palette.COLOR_TRANSPARENT);
    }
    public PaletteEntry(int primary) {
        this(primary, Palette.COLOR_TRANSPARENT, Palette.COLOR_TRANSPARENT, Palette.COLOR_TRANSPARENT);
    }
}
