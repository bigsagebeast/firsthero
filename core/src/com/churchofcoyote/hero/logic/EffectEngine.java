package com.churchofcoyote.hero.logic;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.churchofcoyote.hero.GameLogic;
import com.churchofcoyote.hero.GameState;
import com.churchofcoyote.hero.Graphics;
import com.churchofcoyote.hero.GraphicsState;
import com.churchofcoyote.hero.gfx.Gfx;
import com.churchofcoyote.hero.gfx.GfxMovingCircle;
import com.churchofcoyote.hero.gfx.GfxRectBorder;
import com.churchofcoyote.hero.gfx.GfxRectFilled;

import java.util.ArrayList;
import java.util.List;

public class EffectEngine implements GameLogic {

    ShapeRenderer shapeBatch = new ShapeRenderer();
    public List<Gfx> gfxList = new ArrayList<>();

    public void addGfx(Gfx gfx) {
        gfxList.add(gfx);
    }

    @Override
    public void update(GameState state) {
        List<Gfx> garbage = new ArrayList<>();
        for (Gfx gfx : gfxList) {
            gfx.update();
            if (!gfx.active) {
                garbage.add(gfx);
            }
        }
        for (Gfx gfx : garbage) {
            gfxList.remove(gfx);
        }
    }

    @Override
    public void render(Graphics g, GraphicsState gState) {

        shapeBatch.begin(ShapeRenderer.ShapeType.Filled);
        for (Gfx gfx : gfxList) {
            if (gfx instanceof GfxRectFilled) {
                GfxRectFilled gfxRectFilled = (GfxRectFilled)gfx;
                shapeBatch.setColor(gfxRectFilled.color);
                shapeBatch.rect(gfxRectFilled.x, Graphics.height - gfxRectFilled.y - gfxRectFilled.height, gfxRectFilled.width, gfxRectFilled.height);
            }
        }
        shapeBatch.end();

        shapeBatch.begin(ShapeRenderer.ShapeType.Line);
        for (Gfx gfx : gfxList) {
            if (gfx instanceof GfxRectBorder) {
                GfxRectBorder gfxRectBorder = (GfxRectBorder)gfx;
                shapeBatch.setColor(gfxRectBorder.color);
                shapeBatch.rect(gfxRectBorder.x, Graphics.height - gfxRectBorder.y - gfxRectBorder.height, gfxRectBorder.width, gfxRectBorder.height);
            }
        }
        for (Gfx gfx : gfxList) {
            if (gfx instanceof GfxMovingCircle) {
                GfxMovingCircle gfxMovingCircle = (GfxMovingCircle)gfx;
                shapeBatch.setColor(gfxMovingCircle.color);
                shapeBatch.circle(gfxMovingCircle.x, gfxMovingCircle.y, gfxMovingCircle.currentRadius());
            }
        }
        shapeBatch.end();
    }
}
