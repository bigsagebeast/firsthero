package com.churchofcoyote.hero.dialogue;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.churchofcoyote.hero.Graphics;
import com.churchofcoyote.hero.glyphtile.GlyphTile;
import com.churchofcoyote.hero.logic.TextEngine;
import com.churchofcoyote.hero.text.TextBlock;

import java.util.ArrayList;
import java.util.List;

public class DialogueBox {

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
    private String titleText = "Inventory";
    private String footerText = "";
    private int lineOffset = 0;
    private int selection = -1;
    private Object finalValue = null;

    private TextBlock title;
    private TextBlock footer;
    private TextBlock lineParent;
    private List<DialogueLine> lines = new ArrayList<>();
    private List<Object> mapping = new ArrayList<>();

    public DialogueBox() {
    }

    public void update(TextEngine textEngine) {
        if (compiled) {
            return;
        }
        compile(textEngine);
    }

    public DialogueBox withDimensions(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        return this;
    }

    public DialogueBox withMargins(int marginX, int marginY) {
        this.x = marginX;
        this.y = marginY;
        this.width = Graphics.width - (marginX * 2);
        this.height = Graphics.height - (marginY * 2);
        return this;
    }

    public DialogueBox withFooterClosable() {
        footerText = "SPACE to close";
        return this;
    }

    public DialogueBox withFooterClosableAndSelectable() {
        footerText = "Arrow keys to move, ENTER to select, SPACE to close";
        return this;
    }

    public DialogueBox withTitle(String title) {
        titleText = title;
        return this;
    }

    public void addHeader(String text) {
        if (lines.size() > 0) {
            DialogueLine spacer = new DialogueLine();
            spacer.text = "";
            spacer.value = -1;
            lines.add(spacer);
        }
        DialogueLine line = new DialogueLine();
        line.text = text;
        line.value = -1;
        lines.add(line);
    }

    public void addItem(String text, Object value) {
        DialogueLine line = new DialogueLine();
        line.text = "  " + text;
        line.value = mapping.size();
        mapping.add(value);
        lines.add(line);
    }

    public void addItem(String text, GlyphTile glyph, Object value) {
        DialogueLine line = new DialogueLine();
        line.text = "  ` " + text;
        line.value = mapping.size();
        line.glyphs = new GlyphTile[] {glyph};
        mapping.add(value);
        lines.add(line);
    }

    public void autoHeight() {
        int textLine = lines.size();
        height = (textLine * FONT_SIZE) + FOOTER_OFFSET_FROM_BOTTOM + 16; // TODO magic number
    }

    public void compile(TextEngine textEngine) {
        lineParent = new TextBlock("", null, FONT_SIZE, 0, 0,
                x + ITEM_OFFSET_FROM_LEFT, y + ITEM_OFFSET_FROM_TOP,
                Color.WHITE);
        title =  new TextBlock(titleText, null, FONT_SIZE, 0, 0,
                x + width/2 - (titleText.length() * FONT_SIZE)/2, y + TITLE_OFFSET_FROM_TOP,
                Color.YELLOW);
        footer = new TextBlock(footerText, null, FONT_SIZE, 0, 0,
                x + FOOTER_OFFSET_FROM_LEFT, y + height - FOOTER_OFFSET_FROM_BOTTOM,
                Color.YELLOW);

        textEngine.addBlock(lineParent);
        textEngine.addBlock(footer);
        textEngine.addBlock(title);

        for (int i=0; i<lines.size(); i++) {
            DialogueLine line = lines.get(i);
            line.textBlock = new TextBlock(line.text, null, FONT_SIZE, 0, i,
                    0, 0, Color.WHITE, line.glyphs);
            lineParent.addChild(line.textBlock);
        }

        selectNext();

        compiled = true;
    }

    private void selectNext() {
        int nextSelection = -1;
        for (int i = selection+1; i < lines.size(); i++) {
            if (i >= 0 && lines.get(i).value > -1) {
                nextSelection = i;
                break;
            }
        }
        if (nextSelection == -1) {
            for (int i = 0; i < selection; i++) {
                if (lines.get(i).value > -1) {
                    nextSelection = i;
                    break;
                }
            }
        }
        if (nextSelection > -1) {
            if (selection >= 0) {
                lines.get(selection).textBlock.text = " " + lines.get(selection).textBlock.text.substring(1);
            }
            selection = nextSelection;
            lines.get(nextSelection).textBlock.text = ">" + lines.get(nextSelection).textBlock.text.substring(1);
        }
    }

    private void selectPrevious() {
        int nextSelection = -1;
        for (int i = selection-1; i >= 0; i--) {
            if (i >= 0 && lines.get(i).value > -1) {
                nextSelection = i;
                break;
            }
        }
        if (nextSelection == -1) {
            for (int i = lines.size()-1; i > selection; i--) {
                if (lines.get(i).value > -1) {
                    nextSelection = i;
                    break;
                }
            }
        }
        if (nextSelection > -1) {
            if (selection >= 0) {
                lines.get(selection).textBlock.text = " " + lines.get(selection).textBlock.text.substring(1);
            }
            selection = nextSelection;
            lines.get(nextSelection).textBlock.text = ">" + lines.get(nextSelection).textBlock.text.substring(1);
        }

    }

    public void close() {
        if (title != null) {
            title.close();
        }
        if (footer != null) {
            footer.close();
        }
        if (lineParent != null) {
            lineParent.close();
        }
        closed = true;
    }

    public boolean isClosed() {
        return closed;
    }

    public Object getFinalValue() {
        return finalValue;
    }

    public boolean keyDown(int keycode, boolean shift, boolean ctrl, boolean alt) {
        if (keycode == Input.Keys.SPACE) {
            close();
        } else if (keycode == Input.Keys.UP || keycode == Input.Keys.NUMPAD_8) {
            selectPrevious();
        } else if (keycode == Input.Keys.DOWN || keycode == Input.Keys.NUMPAD_2) {
            selectNext();
        } else if (keycode == Input.Keys.ENTER) {
            if (selection >= 0) {
                finalValue = mapping.get(lines.get(selection).value);
            } else {
                finalValue = null;
            }
            close();
        }

        return true;
    }

    public int getX() { return x; }
    public int getY() { return y; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }
}
