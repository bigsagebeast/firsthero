package com.bigsagebeast.hero.module;

import com.badlogic.gdx.Input;
import com.bigsagebeast.hero.GameState;
import com.bigsagebeast.hero.Graphics;
import com.bigsagebeast.hero.GraphicsState;
import com.bigsagebeast.hero.roguelike.game.Game;
import com.bigsagebeast.hero.util.Compass;

import java.util.function.Consumer;

public class PauseModule extends Module {

    // Do nothing except prompt for a keypress
    Runnable runnable;
    public PauseModule() {
    }

    public void begin(Runnable runnable) {
        super.start();
        this.runnable = runnable;
    }

    @Override
    public void update(GameState state) {
    }


    @Override
    public void render(Graphics g, GraphicsState gState) {
    }

    @Override
    public boolean keyDown(int keycode, boolean shift, boolean ctrl, boolean alt) {
        switch (keycode) {
            case Input.Keys.ENTER:
                end();
                Game.unannounce();
                if (runnable != null) {
                    runnable.run();
                }
                break;
        }

        return true;
    }
}
