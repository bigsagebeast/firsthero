package com.bigsagebeast.hero.module;

import com.badlogic.gdx.graphics.Color;
import com.bigsagebeast.hero.GameLoop;
import com.bigsagebeast.hero.GameState;
import com.bigsagebeast.hero.Graphics;
import com.bigsagebeast.hero.GraphicsState;
import com.bigsagebeast.hero.dialogue.TextEntryBox;
import com.bigsagebeast.hero.gfx.GfxRectBorder;
import com.bigsagebeast.hero.gfx.GfxRectFilled;

import java.util.function.Consumer;

public class TextEntryModule extends Module {
    private float endTime;
    private GfxRectFilled background1;
    private GfxRectBorder background2;
    private float margin = 10f;

    private TextEntryBox textEntryBox;
    Consumer<String> handler;


    public void openTextEntryBox(TextEntryBox textEntryBox, Consumer<String> handler) {

        background1 = new GfxRectFilled(Color.BLACK,
                textEntryBox.getX() - margin,
                textEntryBox.getY() - margin,
                textEntryBox.getWidth() + 2f*margin,
                textEntryBox.getHeight() + 2f*margin);
        effectEngine.addGfx(background1);
        background2 = new GfxRectBorder(Color.WHITE,
                textEntryBox.getX() - margin,
                textEntryBox.getY() - margin,
                textEntryBox.getWidth() + 2f*margin,
                textEntryBox.getHeight() + 2f*margin);
        effectEngine.addGfx(background2);

        this.textEntryBox = textEntryBox;
        this.handler = handler;

        this.start();
    }

    @Override
    public void update(GameState state) {
        if (textEntryBox.isClosed()) {
            background1.active = false;
            background2.active = false;
            this.end();
            GameLoop.roguelikeModule.setDirty();
            String finalValue = textEntryBox.getFinalValue();
            textEntryBox = null;
            handler.accept(finalValue);
        } else {
            textEntryBox.update(textEngine);
        }
    }

    @Override
    public void render(Graphics g, GraphicsState gState) {

    }

    @Override
    public boolean keyDown(int keycode, boolean shift, boolean ctrl, boolean alt) {
        if (textEntryBox != null) {
            return textEntryBox.keyDown(keycode, shift, ctrl, alt);
        }
        return false;
    }

}
