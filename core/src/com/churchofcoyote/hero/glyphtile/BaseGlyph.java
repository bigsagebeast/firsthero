package com.churchofcoyote.hero.glyphtile;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;

public class BaseGlyph {
    public Pixmap pixmap;

    public BaseGlyph(Pixmap pixmap) {
        this.pixmap = pixmap;
    }

    public GlyphTile create(int primary, int secondary, int tertiary, int background) {
        Pixmap destination = new Pixmap(GlyphEngine.GLYPH_WIDTH, GlyphEngine.GLYPH_HEIGHT, Pixmap.Format.RGBA8888);

        for (int x=0; x<GlyphEngine.GLYPH_WIDTH; x++) {
            for (int y=0; y<GlyphEngine.GLYPH_HEIGHT; y++) {
                if (pixmap.getPixel(x, y) == Palette.getColor(Palette.SOURCE_PRIMARY)) {
                    destination.drawPixel(x, y, Palette.getColor(primary));
                }
                else if (pixmap.getPixel(x, y) == Palette.getColor(Palette.SOURCE_SECONDARY)) {
                    destination.drawPixel(x, y, Palette.getColor(secondary));
                }
                else if (pixmap.getPixel(x, y) == Palette.getColor(Palette.SOURCE_TERTIARY)) {
                    destination.drawPixel(x, y, Palette.getColor(tertiary));
                }
                else if (pixmap.getPixel(x, y) == Palette.getColor(Palette.SOURCE_BACKGROUND)) {
                    destination.drawPixel(x, y, Palette.getColor(background));
                } else {
                    destination.drawPixel(x, y, 0xffaaaaff);
                }
            }
        }
        return new GlyphTile(new Texture(destination));
    }
}
