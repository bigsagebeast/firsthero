package com.bigsagebeast.hero.dialogue;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.utils.Align;
import com.bigsagebeast.hero.Graphics;
import com.bigsagebeast.hero.glyphtile.GlyphTile;
import com.bigsagebeast.hero.logic.TextEngine;
import com.bigsagebeast.hero.text.TextBlock;

import java.util.ArrayList;
import java.util.List;

public class DescriptionBox {

    public static final int FONT_SIZE = 32;
    public static final int FOOTER_OFFSET_FROM_BOTTOM = 28;
    public static final int TITLE_OFFSET_FROM_TOP = 8;
    public static final int ITEM_OFFSET_FROM_LEFT = 0;
    public static final int TEXT_OFFSET_FROM_TOP = TITLE_OFFSET_FROM_TOP + FONT_SIZE + 16;
    public static final int INTERNAL_MARGIN_X = 32;

    private int x;
    private int y;
    private int width;
    private int height;
    private boolean closed = false;
    private boolean compiled = false;
    private GlyphTile titleGlyph;
    private String titleText = "Story";

    private TextBlock title;
    private TextBlock footer;
    private TextBlock textParent;
    private String text;

    public DescriptionBox() {
    }

    public void update(TextEngine textEngine) {
        if (compiled) {
            return;
        }
        compile(textEngine);
    }

    public DescriptionBox withDimensions(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        return this;
    }

    public DescriptionBox withMargins(int marginX, int marginY) {
        this.x = marginX;
        this.y = marginY;
        this.width = Graphics.width - (marginX * 2);
        this.height = Graphics.height - (marginY * 2);
        return this;
    }

    // Remember to put a ` where you want the glyph if you supply one
    public DescriptionBox withTitle(String titleText, GlyphTile glyph) {
        this.titleText = titleText;
        titleGlyph = glyph;
        return this;
    }

    public DescriptionBox withText(String text) {
        this.text = text;
        return this;
    }

    public void compile(TextEngine textEngine) {
        textParent = new TextBlock("", null, FONT_SIZE, 0, 0,
                x + ITEM_OFFSET_FROM_LEFT, y + TEXT_OFFSET_FROM_TOP,
                Color.WHITE);
        title =  new TextBlock(titleText, null, FONT_SIZE, 0, 0,
                x + width/2 - (titleText.length() * FONT_SIZE)/2, y + TITLE_OFFSET_FROM_TOP,
                Color.YELLOW, new GlyphTile[] {titleGlyph});
        footer = new TextBlock("");

        textEngine.addBlock(textParent);
        textEngine.addBlock(footer);
        textEngine.addBlock(title);

        GlyphLayout layout = Graphics.createProportionalGlyphLayout(text, FONT_SIZE, width - (INTERNAL_MARGIN_X * 2), Align.topLeft, true, Color.WHITE);
        textParent.addChild(new TextBlock(layout, FONT_SIZE, null, INTERNAL_MARGIN_X, 0));

        height = (int)layout.height + FONT_SIZE + FOOTER_OFFSET_FROM_BOTTOM;

        compiled = true;
    }


    public void close() {
        title.close();
        footer.close();
        textParent.close();
        closed = true;
    }

    public boolean isClosed() {
        return closed;
    }
    public boolean keyDown(int keycode, boolean shift, boolean ctrl, boolean alt) {
        return false;
    }

    public int getX() { return x; }
    public int getY() { return y; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }
}
