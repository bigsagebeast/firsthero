package com.churchofcoyote.hero.glyphtile;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;

public class BaseGlyph {
    public Pixmap pixmap;

    public BaseGlyph(Pixmap pixmap) {
        this.pixmap = pixmap;
    }

    public GlyphTile create(PaletteEntry paletteEntry) {
        Pixmap destination = new Pixmap(GlyphEngine.GLYPH_WIDTH, GlyphEngine.GLYPH_HEIGHT, Pixmap.Format.RGBA8888);

        for (int x=0; x<GlyphEngine.GLYPH_WIDTH; x++) {
            for (int y=0; y<GlyphEngine.GLYPH_HEIGHT; y++) {
                if (pixmap.getPixel(x, y) == Palette.getColor(Palette.SOURCE_PRIMARY)) {
                    destination.drawPixel(x, GlyphEngine.GLYPH_HEIGHT - y, Palette.getColor(paletteEntry.primary));
                }
                else if (pixmap.getPixel(x, y) == Palette.getColor(Palette.SOURCE_SECONDARY)) {
                    destination.drawPixel(x, GlyphEngine.GLYPH_HEIGHT - y, Palette.getColor(paletteEntry.secondary));
                }
                else if (pixmap.getPixel(x, y) == Palette.getColor(Palette.SOURCE_TERTIARY)) {
                    destination.drawPixel(x, GlyphEngine.GLYPH_HEIGHT - y, Palette.getColor(paletteEntry.tertiary));
                }
                else if (pixmap.getPixel(x, y) == Palette.getColor(Palette.SOURCE_BACKGROUND)) {
                    destination.drawPixel(x, GlyphEngine.GLYPH_HEIGHT - y, Palette.getColor(paletteEntry.background));
                } else {
                    destination.drawPixel(x, GlyphEngine.GLYPH_HEIGHT - y, 0xffaaaaff);
                }
            }
        }
        return new GlyphTile(new Texture(destination));
    }
}
