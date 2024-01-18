package com.bigsagebeast.hero.glyphtile;

import com.bigsagebeast.hero.roguelike.world.Entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public enum IconGlyph {

    DAMAGE("icon.damage", new PaletteEntry(Palette.COLOR_BROWN, Palette.COLOR_RED)),
    TOHIT("icon.tohit", new PaletteEntry(Palette.COLOR_WHITE, Palette.COLOR_RED)),
    PENETRATION("icon.penetration", new PaletteEntry(Palette.COLOR_WHITE, Palette.COLOR_RED)),
    DEFENSE("icon.defense", new PaletteEntry(Palette.COLOR_WHITE, Palette.COLOR_RED)),
    THICKNESS("icon.thickness", new PaletteEntry(Palette.COLOR_WHITE, Palette.COLOR_RED));

    IconGlyph(String glyphName, PaletteEntry palette) {
        this.glyphName = glyphName;
        this.palette = palette;
    }

    private final String glyphName;
    private final PaletteEntry palette;
    private GlyphTile glyphTile = null;
    public GlyphTile icon() {
        if (glyphTile != null) {
            return glyphTile;
        }
        glyphTile = GlyphIndex.get(glyphName).create(palette);
        return glyphTile;
    }
}
