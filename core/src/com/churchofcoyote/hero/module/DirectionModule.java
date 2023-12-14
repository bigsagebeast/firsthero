package com.churchofcoyote.hero.module;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.churchofcoyote.hero.GameLoop;
import com.churchofcoyote.hero.GameState;
import com.churchofcoyote.hero.Graphics;
import com.churchofcoyote.hero.GraphicsState;
import com.churchofcoyote.hero.engine.WindowEngine;
import com.churchofcoyote.hero.roguelike.game.Game;
import com.churchofcoyote.hero.roguelike.world.Entity;
import com.churchofcoyote.hero.text.TextBlock;
import com.churchofcoyote.hero.ui.UIManager;
import com.churchofcoyote.hero.util.Compass;
import com.churchofcoyote.hero.util.Fov;
import com.churchofcoyote.hero.util.Point;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class DirectionModule extends Module {

    private Consumer<Compass> consumer;

    public DirectionModule() {
    }

    public void begin(Consumer<Compass> consumer) {
        begin("Select a direction, or space to cancel.", consumer);
    }

    public void begin(String message, Consumer<Compass> consumer) {
        Game.announce(message);
        this.consumer = consumer;
        super.start();
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
            case Input.Keys.LEFT:
            case Input.Keys.NUMPAD_4:
                end();
                Game.unannounce();
                consumer.accept(Compass.WEST);
                break;
            case Input.Keys.RIGHT:
            case Input.Keys.NUMPAD_6:
                end();
                Game.unannounce();
                consumer.accept(Compass.EAST);
                break;
            case Input.Keys.UP:
            case Input.Keys.NUMPAD_8:
                end();
                Game.unannounce();
                consumer.accept(Compass.NORTH);
                break;
            case Input.Keys.DOWN:
            case Input.Keys.NUMPAD_2:
                end();
                Game.unannounce();
                consumer.accept(Compass.SOUTH);
                break;
            case Input.Keys.HOME:
            case Input.Keys.NUMPAD_7:
                end();
                Game.unannounce();
                consumer.accept(Compass.NORTH_WEST);
                break;
            case Input.Keys.END:
            case Input.Keys.NUMPAD_1:
                end();
                Game.unannounce();
                consumer.accept(Compass.SOUTH_WEST);
                break;
            case Input.Keys.PAGE_UP:
            case Input.Keys.NUMPAD_9:
                end();
                Game.unannounce();
                consumer.accept(Compass.NORTH_EAST);
                break;
            case Input.Keys.PAGE_DOWN:
            case Input.Keys.NUMPAD_3:
                end();
                Game.unannounce();
                consumer.accept(Compass.SOUTH_EAST);
                break;
            case Input.Keys.SPACE:
            case Input.Keys.NUMPAD_5:
                end();
                Game.unannounce();
                consumer.accept(Compass.OTHER);
                break;
        }

        return true;
    }
}
