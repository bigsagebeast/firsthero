package com.churchofcoyote.hero.dialogue;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.churchofcoyote.hero.Graphics;
import com.churchofcoyote.hero.glyphtile.GlyphTile;
import com.churchofcoyote.hero.logic.TextEngine;
import com.churchofcoyote.hero.text.TextBlock;

import java.util.ArrayList;
import java.util.List;

public class ChatBox {

    public static final int FONT_SIZE = 32;
    public static final int FOOTER_OFFSET_FROM_LEFT = 0;
    public static final int FOOTER_OFFSET_FROM_BOTTOM = 32;
    public static final int TITLE_OFFSET_FROM_TOP = 0;
    public static final int ITEM_OFFSET_FROM_LEFT = 0;
    public static final int ITEM_OFFSET_FROM_TOP = TITLE_OFFSET_FROM_TOP + FONT_SIZE;

    private int x;
    private int y;
    private int width;
    private int height;
    private boolean closed = false;
    private boolean compiled = false;
    private GlyphTile titleGlyph;
    private String titleText = "Story";
    private String footerText = "";
    private int lineOffset = 0;
    private int selection = -1;

    private TextBlock title;
    private TextBlock footer;
    private TextBlock textParent;
    private TextBlock linkParent;
    private String text;
    private List<StoryBoxLink> links = new ArrayList<>();

    public ChatBox() {
    }

    public void update(TextEngine textEngine) {
        if (compiled) {
            return;
        }
        compile(textEngine);
    }

    public ChatBox withDimensions(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        return this;
    }

    public ChatBox withMargins(int marginX, int marginY) {
        this.x = marginX;
        this.y = marginY;
        this.width = Graphics.width - (marginX * 2);
        this.height = Graphics.height - (marginY * 2);
        return this;
    }

    // Remember to put a ` where you want the glyph if you supply one
    public ChatBox withTitle(String titleText, GlyphTile glyph) {
        this.titleText = titleText;
        titleGlyph = glyph;
        return this;
    }

    public ChatBox withText(String text) {
        this.text = text;
        return this;
    }

    public void addLink(String text) {
        StoryBoxLink link = new StoryBoxLink();
        link.text = text;
        links.add(link);
    }

    public void compile(TextEngine textEngine) {
        textParent = new TextBlock("", null, FONT_SIZE, 0, 0,
                x + ITEM_OFFSET_FROM_LEFT, y + ITEM_OFFSET_FROM_TOP,
                Color.WHITE);
        linkParent = new TextBlock("", null, FONT_SIZE, 0, 0,
                x + ITEM_OFFSET_FROM_LEFT, y + ITEM_OFFSET_FROM_TOP,
                Color.WHITE);
        title =  new TextBlock(titleText, null, FONT_SIZE, 0, 0,
                x + width/2 - (titleText.length() * FONT_SIZE)/2, y + TITLE_OFFSET_FROM_TOP,
                Color.YELLOW, new GlyphTile[] {titleGlyph});
        footer = new TextBlock(footerText, null, FONT_SIZE, 0, 0,
                x + FOOTER_OFFSET_FROM_LEFT, y + height - FOOTER_OFFSET_FROM_BOTTOM,
                Color.YELLOW);

        textEngine.addBlock(textParent);
        textEngine.addBlock(linkParent);
        textEngine.addBlock(footer);
        textEngine.addBlock(title);

        int textLine = 0;
        String textToDisplay = text;
        int lineWidth = 40;
        while (textToDisplay.length() > 0) {
            TextBlock textChild;
            if (textToDisplay.length() <= lineWidth) {
                textChild = new TextBlock(textToDisplay, null, FONT_SIZE, 2, textLine,
                        0, 0, Color.WHITE);
                textToDisplay = "";
            } else {
                int lastSpace = textToDisplay.lastIndexOf(' ', lineWidth);
                String textThisBlock = textToDisplay.substring(0, lastSpace);
                textToDisplay = textToDisplay.substring(lastSpace + 1);
                textChild = new TextBlock(textThisBlock, null, FONT_SIZE, 2, textLine,
                        0, 0, Color.WHITE);
                textLine++;
            }
            textParent.addChild(textChild);
        }

        textLine += 2;

        for (int i = 0; i < links.size(); i++) {
            StoryBoxLink link = links.get(i);
            link.textBlock = new TextBlock("  " + link.text, null, FONT_SIZE, 0, textLine,
                    0, 0, Color.WHITE);
            linkParent.addChild(link.textBlock);
            textLine++;
        }

        height = (textLine * FONT_SIZE) + FOOTER_OFFSET_FROM_BOTTOM;

        selectNext();

        compiled = true;
    }

    private void selectNext() {
        int nextSelection = -1;
        for (int i = selection+1; i < links.size(); i++) {
                nextSelection = i;
                break;
        }
        if (nextSelection == -1) {
            for (int i = 0; i < selection; i++) {
                    nextSelection = i;
                    break;
            }
        }
        if (nextSelection > -1) {
            if (selection >= 0) {
                links.get(selection).textBlock.text = " " + links.get(selection).textBlock.text.substring(1);
                links.get(selection).textBlock.color = Color.WHITE;
            }
            selection = nextSelection;
            links.get(nextSelection).textBlock.text = ">" + links.get(nextSelection).textBlock.text.substring(1);
            links.get(nextSelection).textBlock.color = Color.YELLOW;
        }
    }

    private void selectPrevious() {
        int nextSelection = -1;
        for (int i = selection-1; i >= 0; i--) {
                nextSelection = i;
                break;
        }
        if (nextSelection == -1) {
            for (int i = links.size()-1; i > selection; i--) {
                    nextSelection = i;
                    break;
            }
        }
        if (nextSelection > -1) {
            if (selection >= 0) {
                links.get(selection).textBlock.text = " " + links.get(selection).textBlock.text.substring(1);
                links.get(selection).textBlock.color = Color.WHITE;
            }
            selection = nextSelection;
            links.get(nextSelection).textBlock.text = ">" + links.get(nextSelection).textBlock.text.substring(1);
            links.get(nextSelection).textBlock.color = Color.YELLOW;
        }

    }

    public void close() {
        title.close();
        footer.close();
        textParent.close();
        linkParent.close();
        closed = true;
    }

    public boolean isClosed() {
        return closed;
    }

    public int getSelection() {
        return selection;
    }

    public boolean keyDown(int keycode, boolean shift, boolean ctrl, boolean alt) {
        if (keycode == Input.Keys.SPACE) {
            close();
        } else if (keycode == Input.Keys.UP || keycode == Input.Keys.NUMPAD_8) {
            selectPrevious();
        } else if (keycode == Input.Keys.DOWN || keycode == Input.Keys.NUMPAD_2) {
            selectNext();
        } else if (keycode == Input.Keys.ENTER || keycode == Input.Keys.NUMPAD_ENTER) {
            close();
        }

        return true;
    }

    public int getX() { return x; }
    public int getY() { return y; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }
}
