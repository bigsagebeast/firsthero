package com.churchofcoyote.hero.module;

import com.badlogic.gdx.graphics.Color;
import com.churchofcoyote.hero.GameLoop;
import com.churchofcoyote.hero.GameState;
import com.churchofcoyote.hero.Graphics;
import com.churchofcoyote.hero.GraphicsState;
import com.churchofcoyote.hero.dialogue.DialogueBox;
import com.churchofcoyote.hero.gfx.GfxRectBorder;
import com.churchofcoyote.hero.gfx.GfxRectFilled;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
