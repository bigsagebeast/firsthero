package com.bigsagebeast.hero.glyphtile;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.bigsagebeast.hero.roguelike.game.Game;
import com.bigsagebeast.hero.util.Util;

public class GlyphTile {
    public int lastGlitch;
    public long glitchNextChange = -1;
    public int glitchStayLengthRandom = 250;
    public int glitchStayLengthMin = 250;
    public Texture[] textures;
    public Texture[] grayTextures;
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
        this.textures = new Texture[] { texture };
        this.grayTextures = new Texture[] { grayTexture };
        this.flipH = flipH;
    }

    public GlyphTile(Texture[] textures, Texture[] grayTextures) { this(textures, grayTextures, false); }

    public GlyphTile(Texture[] textures, Texture[] grayTextures, boolean flipH)
    {
        this.textures = textures;
        this.grayTextures = grayTextures;
        this.flipH = flipH;
    }

    public Texture getTexture(boolean gray) {
        if (glitchNextChange == -1) {
            glitchNextChange = System.currentTimeMillis();
        } else if (System.currentTimeMillis() > glitchNextChange) {
            glitchNextChange = System.currentTimeMillis() + Util.randomBetween(glitchStayLengthMin, glitchStayLengthMin + glitchStayLengthRandom);
            lastGlitch = Game.random.nextInt(textures.length);
        }
        return gray ? grayTextures[lastGlitch] : textures[lastGlitch];
    }
}
