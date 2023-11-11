package com.churchofcoyote.hero.module;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.churchofcoyote.hero.GameLoop;
import com.churchofcoyote.hero.GameState;
import com.churchofcoyote.hero.Graphics;
import com.churchofcoyote.hero.GraphicsState;
import com.churchofcoyote.hero.logic.TextEngine;
import com.churchofcoyote.hero.text.TextBlock;
import com.churchofcoyote.hero.text.effect.TextEffectGranularity;
import com.churchofcoyote.hero.text.effect.TextEffectJitter;

public class TitleScreenModule extends Module {

	public TitleScreenModule() {
	}
	
	@Override
	public void start() {
		super.start();

		TextEffectJitter jitter = new TextEffectJitter(0.0125f, 4f);
		/*
		TextBlock block = new TextBlock("Block jitter", 16f, 0f, 0f, Color.WHITE, 0f, 0.1f,
				jitter, null);
		activeBlocks.add(block);

		TextEffectJitter jitter2 = new TextEffectJitter(0.05f, 4f);
		TextBlock block2 = new TextBlock("Word jitter", 16f, 0f, 1f, Color.YELLOW, 0.5f, 0.1f,
				jitter2, null, TextEffectGranularity.WORD);
		activeBlocks.add(block2);

		TextEffectJitter jitter3 = new TextEffectJitter(0.1f, 2f);
		TextBlock block3 = new TextBlock("Heavy jitter", 16f, 0f, 2f, Color.RED, 1f, 0.1f,
				jitter3, null, TextEffectGranularity.LETTER);
		activeBlocks.add(block3);

		TextEffectJitter jitter4 = new TextEffectJitter(0.2f, 2f);
		TextBlock block4 = new TextBlock("Chaos jitter", 16f, 0f, 3f, Color.MAGENTA, 1.5f, 0.1f,
				jitter4, null, TextEffectGranularity.LETTER);
		activeBlocks.add(block4);

		TextEffectSwap swap1 = new TextEffectSwap(0.1f, 0.8f, 0.05f, 0.2f, true);
		TextEffectJitter jitter5 = new TextEffectJitter(0.2f, 4f);
		TextBlock block5 = new TextBlock(null, 16f, 0f, 6f, Color.ORANGE, 2f, 0.1f,
				null, null, TextEffectGranularity.BLOCK);
		TextBlock block5_1 = new TextBlock("Glitch", 16f, 0f, 0f, Color.ORANGE, 2f, 0.1f,
				null, null, TextEffectGranularity.BLOCK);
		TextBlock block5_2 = new TextBlock("text ", 16f, 7f, 0f, Color.ORANGE, 2.7f, 0.3f,
				null, swap1, TextEffectGranularity.BLOCK);
		TextBlock block5_2a = new TextBlock("kill", 16f, 0.3f, -0.2f, Color.SCARLET, 0f, 0f,
				jitter5, null, TextEffectGranularity.BLOCK);
		TextBlock block5_2b = new TextBlock("destroy", 16f, 0.5f, 0.5f, Color.SALMON, 0f, 0f,
				jitter5, null, TextEffectGranularity.BLOCK);
		TextBlock block5_2c = new TextBlock("no mercy", 16f, -2f, -0.8f, Color.TAN, 0f, 0f,
				jitter5, null, TextEffectGranularity.BLOCK);
		TextBlock block5_2d = new TextBlock("power", 16f, -0.5f, 0.3f, Color.ROYAL, 0f, 0f,
				jitter5, null, TextEffectGranularity.BLOCK);
		block5.addChild(block5_1);
		block5.addChild(block5_2);
		swap1.addAlternate(block5_2a);
		swap1.addAlternate(block5_2b);
		swap1.addAlternate(block5_2c);
		swap1.addAlternate(block5_2d);
		activeBlocks.add(block5);
		*/
		
		textEngine.addBlock(new TextBlock("THE",
				48f, 5f, 2f, Color.CYAN, 0f, 0f, jitter, null, TextEffectGranularity.LETTER));
		textEngine.addBlock(new TextBlock("FIRST",
				48f, 7f, 3.5f, Color.CYAN, 0f, 0f, jitter, null, TextEffectGranularity.LETTER));
		textEngine.addBlock(new TextBlock("HERO",
				48f, 9f, 5f, Color.CYAN, 0f, 0f, jitter, null, TextEffectGranularity.LETTER));

		textEngine.addBlock(new TextBlock("@", 332f, 2.5f, 0.4f, Color.DARK_GRAY, 0f, 0f, null, null));

		textEngine.addBlock(new TextBlock("Music credit: Archons of Light - Nils Ingvarsson", 14, 0, 52, Color.GRAY, -1f, -1f));
		
		options[0] = new TextBlock("  Continue", 14, 18, 29, Color.GRAY, 0f, 0f);
		options[1] = new TextBlock("> New Game", 14, 18, 30, Color.YELLOW, 0f, 0f);
		options[2] = new TextBlock("  Settings", 14, 18, 31, Color.WHITE, 0f, 0f);
		options[3] = new TextBlock("  Watch Intro", 14, 18, 32, Color.WHITE, 0f, 0f);
		options[4] = new TextBlock("  Quit", 14, 18, 33, Color.WHITE, 0f, 0f);
		updateOptions();
		for (TextBlock tb : options) {
			textEngine.addBlock(tb);
		}
	}
	
	public TextBlock[] options = new TextBlock[5];
	int selectedOption = 1;
	
	private void updateOptions() {
		for (int i=0; i<5; i++) {
			if (selectedOption == i) {
				options[i].text = "> " + options[i].text.substring(2);
				if (i == 0 || i == 2) {
					options[i].color = Color.GRAY;
				} else {
					options[i].color = Color.YELLOW;
				}
			} else {
				options[i].text = "  " + options[i].text.substring(2);
				if (i == 0 || i == 2) {
					options[i].color = Color.GRAY;
				} else {
					options[i].color = Color.WHITE;
				}
			}
		}
	} 

	@Override
	public boolean keyDown(int keycode, boolean shift, boolean ctrl, boolean alt) {
		switch (keycode) {
		case Keys.UP:
			selectedOption = (selectedOption + 4) % 5;
			updateOptions();
			break;
		case Keys.DOWN:
			selectedOption = (selectedOption + 1) % 5;
			updateOptions();
			break;
		case Keys.ENTER:
		case Keys.SPACE:
			switch (selectedOption) {
			case 1:
				IntroModule.musicResource.stop();
				end();
				GameLoop.roguelikeModule.start();
				break;
			case 3:
				IntroModule.musicResource.stop();
				end();
				GameLoop.introModule.start();
				break;
			case 4:
				Gdx.app.exit();
			}
			break;
		}
		return true;
	}
	
	@Override
	public void end() {
		super.end();
		textEngine.purge();
	}
	
	@Override
	public void update(GameState state) {
		// TODO Auto-generated method stub

	}

	@Override
	public void render(Graphics g, GraphicsState gState) {
		// TODO Auto-generated method stub

	}

}
