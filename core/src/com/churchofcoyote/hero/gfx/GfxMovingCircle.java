package com.churchofcoyote.hero.gfx;

import com.badlogic.gdx.graphics.Color;
import com.churchofcoyote.hero.GameLoop;
import com.churchofcoyote.hero.Graphics;
import com.churchofcoyote.hero.HeroGame;
import com.churchofcoyote.hero.roguelike.world.Entity;

public class GfxMovingCircle extends Gfx {
    public float startRadius;
    public float endRadius;
    public Color color;
    public float seconds;
    public float startTime;
    public float endTime;
    public float holdTime;
    public Entity e;

    public GfxMovingCircle(float x, float y, Color color, float startRadius, float endRadius, float seconds, float hold) {
        super(x, y);
        this.color = color;
        this.startRadius = startRadius;
        this.endRadius = endRadius;
        this.seconds = seconds;
        startTime = HeroGame.getSeconds();
        endTime = HeroGame.getSeconds() + seconds;
        holdTime = endTime + hold;
    }

    public GfxMovingCircle(Entity e, Color color, float startRadius, float endRadius, float seconds, float hold) {
        super(GameLoop.glyphEngine.getTilePixelX(e.pos.x, e.pos.y), Graphics.height - GameLoop.glyphEngine.getTilePixelY(e.pos.x, e.pos.y));
        this.e = e;
        this.color = color;
        this.startRadius = startRadius;
        this.endRadius = endRadius;
        this.seconds = seconds;
        startTime = HeroGame.getSeconds();
        endTime = HeroGame.getSeconds() + seconds;
        holdTime = endTime + hold;
    }

    public float currentRadius() {
        if (HeroGame.getSeconds() > endTime) {
            return endRadius;
        }
        float proportion = (HeroGame.getSeconds() - startTime) / (endTime - startTime);
        return startRadius + ((endRadius - startRadius) * proportion);
    }

    public void update() {
        x = GameLoop.glyphEngine.getTilePixelX(e.pos.x, e.pos.y);
        y = Graphics.height - GameLoop.glyphEngine.getTilePixelY(e.pos.x, e.pos.y);
        if (HeroGame.getSeconds() > holdTime) {
            active = false;
        }
    }
}
