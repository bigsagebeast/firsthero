package com.bigsagebeast.hero.module;

import com.badlogic.gdx.graphics.Color;
import com.bigsagebeast.hero.GameLoop;
import com.bigsagebeast.hero.GameState;
import com.bigsagebeast.hero.Graphics;
import com.bigsagebeast.hero.GraphicsState;
import com.bigsagebeast.hero.chat.ChatLink;
import com.bigsagebeast.hero.dialogue.DescriptionBox;
import com.bigsagebeast.hero.gfx.GfxRectBorder;
import com.bigsagebeast.hero.gfx.GfxRectFilled;
import com.bigsagebeast.hero.glyphtile.EntityGlyph;
import com.bigsagebeast.hero.glyphtile.GlyphTile;
import com.bigsagebeast.hero.roguelike.world.Bestiary;
import com.bigsagebeast.hero.roguelike.world.Entity;
import com.bigsagebeast.hero.roguelike.world.Itempedia;
import com.bigsagebeast.hero.util.Util;

import java.util.ArrayList;

public class DescriptionModule extends Module {
    private GfxRectFilled background1;
    private GfxRectBorder background2;
    private float margin = 10f;

    private DescriptionBox descriptionBox;

    private String titleText;
    private GlyphTile titleGlyph;

    public void lookAtEntity(Entity entity) {
        this.titleText = "` " + Util.capitalize(entity.getVisibleName());
        this.titleGlyph = EntityGlyph.getGlyph(entity);
        String text;
        if (entity.getMover() != null) {
            text = Bestiary.get(entity.phenotypeName).description;
        } else {
            text = entity.getItemType().description;
        }
        if (text == null) {
            text = entity.getVisibleName() + " - WARN: no description";
        }
        descriptionBox = new DescriptionBox()
                .withDimensions(100, 100, 1000, 50)
                .withText(text)
                .withTitle(titleText, titleGlyph);
        descriptionBox.compile(textEngine);

        if (background1 != null) {
            background1.active = false;
            background2.active = false;
        }

        background1 = new GfxRectFilled(Color.BLACK,
                this.descriptionBox.getX() - margin,
                this.descriptionBox.getY() - margin,
                this.descriptionBox.getWidth() + 2f*margin,
                this.descriptionBox.getHeight() + 2f*margin);
        effectEngine.addGfx(background1);
        background2 = new GfxRectBorder(Color.WHITE,
                this.descriptionBox.getX() - margin,
                this.descriptionBox.getY() - margin,
                this.descriptionBox.getWidth() + 2f*margin,
                this.descriptionBox.getHeight() + 2f*margin);
        effectEngine.addGfx(background2);
        this.start();
    }

    public void terminate() {
        if (background1 != null) {
            background1.active = false;
            background2.active = false;
        }
        if (descriptionBox != null) {
            descriptionBox.close();
        }
        this.end();
        GameLoop.roguelikeModule.setDirty();
    }

    @Override
    public void update(GameState state) {
        descriptionBox.update(textEngine);
    }

    @Override
    public void render(Graphics g, GraphicsState gState) {

    }

    @Override
    public boolean keyDown(int keycode, boolean shift, boolean ctrl, boolean alt) {
        return false;
    }

}
