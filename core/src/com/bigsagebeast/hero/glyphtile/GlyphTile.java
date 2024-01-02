package com.bigsagebeast.hero.glyphtile;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;

public class GlyphTile {
    public Texture texture;
    public Texture grayTexture;
    public boolean flipH;

    // TODO do we need this?  should we just have a null GlyphTile instead?
    public static GlyphTile BLANK = new GlyphTile(
            new Texture(new Pixmap(16, 24, Pixmap.Format.RGBA8888)),
            new Texture(new Pixmap(16, 24, Pixmap.Format.RGBA8888)));

    public GlyphTile(Texture texture, Texture grayTexture) {
        this(texture, grayTexture, false);
    }

    public GlyphTile(Texture texture, Texture grayTexture, boolean flipH)
    {
        this.texture = texture;
        this.grayTexture = grayTexture;
        this.flipH = flipH;
    }
}
