package com.bigsagebeast.hero;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.bigsagebeast.hero.logic.TextEngine;
import com.bigsagebeast.hero.module.*;
import com.bigsagebeast.hero.engine.WindowEngine;
import com.bigsagebeast.hero.glyphtile.GlyphEngine;
import com.bigsagebeast.hero.logic.EffectEngine;
import com.bigsagebeast.hero.roguelike.world.DefinitionLoader;
import com.bigsagebeast.hero.chat.ChatBook;
import com.bigsagebeast.hero.chat.ChatLoader;
import com.bigsagebeast.hero.ui.UIManager;
import com.bigsagebeast.hero.util.QueuedKeypress;

public class GameLoop implements GameLogic, InputProcessor {

	public static TextEngine uiEngine = new TextEngine();
	public static TextEngine textEngine = new TextEngine();
	EffectEngine effectEngine = new EffectEngine();
	public static final GlyphEngine glyphEngine = new GlyphEngine();
	public static final ChatBook CHAT_BOOK = new ChatBook();

	public static final IntroModule introModule = new IntroModule();
	public static final TitleScreenModule titleModule = new TitleScreenModule();
	public static final RoguelikeModule roguelikeModule = new RoguelikeModule();
	public static final PopupModule popupModule = new PopupModule();
	public static final DialogueBoxModule dialogueBoxModule = new DialogueBoxModule();
	public static final TargetingModule targetingModule = new TargetingModule();
	public static final DirectionModule directionModule = new DirectionModule();
	public static final ChatModule CHAT_MODULE = new ChatModule();
	public static final CutsceneModule cutsceneModule = new CutsceneModule();
	public static final FlowModule flowModule = new FlowModule();
	public static final TextEntryModule textEntryModule = new TextEntryModule();
	public static final DescriptionModule descriptionModule = new DescriptionModule();
	private List<Module> allModules = new ArrayList<Module>();
	private Queue<QueuedKeypress> queuedKeyDown = new LinkedList<>();
	private Queue<QueuedKeypress> queuedKeyTyped = new LinkedList<>();
	private Queue<Float> queuedScrollEvents = new LinkedList<>();

