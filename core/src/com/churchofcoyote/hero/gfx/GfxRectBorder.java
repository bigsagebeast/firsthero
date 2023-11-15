package com.churchofcoyote.hero.gfx;

import com.badlogic.gdx.graphics.Color;

public class GfxRectBorder extends Gfx {
    public Color color;
    public float width;
    public float height;

    public GfxRectBorder(Color color, float x, float y, float width, float height) {
        super(x, y);
        this.color = color;
        this.width = width;
        this.height = height;
    }
}
