package com.churchofcoyote.hero.module;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.churchofcoyote.hero.GameLoop;
import com.churchofcoyote.hero.GameState;
import com.churchofcoyote.hero.Graphics;
import com.churchofcoyote.hero.GraphicsState;
import com.churchofcoyote.hero.dialogue.TextEntryBox;
import com.churchofcoyote.hero.roguelike.game.Profile;
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
		
		textEngine.addBlock(new TextBlock("THE", null,
				48f, 5f, 2f, 0f, 0f, Color.CYAN, 0f, 0f, jitter, null, TextEffectGranularity.LETTER));
		textEngine.addBlock(new TextBlock("FIRST", null,
				48f, 7f, 3.5f, 0f, 0f, Color.CYAN, 0f, 0f, jitter, null, TextEffectGranularity.LETTER));
		textEngine.addBlock(new TextBlock("HERO", null,
				48f, 9f, 5f, 0f, 0f, Color.CYAN, 0f, 0f, jitter, null, TextEffectGranularity.LETTER));

		textEngine.addBlock(new TextBlock("@", null, 332f, 2.5f, 0.4f, Color.DARK_GRAY, 0f, 0f, null, null));

		textEngine.addBlock(new TextBlock("Music credit: Archons of Light - Nils Ingvarsson", null, 14, 0, 52, Color.GRAY, -1f, -1f));
		
		options[0] = new TextBlock("  Continue", null, 14, 18, 29, Color.WHITE, 0f, 0f);
		options[1] = new TextBlock("> New Game", null, 14, 18, 30, Color.YELLOW, 0f, 0f);
		options[2] = new TextBlock("  Dungeon", null, 14, 18, 31, Color.WHITE, 0f, 0f);
		options[3] = new TextBlock("  Aurex", null, 14, 18, 32, Color.WHITE, 0f, 0f);
		options[4] = new TextBlock("  Watch Cutscene", null, 14, 18, 33, Color.WHITE, 0f, 0f);
		options[5] = new TextBlock("  Watch Intro", null, 14, 18, 34, Color.WHITE, 0f, 0f);
		options[6] = new TextBlock("  Quit", null, 14, 18, 35, Color.WHITE, 0f, 0f);
		updateOptions();
		for (TextBlock tb : options) {
			textEngine.addBlock(tb);
		}
	}
	
	public TextBlock[] options = new TextBlock[7];
	int selectedOption = 1;
	
	private void updateOptions() {
		for (int i=0; i<7; i++) {
			if (selectedOption == i) {
				options[i].text = "> " + options[i].text.substring(2);
				if (i == 0) {
					options[i].color = Color.GRAY;
				} else {
					options[i].color = Color.YELLOW;
				}
			} else {
				options[i].text = "  " + options[i].text.substring(2);
				if (i == 0) {
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
			case Keys.NUMPAD_8:
				selectedOption = (selectedOption + 6) % 7;
				updateOptions();
				break;
			case Keys.DOWN:
				case Keys.NUMPAD_2:
				selectedOption = (selectedOption + 1) % 7;
				updateOptions();
				break;
			case Keys.ENTER:
			case Keys.SPACE:
				switch (selectedOption) {
					case 0:
						IntroModule.musicResource.stop();
						end();
						GameLoop.roguelikeModule.start();
						GameLoop.roguelikeModule.game.load();
						break;
					case 1:
						requestName();
						/*
						IntroModule.musicResource.stop();
						end();
						Profile.setString("mode", "newGameIntroQuest");
						GameLoop.flowModule.start();
						 */
						break;
					case 2:
						IntroModule.musicResource.stop();
						end();
						GameLoop.roguelikeModule.initialize();
						GameLoop.roguelikeModule.game.startCaves();
						break;
					case 3:
						IntroModule.musicResource.stop();
						end();
						GameLoop.roguelikeModule.initialize();
						GameLoop.roguelikeModule.game.startAurex();
						break;
					case 4:
						IntroModule.musicResource.stop();
						end();
						Profile.setString("mode", "newGameCutscene1");
						GameLoop.flowModule.start();
						break;
					case 5:
						IntroModule.musicResource.stop();
						end();
						GameLoop.introModule.start();
						break;
					case 6:
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

	private void requestName() {
		IntroModule.musicResource.stop();
		end();

		TextEntryBox box = new TextEntryBox()
				.withTitle("Name yourself, God of Heroes!")
				.withMargins(60, 60);
		box.autoHeight();
		GameLoop.textEntryModule.openTextEntryBox(box, this::handleEnterName);

	}

	private void handleEnterName(String name) {

		Profile.setString("name", name);
		Profile.setString("mode", "newGameCutscene1");
		GameLoop.flowModule.start();

	}

}
