package com.bigsagebeast.hero.module;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.bigsagebeast.hero.*;
import com.bigsagebeast.hero.dialogue.TextEntryBox;
import com.bigsagebeast.hero.roguelike.game.Game;
import com.bigsagebeast.hero.roguelike.game.Profile;
import com.bigsagebeast.hero.story.StoryManager;
import com.bigsagebeast.hero.text.TextBlock;
import com.bigsagebeast.hero.text.effect.TextEffectGranularity;
import com.bigsagebeast.hero.text.effect.TextEffectJitter;

public class TitleScreenModule extends Module {

	Texture texture;

	public TitleScreenModule() {
		texture = new Texture(Gdx.files.internal("art/hero on cliff.png"));
	}
	
	@Override
	public void start() {
		super.start();
		MusicPlayer.playIntro();

		TextEffectJitter jitter = new TextEffectJitter(0.0125f, 4f);

		textEngine.addBlock(new TextBlock("THE", null,
				48f, 5f, 2f, 0f, 0f, Color.CYAN, 0f, 0f, jitter, null, TextEffectGranularity.LETTER));
		textEngine.addBlock(new TextBlock("FIRST", null,
				48f, 7f, 3.5f, 0f, 0f, Color.CYAN, 0f, 0f, jitter, null, TextEffectGranularity.LETTER));
		textEngine.addBlock(new TextBlock("HERO", null,
				48f, 9f, 5f, 0f, 0f, Color.CYAN, 0f, 0f, jitter, null, TextEffectGranularity.LETTER));

		//textEngine.addBlock(new TextBlock("@", null, 332f, 2.5f, 0.4f, Color.DARK_GRAY, 0f, 0f, null, null));

		textEngine.addBlock(new TextBlock("Game credits: Timothy Cook (BigSageBeast studios)", null, 14, 20, 50, Color.WHITE, -1f, -1f));
		textEngine.addBlock(new TextBlock("Music credit: WolfMeryX", null, 14, 20, 52, Color.WHITE, -1f, -1f));
		
		options[0] = new TextBlock("  Continue", null, 14, 18, 29, Color.WHITE, 0f, 0f);
		options[1] = new TextBlock("> New Game", null, 14, 18, 30, Color.YELLOW, 0f, 0f);
		options[2] = new TextBlock("  Skip to Dungeon", null, 14, 18, 31, Color.WHITE, 0f, 0f);
		options[3] = new TextBlock("  Skip to Aurex", null, 14, 18, 32, Color.WHITE, 0f, 0f);
		options[4] = new TextBlock("  Skip to Farm", null, 14, 18, 33, Color.WHITE, 0f, 0f);
		options[5] = new TextBlock("  Test duel", null, 14, 18, 34, Color.WHITE, 0f, 0f);
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
					options[i].color = Color.FIREBRICK;
				} else {
					options[i].color = Color.YELLOW;
				}
			} else {
				options[i].text = "  " + options[i].text.substring(2);
				if (i == 0) {
					options[i].color = Color.FIREBRICK;
				} else {
					options[i].color = Color.WHITE;
				}
			}
		}
	} 

	@Override
	public boolean keyDown(int keycode, boolean shift, boolean ctrl, boolean alt) {
		switch (keycode) {
			case Keys.S:
				StoryManager sm = new StoryManager();
				break;

			case Keys.UP:
			case Keys.NUMPAD_8:
				selectedOption = (selectedOption + 6) % 7;
				if (selectedOption == 0) {
					selectedOption = 6;
				}
				updateOptions();
				break;
			case Keys.DOWN:
				case Keys.NUMPAD_2:
				selectedOption = (selectedOption + 1) % 7;
				if (selectedOption == 0) {
					selectedOption++;
				}
				updateOptions();
				break;
			case Keys.ENTER:
			case Keys.NUMPAD_ENTER:
			case Keys.SPACE:
				switch (selectedOption) {
					case 0:
						/*
						IntroModule.musicResource.stop();
						end();
						GameLoop.roguelikeModule.start();
						GameLoop.roguelikeModule.game.load();
						 */
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
						MusicPlayer.stop();
						end();
						GameLoop.roguelikeModule.initialize();
						Game.startCaves();
						break;
					case 3:
						MusicPlayer.stop();
						end();
						GameLoop.roguelikeModule.initialize();
						Game.startAurex();
						break;
					case 4:
						MusicPlayer.stop();
						end();
						Profile.setString("mode", "newGameIntroQuest");
						GameLoop.flowModule.start();
						break;
					case 5:
						MusicPlayer.stop();
						end();
						GameLoop.duelModule.start();
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
		float height = Graphics.height;
		float width = (texture.getWidth()) * (height / texture.getHeight());
		float centerX = (Graphics.width/2) - (width/2);
		g.startBatch();
		g.batch().draw(texture, centerX, 0, width, height);
	}

	private void requestName() {
		MusicPlayer.stop();
		end();

		TextEntryBox box = new TextEntryBox()
				.withTitle("Dead god of Heroes, name yourself!")
				.withMargins(60, 60)
				.withMaxLength(20)
				.autoHeight();
		GameLoop.textEntryModule.openTextEntryBox(box, this::handleEnterName);

	}

	private void handleEnterName(String name) {
		if (name.isEmpty()) {
			name = "the God of Heroes";
		}
		Profile.setString("godName", name);
		Profile.setString("mode", "newGameCutscene1");
		GameLoop.flowModule.start();

	}

}
