package com.bigsagebeast.hero.dialogue;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.bigsagebeast.hero.Graphics;
import com.bigsagebeast.hero.logic.TextEngine;
import com.bigsagebeast.hero.text.TextBlock;
import com.bigsagebeast.hero.glyphtile.GlyphTile;

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
    private boolean cancelable = true;
    private int linesFit;

    private TextBlock title;
    private TextBlock footer;
    private TextBlock lineParent;
    private TextBlock moreTop;
    private TextBlock moreBottom;
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

    public DialogueBox withCancelable(boolean cancelable) {
        this.cancelable = cancelable;
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
        height = (textLine * FONT_SIZE) + FOOTER_OFFSET_FROM_BOTTOM + 32; // TODO magic number
        if (!footerText.isEmpty()) {
            height += FONT_SIZE;
        }
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
        int pixelsForLines = height - FOOTER_OFFSET_FROM_BOTTOM - ITEM_OFFSET_FROM_TOP;
        linesFit = pixelsForLines / FONT_SIZE;

        textEngine.addBlock(lineParent);
        textEngine.addBlock(footer);
        textEngine.addBlock(title);

        moreTop = new TextBlock("(More)", null, FONT_SIZE, 0, 0,0, 0, Color.YELLOW);
        moreBottom = new TextBlock("(More)", null, FONT_SIZE, 0, linesFit - 1,0, 0, Color.YELLOW);
        moreTop.hidden = true;
        moreBottom.hidden = true;
        lineParent.addChild(moreTop);
        lineParent.addChild(moreBottom);


        for (int i=0; i<lines.size(); i++) {
            DialogueLine line = lines.get(i);
            line.textBlock = new TextBlock(line.text, null, FONT_SIZE, 0, i,
                    0, 0, Color.WHITE, line.glyphs);
            lineParent.addChild(line.textBlock);
        }
        if (lines.size() > linesFit) {
            // make space for 'more'
            linesFit -= 2;
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
        shiftLines();
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
        shiftLines();
    }

    private int topLine() {
        int middleLine = linesFit / 2;
        if (selection < middleLine) {
            return 0;
        } else if (selection > lines.size() - middleLine) {
            return lines.size() - linesFit;
        } else {
            return selection - middleLine;
        }
    }

    private void shiftLines() {
        if (linesFit >= lines.size()) {
            return;
        }
        int topLine = topLine();
        int bottomLine = topLine() + linesFit;
        moreTop.hidden = !(topLine > 0);
        moreBottom.hidden = !(bottomLine < lines.size() - 1);

        for (int i=0; i<lines.size(); i++) {
            lines.get(i).textBlock.y = i - topLine;
            if (i == topLine && i != 0 || i > bottomLine && bottomLine < lines.size() - 1) {
                // TODO: I feel like there's an off-by-one somewhere here
                lines.get(i).textBlock.hidden = true;
            } else if (i < topLine || i > bottomLine) {
                lines.get(i).textBlock.hidden = true;
            } else {
                lines.get(i).textBlock.hidden = false;
            }
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
        if ((keycode == Input.Keys.SPACE || keycode == Input.Keys.ESCAPE) && cancelable) {
            close();
        } else if (keycode == Input.Keys.UP || keycode == Input.Keys.NUMPAD_8) {
            selectPrevious();
        } else if (keycode == Input.Keys.DOWN || keycode == Input.Keys.NUMPAD_2) {
            selectNext();
        } else if (keycode == Input.Keys.ENTER || keycode == Input.Keys.NUMPAD_ENTER) {
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
