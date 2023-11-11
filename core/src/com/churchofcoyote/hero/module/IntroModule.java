package com.churchofcoyote.hero.module;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.churchofcoyote.hero.GameLoop;
import com.churchofcoyote.hero.GameState;
import com.churchofcoyote.hero.Graphics;
import com.churchofcoyote.hero.GraphicsState;
import com.churchofcoyote.hero.text.TextBlock;

public class IntroModule extends Module {

	final FileHandle musicFile;
	public static Music musicResource;
	boolean needInit;
	float startSeconds;
	
	private final static int SMALL_FONT = 14;

	public IntroModule() {
		String musicFilename = "aa_arofl.mp3";
		musicFile = Gdx.files.internal(musicFilename);
        musicResource = Gdx.audio.newMusic(musicFile);		
	}
	
	@Override
	public void start() {
		super.start();
		needInit = true;
	}
	
	private void init(GameState state) {
		musicResource.play();
		startSeconds = state.getSeconds() + 0.5f;
		
		// 110 x 62
		// 94 x 53
		
		needInit = false;
		float letterTime = 0.04f;
		float fadeTime = 4.0f;

		int firstLeft = 30;
		int firstTop = 4;
		float firstBegin = startSeconds + 0.5f;
		float firstEnd = startSeconds + 8f;

		int secondLeft = 29;
		int secondTop = 16;
		float secondBegin = startSeconds + 7f;
		float secondEnd = startSeconds + 14f;

		int thirdLeft = 27;
		int thirdTop = 28;
		float thirdBegin = startSeconds + 13f;
		float thirdEnd = startSeconds + 21f;

		int fourthLeft = 26;
		int fourthTop = 40;
		float fourthBegin = startSeconds + 20f;
		float fourthEnd = startSeconds + 28f;

		int fifthLeft = 27;
		int fifthTop = 13;
		float fifthBegin = startSeconds + 31f;
		float fifthEnd = startSeconds + 39f;

		int sixthLeft = 30;
		int sixthTop = 28;
		float sixthBegin = startSeconds + 38f;
		float sixthEnd = startSeconds + 45f;

		/*
		textEngine.addBlock(new TextBlock("12345678901234567890123456789012345678901234567890123456789012345678901234567890",
				12, 0, 0, Color.WHITE, 0f, 0f));
		textEngine.addBlock(new TextBlock("11",
				12, 0, 10, Color.WHITE, 0f, 0f));
		textEngine.addBlock(new TextBlock("21",
				12, 0, 20, Color.WHITE, 0f, 0f));
		textEngine.addBlock(new TextBlock("31",
				12, 0, 30, Color.WHITE, 0f, 0f));
		textEngine.addBlock(new TextBlock("41",
				12, 0, 40, Color.WHITE, 0f, 0f));
		*/
		textEngine.addBlock(new TextBlock("A long time ago,",
				SMALL_FONT, firstLeft + 8, firstTop + 0, Color.WHITE, firstBegin + 0f, letterTime).fade(firstEnd, firstEnd + fadeTime));
		textEngine.addBlock(new TextBlock("the world was destroyed,",
				SMALL_FONT, firstLeft + 4, firstTop + 2, Color.WHITE, firstBegin + 0.9f, letterTime).fade(firstEnd, firstEnd + fadeTime));
		textEngine.addBlock(new TextBlock("and time itself",
				SMALL_FONT, firstLeft + 0, firstTop + 4, Color.WHITE, firstBegin + 2.5f, letterTime).fade(firstEnd, firstEnd + fadeTime));
		textEngine.addBlock(new TextBlock(                "ceased to exist.",
				SMALL_FONT, firstLeft + 16, firstTop + 4, Color.WHITE, firstBegin + 3.5f, letterTime).fade(firstEnd, firstEnd + fadeTime));
		
		textEngine.addBlock(new TextBlock(      "Jealous demigods stole",
				SMALL_FONT, secondLeft + 6, secondTop + 0, Color.WHITE, secondBegin + 0.0f, letterTime).fade(secondEnd, secondEnd + fadeTime));
		textEngine.addBlock(new TextBlock(      "the fabric of reality,",
				SMALL_FONT, secondLeft + 6, secondTop + 2, Color.WHITE, secondBegin + 1f, letterTime).fade(secondEnd, secondEnd + fadeTime));
		textEngine.addBlock(new TextBlock("and divided it amongst themselves.",
				SMALL_FONT, secondLeft + 0, secondTop + 4, Color.WHITE, secondBegin + 2.5f, letterTime).fade(secondEnd, secondEnd + fadeTime));
		
		textEngine.addBlock(new TextBlock(        "Using this new power",
				SMALL_FONT, thirdLeft + 8, thirdTop + 0, Color.WHITE, thirdBegin + 0.0f, letterTime).fade(thirdEnd, thirdEnd + fadeTime));
		textEngine.addBlock(new TextBlock( "they created an infinity of worlds,",
				SMALL_FONT, thirdLeft + 1, thirdTop + 2, Color.WHITE, thirdBegin + 1.25f, letterTime).fade(thirdEnd, thirdEnd + fadeTime));
		textEngine.addBlock(new TextBlock("guided by their old myths and legends.",
				SMALL_FONT, thirdLeft + 0, thirdTop + 4, Color.WHITE, thirdBegin + 3.5f, letterTime).fade(thirdEnd, thirdEnd + fadeTime));
		
		textEngine.addBlock(new TextBlock("But these worlds, ",
				SMALL_FONT, fourthLeft + 0, fourthTop + 0, Color.WHITE, fourthBegin + 0.0f, letterTime).fade(fourthEnd, fourthEnd + fadeTime));
		textEngine.addBlock(new TextBlock("                  in all their multitude,",
				SMALL_FONT, fourthLeft + 0, fourthTop + 0, Color.WHITE, fourthBegin + 0.25f, letterTime).fade(fourthEnd, fourthEnd + fadeTime));
		textEngine.addBlock(new TextBlock( "are yet pale shadows of what used to be.",
				SMALL_FONT, fourthLeft + 1, fourthTop + 2, Color.WHITE, fourthBegin + 2.5f, letterTime).fade(fourthEnd, fourthEnd + fadeTime));
		textEngine.addBlock(new TextBlock(     "They are static and unchanging.",
				SMALL_FONT, fourthLeft + 6, fourthTop + 4, Color.WHITE, fourthBegin + 5f, letterTime).fade(fourthEnd, fourthEnd + fadeTime));
		
		textEngine.addBlock(new TextBlock(   "What is life without death?",
				SMALL_FONT, fifthLeft + 4, fifthTop + 0, Color.WHITE, fifthBegin + 0.1f, letterTime).fade(fifthEnd, fifthEnd + fadeTime));
		textEngine.addBlock(new TextBlock( "What is a beginning without an end?",
				SMALL_FONT, fifthLeft + 1, fifthTop + 2, Color.WHITE, fifthBegin + 2f, letterTime).fade(fifthEnd, fifthEnd + fadeTime));
		textEngine.addBlock(new TextBlock("The river of life has ceased to flow.",
				SMALL_FONT, fifthLeft + 0, fifthTop + 4, Color.WHITE, fifthBegin + 4f, letterTime).fade(fifthEnd, fifthEnd + fadeTime));
		
		textEngine.addBlock(new TextBlock("And so, for an eternity,",
				SMALL_FONT, sixthLeft + 1, sixthTop + 0, Color.WHITE, sixthBegin + 0.7f, letterTime).flame(sixthEnd));
		textEngine.addBlock(new TextBlock("these new worlds slumbered,",
				SMALL_FONT, sixthLeft + 0, sixthTop + 2, Color.WHITE, sixthBegin + 2.2f, letterTime).flame(sixthEnd));
		textEngine.addBlock(new TextBlock("waiting for a      ...",
				SMALL_FONT, sixthLeft + 3, sixthTop + 4, Color.WHITE, sixthBegin + 4.5f, letterTime).flame(sixthEnd));
		textEngine.addBlock(new TextBlock(              "spark",
				SMALL_FONT, sixthLeft + 17, sixthTop + 4, Color.YELLOW, sixthBegin + 4.5f + (letterTime * 14), letterTime).flame(sixthEnd));
		
		textEngine.addBlock(new TextBlock("[Enter to skip]", SMALL_FONT, 79, 52, Color.GRAY, -1f, -1f));
	}
	
	@Override
	public void end() {
		textEngine.purge();
		super.end();
	}
	
	// false = ignored, true = captured
	@Override
	public boolean keyTyped(char key, boolean ctrl, boolean alt) {
		return false;
	}

	public boolean keyDown(int keycode, boolean shift, boolean ctrl, boolean alt) {
		if (keycode == Keys.ENTER) {
			musicResource.setPosition(45.95f);
			end();
			GameLoop.titleModule.start();
		}
		return true;
	}			
	
	@Override
	public void update(GameState state) {
		if (needInit) {
			init(state);
		}
		if (state.getSeconds() > startSeconds + 46f) {
			end();
			GameLoop.titleModule.start();
		}
		
	}
	
	

	@Override
	public void render(Graphics g, GraphicsState gState) {
		// TODO Auto-generated method stub
		
	}

}
