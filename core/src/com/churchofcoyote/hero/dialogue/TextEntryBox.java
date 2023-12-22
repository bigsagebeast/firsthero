package com.churchofcoyote.hero.dialogue;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.churchofcoyote.hero.Graphics;
import com.churchofcoyote.hero.logic.TextEngine;
import com.churchofcoyote.hero.text.TextBlock;

import java.util.Locale;

public class TextEntryBox {

    public static final int FONT_SIZE = 32;
    public static final int FOOTER_OFFSET_FROM_LEFT = 0;
    public static final int FOOTER_OFFSET_FROM_BOTTOM = 16;
    public static final int TITLE_OFFSET_FROM_TOP = 0;
    public static final int FIRST_ROW_FROM_TOP = FONT_SIZE + TITLE_OFFSET_FROM_TOP;
    public static final int BOTTOM_POSSIBLE_ROW = FONT_SIZE + FOOTER_OFFSET_FROM_BOTTOM;
    public static final int ITEM_OFFSET_FROM_LEFT = 0;
    public static final int ITEM_OFFSET_FROM_TOP = TITLE_OFFSET_FROM_TOP + FONT_SIZE;

    private int x;
    private int y;
    private int width;
    private int height;
    private boolean closed = false;
    private boolean compiled = false;
    private String titleText = "";
    private String footerText = "";
    private int lineOffset = 0;
    private int selection = -1;
    private String textEntry = "";

    private TextBlock title;
    private TextBlock footer;
    private TextBlock textEntryBlock;


    public TextEntryBox() {
    }

    public void update(TextEngine textEngine) {
        if (compiled) {
            return;
        }
        compile(textEngine);
    }

    public TextEntryBox withDimensions(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        return this;
    }

    public TextEntryBox withMargins(int marginX, int marginY) {
        this.x = marginX;
        this.y = marginY;
        this.width = Graphics.width - (marginX * 2);
        this.height = Graphics.height - (marginY * 2);
        return this;
    }

    public TextEntryBox withTitle(String title) {
        titleText = title;
        return this;
    }

    public void autoHeight() {
        int textLine = 1;
        height = (textLine * FONT_SIZE) + FOOTER_OFFSET_FROM_BOTTOM + 16; // TODO magic number
    }

    public void compile(TextEngine textEngine) {
        textEntryBlock = new TextBlock("", null, FONT_SIZE, 0, 0,
                x + ITEM_OFFSET_FROM_LEFT, y + ITEM_OFFSET_FROM_TOP,
                Color.WHITE);
        title =  new TextBlock(titleText, null, FONT_SIZE, 0, 0,
                x + width/2 - (titleText.length() * FONT_SIZE)/2, y + TITLE_OFFSET_FROM_TOP,
                Color.YELLOW);
        footer = new TextBlock(footerText, null, FONT_SIZE, 0, 0,
                x + FOOTER_OFFSET_FROM_LEFT, y + height - FOOTER_OFFSET_FROM_BOTTOM,
                Color.YELLOW);

        textEngine.addBlock(textEntryBlock);
        textEngine.addBlock(footer);
        textEngine.addBlock(title);

        compiled = true;
    }


    public void close() {
        if (title != null) {
            title.close();
        }
        if (footer != null) {
            footer.close();
        }
        if (textEntryBlock != null) {
            textEntryBlock.close();
        }
        closed = true;
    }

    public boolean isClosed() {
        return closed;
    }

    public String getFinalValue() {
        return textEntry;
    }

    public boolean keyDown(int keycode, boolean shift, boolean ctrl, boolean alt) {
        String equivalent = null;
        if (keycode >= Input.Keys.A && keycode <= Input.Keys.Z) {
            equivalent = String.valueOf((char)(keycode - Input.Keys.A + 'a'));
        } else if (keycode >= Input.Keys.NUM_0 && keycode <= Input.Keys.NUM_9) {
            equivalent = String.valueOf((char)(keycode - Input.Keys.A + '0'));
        } else if (keycode == Input.Keys.SPACE) {
            equivalent = " ";
        } else if (keycode == Input.Keys.MINUS) {
            equivalent = "-";
        } else if (keycode == Input.Keys.PERIOD) {
            equivalent = ".";
        }

        if (shift && equivalent != null) {
            equivalent = equivalent.toUpperCase(Locale.ROOT);
        }

        if (equivalent != null) {
            textEntry = textEntry + equivalent;
        } else if (keycode == Input.Keys.BACKSPACE) {
            if (textEntry.length() > 0) {
                textEntry = textEntry.substring(0, textEntry.length() - 1);
            }
        } else if (keycode == Input.Keys.ENTER) {

            close();
        }
        textEntryBlock.pixelOffsetX = (width/2) - (textEntry.length() * FONT_SIZE / 2);
        textEntryBlock.text = textEntry + "|";
        return true;
    }

    public int getX() { return x; }
    public int getY() { return y; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }
}
