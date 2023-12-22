package com.churchofcoyote.hero.module;

import com.badlogic.gdx.graphics.Color;
import com.churchofcoyote.hero.GameLoop;
import com.churchofcoyote.hero.GameState;
import com.churchofcoyote.hero.Graphics;
import com.churchofcoyote.hero.GraphicsState;
import com.churchofcoyote.hero.dialogue.StoryBox;
import com.churchofcoyote.hero.gfx.GfxRectBorder;
import com.churchofcoyote.hero.gfx.GfxRectFilled;
import com.churchofcoyote.hero.glyphtile.GlyphTile;
import com.churchofcoyote.hero.chat.ChatBook;
import com.churchofcoyote.hero.chat.ChatLink;
import com.churchofcoyote.hero.chat.ChatPage;

import java.util.ArrayList;
import java.util.function.Consumer;

public class ChatModule extends Module {
    private float endTime;
    private GfxRectFilled background1;
    private GfxRectBorder background2;
    private float margin = 10f;

    private StoryBox storyBox;
    Consumer<Object> handler;

    private String titleText;
    private GlyphTile titleGlyph;
    ArrayList<ChatLink> validLinks = new ArrayList<>();

    public void openStory(String key, String titleText, GlyphTile titleGlyph) {
        this.titleText = titleText;
        this.titleGlyph = titleGlyph;
        openPage(key);
        this.start();
    }

    private void openPage(String key) {
        ChatPage page = ChatBook.get(key);

        if (page == null) {
            // TODO debug message?
            System.out.println("No page with key " + key);
            terminate();
            return;
        }
        validLinks.clear();
        for (ChatLink link : page.links) {
            // filter out links that don't meet the conditions
            // 'auto' pages are usually the landing pages, and redirect to real pages based on story flags
            if (page.auto) {
                openPage(link.nextPage);
                return;
            } else {
                validLinks.add(link);
            }
        }
        if (validLinks.isEmpty()) {
            System.out.println("No valid links on page " + key);
            terminate();
            return;
        }

        storyBox = new StoryBox()
                .withMargins(60, 60)
                .withTitle(titleText, titleGlyph)
                .withText(page.text);

        for (ChatLink link : validLinks) {
            storyBox.addLink(link.text);
        }

        if (background1 != null) {
            background1.active = false;
            background2.active = false;
        }
        storyBox.update(textEngine);

        background1 = new GfxRectFilled(Color.BLACK,
                this.storyBox.getX() - margin,
                this.storyBox.getY() - margin,
                this.storyBox.getWidth() + 2f*margin,
                this.storyBox.getHeight() + 2f*margin);
        effectEngine.addGfx(background1);
        background2 = new GfxRectBorder(Color.WHITE,
                this.storyBox.getX() - margin,
                this.storyBox.getY() - margin,
                this.storyBox.getWidth() + 2f*margin,
                this.storyBox.getHeight() + 2f*margin);
        effectEngine.addGfx(background2);
    }

    private void terminate() {
        if (background1 != null) {
            background1.active = false;
            background2.active = false;
        }
        this.end();
        GameLoop.roguelikeModule.setDirty();
    }

    @Override
    public void update(GameState state) {
        if (storyBox.isClosed()) {

            ChatLink link = validLinks.get(storyBox.getSelection());
            if (link.terminal) {
                terminate();
                storyBox = null;
            } else {
                openPage(link.nextPage);
            }
        } else {
            storyBox.update(textEngine);
        }
    }

    @Override
    public void render(Graphics g, GraphicsState gState) {

    }

    @Override
    public boolean keyDown(int keycode, boolean shift, boolean ctrl, boolean alt) {
        if (storyBox != null) {
            return storyBox.keyDown(keycode, shift, ctrl, alt);
        }
        return false;
    }

}
