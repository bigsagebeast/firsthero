package com.churchofcoyote.hero.glyphtile;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;

public class GlyphTile {
    public Texture texture;

    // TODO do we need this?  should we just have a null GlyphTile instead?
    public static GlyphTile BLANK = new GlyphTile(new Texture(new Pixmap(16, 24, Pixmap.Format.RGBA8888)));

    public GlyphTile(Texture texture)
    {
        this.texture = texture;
    }
}