	public GameLoop() {
		try {
			glyphEngine.initialize();

			ArrayList<FileHandle> files = new ArrayList<>();
			ArrayList<FileHandle> dirs = new ArrayList<>();
			dirs.add(Gdx.files.internal("defs"));

			FileHandle assetDefsHandle = Gdx.files.internal("assets-defs.txt");
			BufferedReader reader = new BufferedReader(new InputStreamReader(assetDefsHandle.read()));
			List<String> defFilePaths = new ArrayList<>();
			while (true) {
				String line;
				try {
					line = reader.readLine();
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
				if (line == null) break;
				if (line.length() > 0) {
					defFilePaths.add(line);
				}
			}

			while (!dirs.isEmpty()) {
				FileHandle[] dirFiles = dirs.remove(0).list();
				for (FileHandle dirFile : dirFiles) {
					if (dirFile.isDirectory()) {
						dirs.add(dirFile);
					} else {
						files.add(dirFile);
					}
				}
			}
			if (files.size() > 0 && files.size() != defFilePaths.size()) {
				throw new RuntimeException("Incorrect assets-defs manifest! " + files.size() + " vs " + defFilePaths.size());
			}
			/*
			for (FileHandle file : files) {
				DefinitionLoader.loadFile(file);
			};
			 */
			for (String defFilePath : defFilePaths) {
				FileHandle defFileHandle = Gdx.files.internal(defFilePath);
				DefinitionLoader.loadFile(defFileHandle);
			}
		} catch (SetupException e) {
			throw new RuntimeException(e);
		}

		ChatLoader.createPages(CHAT_BOOK, "story/story.json");

		//Gdx.graphics.setContinuousRendering(false);
		//Gdx.graphics.setVSync(false);
		Module.setEngines(textEngine, uiEngine, effectEngine);
		UIManager.resize(Graphics.width, Graphics.height);
		allModules = new ArrayList<Module>();
		allModules.add(textEntryModule);
		allModules.add(cutsceneModule);
		allModules.add(popupModule);
		allModules.add(descriptionModule);
		allModules.add(dialogueBoxModule);
		allModules.add(CHAT_MODULE);
		allModules.add(directionModule);
		allModules.add(targetingModule);
		allModules.add(introModule);
		allModules.add(titleModule);
		allModules.add(roguelikeModule);
		allModules.add(flowModule);

		introModule.start();
	}

	public void update(GameState state) {
		for (Module m : allModules) {
			if (m.isRunning()) {
				m.update(state);
			}
		}
		for (QueuedKeypress q : queuedKeyTyped) {
			for (Module m : allModules) {
				if (m.isRunning()) {
					if (m.keyTyped((char) q.keycode, q.ctrl, q.alt)) {
						break;
					}
				}
			}
		}
		queuedKeyTyped.clear();
		for (QueuedKeypress q : queuedKeyDown) {
			for (Module m : allModules) {
				if (m.isRunning()) {
					if (m.keyDown(q.keycode, q.shift, q.ctrl, q.alt)) {
						break;
					}
				}
			}
		}
		for (Float f : queuedScrollEvents) {
			for (Module m : allModules) {
				if (m.isRunning()) {
					if (m.scrolled(f)) {
						break;
					}
				}
			}
		}
		queuedKeyDown.clear();
		effectEngine.update(state);
		textEngine.update(state);
		uiEngine.update(state);
		glyphEngine.update(state);
	}
	
	public void render(Graphics g, GraphicsState gState) {
		ShapeRenderer shapeBatch = new ShapeRenderer();
		shapeBatch.begin(ShapeType.Filled);
	    shapeBatch.setColor(0.1f, 0.1f, 0.1f, 1.0f);
	    shapeBatch.rect(0, 0, g.getViewport().getWorldWidth(), g.getViewport().getWorldHeight());
	    shapeBatch.end();

		long startAll = System.currentTimeMillis();
		long start = System.currentTimeMillis();
		if (cutsceneModule.isRunning()) {
			cutsceneModule.render(g, gState);
		}
	    g.startBatch();
		if (roguelikeModule.isRunning()) {
			glyphEngine.render(g, gState);
		}
		HeroGame.updateTimer("ge", System.currentTimeMillis() - start);
		start = System.currentTimeMillis();
		uiEngine.render(g, gState);
		HeroGame.updateTimer("uie", System.currentTimeMillis() - start);
		WindowEngine.setAllClean();
		start = System.currentTimeMillis();
		if (roguelikeModule.isRunning()) {
			WindowEngine.render(g);
		}
		HeroGame.updateTimer("we", System.currentTimeMillis() - start);
		g.endBatch();
		start = System.currentTimeMillis();
		effectEngine.render(g, gState);
		HeroGame.updateTimer("ee", System.currentTimeMillis() - start);
		start = System.currentTimeMillis();
		g.startBatch();
	    textEngine.render(g, gState);
		HeroGame.updateTimer("te", System.currentTimeMillis() - start);
	    g.endBatch();
		HeroGame.updateTimer("all", System.currentTimeMillis() - startAll);
	}
	
	@Override
	public boolean keyDown(int keycode) {
		// TODO Auto-generated method stub
		boolean ctrl = Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT) || Gdx.input.isKeyPressed(Input.Keys.CONTROL_RIGHT);
		boolean alt = Gdx.input.isKeyPressed(Input.Keys.ALT_LEFT) || Gdx.input.isKeyPressed(Input.Keys.ALT_RIGHT);
		boolean shift = Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) || Gdx.input.isKeyPressed(Input.Keys.SHIFT_RIGHT);
		if (alt && keycode == Input.Keys.ENTER) {
			Graphics.swapFullscreen();
		} else {
			queuedKeyDown.add(new QueuedKeypress(keycode, shift, ctrl, alt));
		}
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		boolean ctrl = Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT) || Gdx.input.isKeyPressed(Input.Keys.CONTROL_RIGHT);
		boolean alt = Gdx.input.isKeyPressed(Input.Keys.ALT_LEFT) || Gdx.input.isKeyPressed(Input.Keys.ALT_RIGHT);
		boolean shift = Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) || Gdx.input.isKeyPressed(Input.Keys.SHIFT_RIGHT);
		queuedKeyTyped.add(new QueuedKeypress((int)character, shift, ctrl, alt));
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean scrolled(float x, float y) {
		queuedScrollEvents.add(y);
		return false;
	}

	public boolean touchCancelled(int a,int b,int c,int d) { return false; }
}
