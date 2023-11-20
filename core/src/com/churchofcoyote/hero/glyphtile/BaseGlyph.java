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
        Pixmap destinationGray = new Pixmap(GlyphEngine.GLYPH_WIDTH, GlyphEngine.GLYPH_HEIGHT, Pixmap.Format.RGBA8888);
        for (int x=0; x<GlyphEngine.GLYPH_WIDTH; x++) {
            for (int y=0; y<GlyphEngine.GLYPH_HEIGHT; y++) {
                if (pixmap.getPixel(x, y) == Palette.getColor(Palette.SOURCE_PRIMARY)) {
                    destination.drawPixel(x, GlyphEngine.GLYPH_HEIGHT - y - 1, Palette.getColor(paletteEntry.primary));
                    destinationGray.drawPixel(x, GlyphEngine.GLYPH_HEIGHT - y - 1, grayscale(Palette.getColor(paletteEntry.primary)));
                }
                else if (pixmap.getPixel(x, y) == Palette.getColor(Palette.SOURCE_SECONDARY)) {
                    destination.drawPixel(x, GlyphEngine.GLYPH_HEIGHT - y - 1, Palette.getColor(paletteEntry.secondary));
                    destinationGray.drawPixel(x, GlyphEngine.GLYPH_HEIGHT - y - 1, grayscale(Palette.getColor(paletteEntry.secondary)));
                }
                else if (pixmap.getPixel(x, y) == Palette.getColor(Palette.SOURCE_TERTIARY)) {
                    destination.drawPixel(x, GlyphEngine.GLYPH_HEIGHT - y - 1, Palette.getColor(paletteEntry.tertiary));
                    destinationGray.drawPixel(x, GlyphEngine.GLYPH_HEIGHT - y - 1, grayscale(Palette.getColor(paletteEntry.tertiary)));
                }
                else if (pixmap.getPixel(x, y) == Palette.getColor(Palette.SOURCE_BACKGROUND)) {
                    destination.drawPixel(x, GlyphEngine.GLYPH_HEIGHT - y - 1, Palette.getColor(paletteEntry.background));
                    destinationGray.drawPixel(x, GlyphEngine.GLYPH_HEIGHT - y - 1, grayscale(Palette.getColor(paletteEntry.background)));
                } else {
                    destination.drawPixel(x, GlyphEngine.GLYPH_HEIGHT - y - 1, 0xffaaaaff);
                    destinationGray.drawPixel(x, GlyphEngine.GLYPH_HEIGHT - y - 1, 0xffaaaaff);
                }
            }
        }

        return new GlyphTile(new Texture(destination), new Texture(destinationGray));
    }

    private static int grayscale(int color) {
        int red = ((color & 0xff000000) >> 24) & 0x000000ff;
        int green = (color & 0x00ff0000) >> 16;
        int blue = (color & 0x0000ff00) >> 8;
        int alpha = (color & 0x000000ff);
        int average = (red + blue + green) / 3;
        int grayscale = (average << 24) | (average << 16) | (average << 8) | alpha;
        return grayscale;
    }
}
