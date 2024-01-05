package com.bigsagebeast.hero.logic;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.bigsagebeast.hero.GameLogic;
import com.bigsagebeast.hero.GameState;
import com.bigsagebeast.hero.Graphics;
import com.bigsagebeast.hero.GraphicsState;
import com.bigsagebeast.hero.gfx.Gfx;
import com.bigsagebeast.hero.gfx.GfxMovingCircle;
import com.bigsagebeast.hero.gfx.GfxRectBorder;
import com.bigsagebeast.hero.gfx.GfxRectFilled;

import java.util.ArrayList;
import java.util.List;

public class EffectEngine implements GameLogic {

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

        g.startShapeBatch(ShapeRenderer.ShapeType.Filled);
        for (Gfx gfx : gfxList) {
            if (gfx instanceof GfxRectFilled) {
                GfxRectFilled gfxRectFilled = (GfxRectFilled)gfx;
                g.shapeBatch().setColor(gfxRectFilled.color);
                g.shapeBatch().rect(gfxRectFilled.x, Graphics.height - gfxRectFilled.y - gfxRectFilled.height, gfxRectFilled.width, gfxRectFilled.height);
            }
        }
        g.endShapeBatch();

        g.startShapeBatch(ShapeRenderer.ShapeType.Line);
        for (Gfx gfx : gfxList) {
            if (gfx instanceof GfxRectBorder) {
                GfxRectBorder gfxRectBorder = (GfxRectBorder)gfx;
                g.shapeBatch().setColor(gfxRectBorder.color);
                g.shapeBatch().rect(gfxRectBorder.x, Graphics.height - gfxRectBorder.y - gfxRectBorder.height, gfxRectBorder.width, gfxRectBorder.height);
            }
        }
        for (Gfx gfx : gfxList) {
            if (gfx instanceof GfxMovingCircle) {
                GfxMovingCircle gfxMovingCircle = (GfxMovingCircle)gfx;
                g.shapeBatch().setColor(gfxMovingCircle.color);
                g.shapeBatch().circle(gfxMovingCircle.x, gfxMovingCircle.y, gfxMovingCircle.currentRadius());
            }
        }
        g.endShapeBatch();
    }
}
