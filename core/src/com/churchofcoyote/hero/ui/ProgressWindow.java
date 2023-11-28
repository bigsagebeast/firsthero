package com.churchofcoyote.hero.ui;

import com.badlogic.gdx.graphics.Color;
import com.churchofcoyote.hero.engine.WindowEngine;
import com.churchofcoyote.hero.module.RoguelikeModule;
import com.churchofcoyote.hero.text.TextBlock;
import com.churchofcoyote.hero.text.effect.TextEffectGranularity;
import com.churchofcoyote.hero.text.effect.TextEffectJitter;

public abstract class ProgressWindow extends UIWindow {
    private TextBlock parentBlock;
    private TextBlock preBlock;
    private TextBlock midBlock;
    private TextBlock postBlock;
    public ProgressWindow() {
        parentBlock = new TextBlock("", getWindowName(), RoguelikeModule.FONT_SIZE, 0, 0, 0, 2, Color.WHITE);
        preBlock = new TextBlock("", null, RoguelikeModule.FONT_SIZE, 0, 0, Color.WHITE);
        midBlock = new TextBlock("", null, RoguelikeModule.FONT_SIZE, 0, 0, Color.WHITE);
        postBlock = new TextBlock("", null, RoguelikeModule.FONT_SIZE, 0, 0, Color.WHITE);
        parentBlock.addChild(preBlock);
        parentBlock.addChild(midBlock);
        parentBlock.addChild(postBlock);
    }

    public TextBlock getTextBlockParent() {
        return parentBlock;
    }

    protected abstract String getWindowName();
    protected abstract String getLabel();
    protected abstract Color getColor(int current, int max);
    public abstract void update();

    protected void setValue(int current, int max) {
        WindowEngine.setDirty(getWindowName());

        // TODO this does jitter a little bit, but only on updates.  hmm.
        //midBlock.close();
        //midBlock = new TextBlock("", null, (float)RoguelikeModule.FONT_SIZE, 0f, 0f, 0, 0, Color.WHITE, 0f, 0f, new TextEffectJitter(0.0525f, 1f), null, TextEffectGranularity.LETTER);
        //parentBlock.addChild(midBlock);

        int usableLength = WindowEngine.getSize(getWindowName()).x / RoguelikeModule.FONT_SIZE;
        String mid = getLabel() + ": " + current + "/" + max;
        int midLength = mid.length();
        int midStart = (usableLength / 2) - (mid.length() / 2);
        int midEnd = (usableLength / 2) + ((mid.length() + 1) / 2);
        //System.out.printf("usable %d midLength %d midStart %d\n", usableLength, midLength, midStart);
        float proportion = ((float)current / (float)max);
        int maxHashes = (int)(usableLength * proportion);

        String preString = produceHashes(Math.min(midStart - 1, maxHashes));
        String postString = produceHashes(Math.min(usableLength - midEnd - 1, maxHashes - midEnd));


        preBlock.text = preString;
        midBlock.text = mid;
        postBlock.text = postString;
        midBlock.x = midStart;
        postBlock.x = midEnd + 1;
        //progressBlock.text = "### " + getLabel() + ": " + current + "/" + max + " ###";
        preBlock.color = getColor(current, max);
        midBlock.color = getColor(current, max);
        postBlock.color = getColor(current, max);

        //System.out.println("updated progress to " + midBlock.text);
        parentBlock.compile();
    }

    private String produceHashes(int length) {
        StringBuilder sb = new StringBuilder();
        for (int i=0; i<length; i++)
            sb.append("#");
        return sb.toString();
    }
}
