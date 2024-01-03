package com.bigsagebeast.hero.module;

import com.badlogic.gdx.graphics.Color;
import com.bigsagebeast.hero.GameLoop;
import com.bigsagebeast.hero.GameState;
import com.bigsagebeast.hero.Graphics;
import com.bigsagebeast.hero.GraphicsState;
import com.bigsagebeast.hero.dialogue.DialogueBox;
import com.bigsagebeast.hero.gfx.GfxRectBorder;
import com.bigsagebeast.hero.gfx.GfxRectFilled;
import com.bigsagebeast.hero.roguelike.game.Game;

import java.util.function.Consumer;

public class DialogueBoxModule extends Module {
    private float endTime;
    private GfxRectFilled background1;
    private GfxRectBorder background2;
    private float margin = 10f;

    private DialogueBox dialogueBox;
    Consumer<Object> handler;


    public void openDialogueBox(DialogueBox dialogueBox, Consumer<Object> handler) {

        background1 = new GfxRectFilled(Color.BLACK,
                dialogueBox.getX() - margin,
                dialogueBox.getY() - margin,
                dialogueBox.getWidth() + 2f*margin,
                dialogueBox.getHeight() + 2f*margin);
        effectEngine.addGfx(background1);
        background2 = new GfxRectBorder(Color.WHITE,
                dialogueBox.getX() - margin,
                dialogueBox.getY() - margin,
                dialogueBox.getWidth() + 2f*margin,
                dialogueBox.getHeight() + 2f*margin);
        effectEngine.addGfx(background2);

        this.dialogueBox = dialogueBox;
        this.handler = handler;

        Game.interrupt();
        this.start();
    }

    @Override
    public void update(GameState state) {
        if (dialogueBox.isClosed()) {
            background1.active = false;
            background2.active = false;
            this.end();
            GameLoop.roguelikeModule.setDirty();
            Object finalValue = dialogueBox.getFinalValue();
            dialogueBox = null;
            handler.accept(finalValue);
        } else {
            dialogueBox.update(textEngine);
        }
    }

    @Override
    public void render(Graphics g, GraphicsState gState) {

    }

    @Override
    public boolean keyDown(int keycode, boolean shift, boolean ctrl, boolean alt) {
        if (dialogueBox != null) {
            return dialogueBox.keyDown(keycode, shift, ctrl, alt);
        }
        return false;
    }

}
