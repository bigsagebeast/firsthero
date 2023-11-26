package com.churchofcoyote.hero;

import java.util.*;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.churchofcoyote.hero.engine.asciitile.AsciiTileEngine;
import com.churchofcoyote.hero.glyphtile.GlyphEngine;
import com.churchofcoyote.hero.logic.EffectEngine;
import com.churchofcoyote.hero.logic.TextEngine;
import com.churchofcoyote.hero.module.*;
import com.churchofcoyote.hero.util.QueuedKeypress;

import javax.print.Doc;

public class GameLoop implements GameLogic, InputProcessor {

	TextEngine uiEngine = new TextEngine();
	TextEngine textEngine = new TextEngine();
	EffectEngine effectEngine = new EffectEngine();
	AsciiTileEngine asciiTileEngine = new AsciiTileEngine();
	public static final GlyphEngine glyphEngine = new GlyphEngine();
	ShapeRenderer shapeBatch = new ShapeRenderer();
	
	public static final IntroModule introModule = new IntroModule();
	public static final TitleScreenModule titleModule = new TitleScreenModule();
	public static final RoguelikeModule roguelikeModule = new RoguelikeModule();
	public static final PopupModule popupModule = new PopupModule();
	public static final DialogueBoxModule dialogueBoxModule = new DialogueBoxModule();
	private List<Module> allModules = new ArrayList<Module>();
	private Queue<QueuedKeypress> queuedKeyDown = new LinkedList<>();
	private Queue<QueuedKeypress> queuedKeyTyped = new LinkedList<>();

	public GameLoop() {
		try {
			glyphEngine.initialize();
		} catch (SetupException e) {
			throw new RuntimeException(e);
		}

		//Gdx.graphics.setContinuousRendering(false);
		//Gdx.graphics.setVSync(false);
		Module.setEngines(textEngine, uiEngine, effectEngine, asciiTileEngine);
		allModules = new ArrayList<Module>();
		allModules.add(popupModule);
		allModules.add(dialogueBoxModule);
		allModules.add(introModule);
		allModules.add(titleModule);
		allModules.add(roguelikeModule);

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
		queuedKeyDown.clear();
		//asciiTileEngine.update(state);
		effectEngine.update(state);
		textEngine.update(state);
		uiEngine.update(state);
		glyphEngine.update(state);
	}
	
	public void render(Graphics g, GraphicsState gState) {
		shapeBatch.begin(ShapeType.Filled);
	    shapeBatch.setColor(0.1f, 0.1f, 0.1f, 1.0f);
	    shapeBatch.rect(0, 0, g.getViewport().getWorldWidth(), g.getViewport().getWorldHeight());
	    shapeBatch.end();

	    g.startBatch();
	    //asciiTileEngine.render(g, gState);
		glyphEngine.render(g, gState);
		uiEngine.render(g, gState);
		g.endBatch();
		effectEngine.render(g, gState);
		g.startBatch();
		long start = System.currentTimeMillis();
	    textEngine.render(g, gState);
	    HeroGame.updateTimer("te", System.currentTimeMillis() - start);
	    g.endBatch();
	}
	
	@Override
	public boolean keyDown(int keycode) {
		// TODO Auto-generated method stub
		boolean ctrl = Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT) || Gdx.input.isKeyPressed(Input.Keys.CONTROL_RIGHT);
		boolean alt = Gdx.input.isKeyPressed(Input.Keys.ALT_LEFT) || Gdx.input.isKeyPressed(Input.Keys.ALT_RIGHT);
		boolean shift = Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) || Gdx.input.isKeyPressed(Input.Keys.SHIFT_RIGHT);
		queuedKeyDown.add(new QueuedKeypress(keycode, shift, ctrl, alt));
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

	public boolean scrolled(int amount) {
		return false;
	}

	public boolean scrolled(float x, float y) {
		return false;
	}

	public boolean touchCancelled(int a,int b,int c,int d) { return false; }
}
