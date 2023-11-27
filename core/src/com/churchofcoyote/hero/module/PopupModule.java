package com.churchofcoyote.hero.module;

import com.badlogic.gdx.graphics.Color;
import com.churchofcoyote.hero.*;
import com.churchofcoyote.hero.gfx.GfxMovingCircle;
import com.churchofcoyote.hero.gfx.GfxRectBorder;
import com.churchofcoyote.hero.gfx.GfxRectFilled;
import com.churchofcoyote.hero.roguelike.world.Entity;
import com.churchofcoyote.hero.text.TextBlock;

import java.util.ArrayList;
import java.util.List;

public class PopupModule extends Module {
    private float endTime;
    private GfxRectFilled background1;
    private GfxRectBorder background2;
    private float margin = 10f;
    private boolean displayingPopup = false;

    List<PopupOrder> orders = new ArrayList<PopupOrder>();

    public void handleOrder(PopupOrder order) {
        displayingPopup = true;
        if (order.entity != null) {
            GfxMovingCircle trackingCircle = new GfxMovingCircle(order.entity,
                    Color.CYAN, 100, 16, order.shrinkTime, order.time - order.shrinkTime);
            effectEngine.addGfx(trackingCircle);
        }

        TextBlock popupBlock = new TextBlock(order.text, null,
                36, 10, 5, Color.GRAY, 0f, 0f).fade(order.time-1, order.time);
        textEngine.addBlock(popupBlock);

        background1 = new GfxRectFilled(Color.BLACK,
                popupBlock.getPixelX() - margin,
                popupBlock.getPixelY() - margin - 3f,
                popupBlock.getPixelWidth() + 2f*margin,
                popupBlock.getPixelHeight() + 2f*margin);
        effectEngine.addGfx(background1);
        background2 = new GfxRectBorder(Color.WHITE,
                popupBlock.getPixelX() - margin,
                popupBlock.getPixelY() - margin - 3f,
                popupBlock.getPixelWidth() + 2f*margin,
                popupBlock.getPixelHeight() + 2f*margin);
        effectEngine.addGfx(background2);

        endTime = HeroGame.getSeconds() + order.time;

        this.start();

    }

    public void createPopup(String text, float time) {
        orders.add(new PopupOrder(text, time));
        this.start();
    }


    public void createPopup(String text, float time, Entity entity, float shrinkTime) {
        orders.add(new PopupOrder(text, time, entity, shrinkTime));
        this.start();
    }

    @Override
    public void update(GameState state) {
        if (displayingPopup && state.getSeconds() > endTime && this.isRunning()) {
            background1.active = false;
            background2.active = false;
            displayingPopup = false;
        }
        if (!this.displayingPopup && orders.size() > 0) {
            PopupOrder order = orders.get(0);
            orders.remove(0);
            handleOrder(order);
        }
        if (!displayingPopup) {
            this.end();
        }
    }

    @Override
    public void render(Graphics g, GraphicsState gState) {

    }

    public boolean keyDown(int keycode, boolean shift, boolean ctrl, boolean alt) {
        return true;
    }

    public class PopupOrder {
        public PopupOrder(String text, float time) {
            this.text = text;
            this.time = time;
        }
        public PopupOrder(String text, float time, Entity entity, float shrinkTime)
        {
            this.text = text;
            this.time = time;
            this.entity = entity;
            this.shrinkTime = shrinkTime;
        }
        String text;
        float time;
        Entity entity;
        float shrinkTime;
    }
}
