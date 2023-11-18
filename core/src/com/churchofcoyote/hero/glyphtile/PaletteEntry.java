package com.churchofcoyote.hero.glyphtile;

public class PaletteEntry {
    public int primary;
    public int secondary;
    public int tertiary;
    public int background;
    public PaletteEntry(int primary, int secondary, int tertiary, int background) {
        this.primary = primary;
        this.secondary = secondary;
        this.tertiary = tertiary;
        this.background = background;
    }
    public PaletteEntry(int primary, int secondary, int tertiary) {
        this(primary, secondary, tertiary, Palette.COLOR_TRANSPARENT);
    }
}
