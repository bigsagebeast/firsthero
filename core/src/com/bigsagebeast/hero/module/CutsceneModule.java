package com.bigsagebeast.hero.module;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.bigsagebeast.hero.GameLoop;
import com.bigsagebeast.hero.GameState;
import com.bigsagebeast.hero.Graphics;
import com.bigsagebeast.hero.GraphicsState;
import com.bigsagebeast.hero.text.TextBlock;
import com.bigsagebeast.hero.text.effect.TextEffectGranularity;
import com.bigsagebeast.hero.roguelike.game.Profile;

import java.util.ArrayList;
import java.util.List;

public class CutsceneModule extends Module {

	public static Music musicResource;
	boolean needInit;
	float startSeconds;
	Texture texture;
	public float lastSeconds = 0.0f;
	public float letterEndTime = 0.0f;

	private final static int SMALL_FONT = 14;
	private final static int LARGE_FONT = 20;

	List<TextBlock> blocks = new ArrayList<>();

	public Scene scene;

	public CutsceneModule() {
		scene = new Scene();
	}
	
	@Override
	public void start() {
		super.start();
		needInit = true;
	}

	private void init(GameState state) {
		startSeconds = state.getSeconds() + 0.5f;
		lastSeconds = startSeconds;
		int fontSize = LARGE_FONT;
		blocks.clear();

		texture = new Texture(Gdx.files.internal(scene.artFile));

		int lettersSoFar = 0;
		int topPixel = (Graphics.height*3/4);
		for (int i = 0; i < scene.text.size(); i++) {
			int pauses = 0;
			String paragraph = scene.text.get(i);
			String line = paragraph;
			while (line.endsWith("@")) {
				line = line.substring(0, line.length()-1);
				pauses++;
			}
			int pixelWidth = paragraph.length() * fontSize;
			int leftPixel = (Graphics.width/2) - (pixelWidth/2);

			TextBlock tb = new TextBlock(line, null, (float)fontSize, 0f, (float)i, leftPixel, topPixel, Color.WHITE, startSeconds + scene.secondsBeforeFade + scene.fadeTime + (scene.letterTime * lettersSoFar), scene.letterTime,
					null, null, TextEffectGranularity.BLOCK);
			textEngine.addBlock(tb);
			blocks.add(tb);

			lettersSoFar += line.length();
			lettersSoFar += 10 * pauses;
		}

		letterEndTime = startSeconds + scene.secondsBeforeFade + scene.fadeTime + (lettersSoFar * scene.letterTime);

		needInit = false;
	}

	public void loadIntro1() {
		scene.artFile = "art/Nemesis-kneeling.png";
		scene.secondsBeforeFade = 1.0f;
		scene.fadeTime = 1.0f;
		scene.letterTime = 0.05f;
		scene.text.clear();
		scene.text.add("My dearest sibling, let me tell you a story.@");
		scene.text.add("It's called, \"The farmboy who repelled the invaders\".@");
		scene.text.add("It's a small story, one told to children, back when");
		scene.text.add("the Bodnam still told stories, back in the Old King's");
		scene.text.add("era, before the worlds turned gray.@@");
		scene.text.add("");
		scene.text.add("I pray this mortal can revive your spark.");
		scene.text.add("                                          [Enter]");
	}

	public void loadIntro2() {
		String playerName = "(YOUR NAME)";
		if (Profile.getString("godName") != null) {
			playerName = Profile.getString("godName");
		}
		scene.artFile = "art/Nemesis-kneeling.png";
		scene.secondsBeforeFade = 1.0f;
		scene.fadeTime = 1.0f;
		scene.letterTime = 0.05f;
		scene.text.clear();
		scene.text.add("And so, the farmboy prayed to " + playerName + ",@");
		scene.text.add("first and last to take up the sword. It's such a");
		scene.text.add("small story, I'm not even in it! But this one");
		scene.text.add("happened, you know. In a world not so far away.@");
		scene.text.add("Your light is still here, isn't it?@@");
		scene.text.add("");
		scene.text.add("Please... Come back to us.");
		scene.text.add("                                          [Enter]");
	}

	@Override
	public void end() {
		textEngine.purge();
		GameLoop.flowModule.start();
		super.end();
	}
	
	// false = ignored, true = captured
	@Override
	public boolean keyTyped(char key, boolean ctrl, boolean alt) {
		return true;
	}

	public boolean keyDown(int keycode, boolean shift, boolean ctrl, boolean alt) {
		if (keycode == Keys.ENTER || keycode == Keys.NUMPAD_ENTER) {
			if (lastSeconds > letterEndTime) {
				end();
			} else {
				letterEndTime = 0;
				startSeconds = 0;
				for (TextBlock tb : blocks) {
					tb.quickFinish();
				}
			}
		}
		return true;
	}			
	
	@Override
	public void update(GameState state) {
		if (needInit) {
			init(state);
		}
		lastSeconds = state.getSeconds();
	}
	
	

	@Override
	public void render(Graphics g, GraphicsState gState) {
		if (needInit) {
			return;
		}
		// TODO Auto-generated method stub
		g.startBatch();
		if (lastSeconds < startSeconds + scene.secondsBeforeFade) {
			g.batch().setColor(1, 1, 1, 1f);
		} else if (lastSeconds < startSeconds + scene.secondsBeforeFade + scene.fadeTime) {
			float secondsIntoFade = lastSeconds - (startSeconds + scene.secondsBeforeFade);
			float proportion = secondsIntoFade / scene.fadeTime;
			float fade = 1.0f - (0.5f * proportion);
			g.batch().setColor(1, 1, 1, fade);
		} else {
			g.batch().setColor(1, 1, 1, 0.5f);
		}
		// presume that height is the bounding factor
		float height = Graphics.height;
		float width = (texture.getWidth()) * (height / texture.getHeight());
		float centerX = (Graphics.width/2) - (width/2);
		g.batch().draw(texture, centerX, 0, width, height);
		g.endBatch();
	}

	public class Scene {
		String artFile;
		List<String> text;
		float secondsBeforeFade;
		float fadeTime;
		float letterTime;

		public Scene() {
			text = new ArrayList<>();
		}
	}

}
