package com.churchofcoyote.hero.glyphtile;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;

public class GlyphTile {
    public Texture texture;
    public Texture grayTexture;

    // TODO do we need this?  should we just have a null GlyphTile instead?
    public static GlyphTile BLANK = new GlyphTile(
            new Texture(new Pixmap(16, 24, Pixmap.Format.RGBA8888)),
            new Texture(new Pixmap(16, 24, Pixmap.Format.RGBA8888)));

    public GlyphTile(Texture texture, Texture grayTexture)
    {
        this.texture = texture;
        this.grayTexture = grayTexture;

        //texture.getTextureData().prepare();
        //Pixmap p = texture.getTextureData().consumePixmap();
        //System.out.println(String.format("%08x", p.getPixel(8, 1)));
    }
}
