package com.churchofcoyote.hero.gfx;

import com.badlogic.gdx.graphics.Color;

public class GfxRectFilled extends Gfx {
    public Color color;
    public float width;
    public float height;

    public GfxRectFilled(Color color, float x, float y, float width, float height) {
        super(x, y);
        this.color = color;
        this.width = width;
        this.height = height;
    }
}
