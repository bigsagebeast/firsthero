package com.bigsagebeast.hero.module;

import com.badlogic.gdx.graphics.Color;
import com.bigsagebeast.hero.GameLoop;
import com.bigsagebeast.hero.GameState;
import com.bigsagebeast.hero.Graphics;
import com.bigsagebeast.hero.GraphicsState;
import com.bigsagebeast.hero.chat.*;
import com.bigsagebeast.hero.dialogue.ChatBox;
import com.bigsagebeast.hero.gfx.GfxRectBorder;
import com.bigsagebeast.hero.gfx.GfxRectFilled;
import com.bigsagebeast.hero.glyphtile.EntityGlyph;
import com.bigsagebeast.hero.glyphtile.GlyphTile;
import com.bigsagebeast.hero.roguelike.game.Game;
import com.bigsagebeast.hero.roguelike.world.Bestiary;
import com.bigsagebeast.hero.roguelike.world.Entity;
import com.bigsagebeast.hero.util.Util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class ChatModule extends Module {
    private float endTime;
    private GfxRectFilled background1;
    private GfxRectBorder background2;
    private float margin = 10f;

    private ChatBox chatBox;
    Consumer<Object> handler;

    private String titleText;
    private GlyphTile titleGlyph;
    ArrayList<ChatLink> validLinks = new ArrayList<>();

    public void openStory(Entity entity) {
        String page = Bestiary.get(entity.phenotypeName).chatPage;
        String name;
        if (entity.getPhenotype().chatName != null) {
            name = entity.getPhenotype().chatName;
        } else {
            name = Util.capitalize(entity.getVisibleName());
        }
        openStory(page, "` " + name, EntityGlyph.getGlyph(entity));
    }

    public void openStory(String key, String titleText, GlyphTile titleGlyph) {
        this.titleText = titleText;
        this.titleGlyph = titleGlyph;
        openPage(key);
        this.start();
        Game.interrupt();
    }

    public void openArbitrary(ChatBox chatBox, ArrayList<ChatLink> links) {
        this.chatBox = chatBox;
        validLinks.clear();

        for (ChatLink link : links) {
            validLinks.add(link);
            chatBox.addLink(link.text);
        }

        if (background1 != null) {
            background1.active = false;
            background2.active = false;
        }
        chatBox.update(textEngine);

        background1 = new GfxRectFilled(Color.BLACK,
                this.chatBox.getX() - margin,
                this.chatBox.getY() - margin,
                this.chatBox.getWidth() + 2f*margin,
                this.chatBox.getHeight() + 2f*margin);
        effectEngine.addGfx(background1);
        background2 = new GfxRectBorder(Color.WHITE,
                this.chatBox.getX() - margin,
                this.chatBox.getY() - margin,
                this.chatBox.getWidth() + 2f*margin,
                this.chatBox.getHeight() + 2f*margin);
        effectEngine.addGfx(background2);
        this.start();
    }

    private void openPage(String key) {
        if (key == null || key.isEmpty()) {
            terminate();
            return;
        }
        ChatPage page = ChatBook.get(key);

        if (page == null) {
            GameLoop.warn("No page with key " + key);
            terminate();
            return;
        }
        validLinks.clear();
        List<ChatLink> links = new ArrayList<>();
        links.addAll(page.links);
        if (page.inheritLinks != null) {
            links.addAll(ChatBook.get(page.inheritLinks).links);
        }
        for (ChatLink link : links) {
            // filter out links that don't meet the conditions
            // 'auto' pages are usually the landing pages, and redirect to real pages based on story flags
            if (page.auto) {
                openPage(link.nextPage);
                return;
            } else {
                boolean canShow = true;
                for (ChatComparator test : link.tests) {
                    if (!ChatState.compare(test)) {
                        canShow = false;
                    }
                }
                if (canShow) {
                    validLinks.add(link);
                }
            }
        }

        if (validLinks.isEmpty()) {
            //System.out.println("No valid links on page " + key);
            terminate();
            return;
        }

        chatBox = new ChatBox()
                .withMargins(60, 60)
                .withTitle(titleText, titleGlyph)
                .withText(page.text);

        for (ChatLink link : validLinks) {
            chatBox.addLink(link.text);
        }

        if (background1 != null) {
            background1.active = false;
            background2.active = false;
        }
        chatBox.update(textEngine);

        background1 = new GfxRectFilled(Color.BLACK,
                this.chatBox.getX() - margin,
                this.chatBox.getY() - margin,
                this.chatBox.getWidth() + 2f*margin,
                this.chatBox.getHeight() + 2f*margin);
        effectEngine.addGfx(background1);
        background2 = new GfxRectBorder(Color.WHITE,
                this.chatBox.getX() - margin,
                this.chatBox.getY() - margin,
                this.chatBox.getWidth() + 2f*margin,
                this.chatBox.getHeight() + 2f*margin);
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
        if (chatBox.isClosed()) {

            ChatLink link = validLinks.get(chatBox.getSelection());

            if (link.codeClass != null) {
                Class<?> clazz = null;
                try {
                    clazz = Class.forName(link.codeClass);
                    Method method = clazz.getMethod(link.codeMethod);
                    method.invoke(null);
                } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                    throw new RuntimeException(e);
                }
            }

            // do this last, in case it opens another chatbox
            for (ChatSetter setter : link.setters) {
                ChatState.execute(setter);
            }

            if (link.terminal) {
                terminate();
                chatBox = null;
                if (link.runnable != null) {
                    link.runnable.run();
                }
            } else {
                openPage(link.nextPage);
                if (link.runnable != null) {
                    link.runnable.run();
                }
            }
        } else {
            chatBox.update(textEngine);
        }
    }

    @Override
    public void render(Graphics g, GraphicsState gState) {

    }

    @Override
    public boolean keyDown(int keycode, boolean shift, boolean ctrl, boolean alt) {
        if (chatBox != null) {
            return chatBox.keyDown(keycode, shift, ctrl, alt);
        }
        return false;
    }

}
