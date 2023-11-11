package com.churchofcoyote.hero;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.churchofcoyote.hero.engine.asciitile.AsciiTileEngine;
import com.churchofcoyote.hero.logic.TextEngine;
import com.churchofcoyote.hero.module.IntroModule;
import com.churchofcoyote.hero.module.Module;
import com.churchofcoyote.hero.module.RoguelikeModule;
import com.churchofcoyote.hero.module.TitleScreenModule;

public class GameLoop implements GameLogic, InputProcessor {

	TextEngine textEngine = new TextEngine();
	AsciiTileEngine asciiTileEngine = new AsciiTileEngine();
	ShapeRenderer shapeBatch = new ShapeRenderer();
	
	public static final IntroModule introModule = new IntroModule();
	public static final TitleScreenModule titleModule = new TitleScreenModule();
	public static final RoguelikeModule roguelikeModule = new RoguelikeModule();
	private List<Module> allModules = new ArrayList<Module>();
	
	public GameLoop() {
		//Gdx.graphics.setContinuousRendering(false);
		//Gdx.graphics.setVSync(false);
		Module.setEngines(textEngine, asciiTileEngine);
		allModules = new ArrayList<Module>();
		allModules.add(introModule);
		allModules.add(titleModule);
		allModules.add(roguelikeModule);
		
		//roguelikeModule.start();
		introModule.start();
		//titleModule.start();
		
		
		
	}
	
	public void update(GameState state) {
		for (Module m : allModules) {
			if (m.isRunning()) {
				m.update(state);
			}
		}
		asciiTileEngine.update(state);
		textEngine.update(state);
		
		/*
		if (state.getSeconds() >= 46f && introModule.isRunning()) {
			introModule.end();
			titleModule.start();
		}
		*/
	}
	
	public void render(Graphics g, GraphicsState gState) {
		shapeBatch.begin(ShapeType.Filled);
	    shapeBatch.setColor(0.1f, 0.1f, 0.1f, 1.0f);
	    shapeBatch.rect(0, 0, g.getViewport().getWorldWidth(), g.getViewport().getWorldHeight());
	    shapeBatch.end();

	    g.startBatch();
	    asciiTileEngine.render(g, gState);
	    long start = System.currentTimeMillis();
	    textEngine.render(g, gState);
	    HeroGame.updateTimer("te", System.currentTimeMillis() - start);
	    g.endBatch();
	    
	    
		
	    /*
	    g.startBatch();
		g.font().setColor(Color.BLUE);
		g.font().getData().setScale(1.0f);
		g.font().draw(g.batch(), "Test", 20f, 20f);
		g.batch().end();
		g.batch().begin();
		g.batch().setShader(g.fontShader());
		g.font().setColor(Color.WHITE);
		g.font().getData().setScale(0.5f);
		g.font().draw(g.batch(), "Test2", 20f, 150f);
		g.batch().setShader(null);
		g.batch().end();
		*/
	}
	
	@Override
	public boolean keyDown(int keycode) {
		// TODO Auto-generated method stub
		boolean ctrl = Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT) || Gdx.input.isKeyPressed(Input.Keys.CONTROL_RIGHT);
		boolean alt = Gdx.input.isKeyPressed(Input.Keys.ALT_LEFT) || Gdx.input.isKeyPressed(Input.Keys.ALT_RIGHT);
		boolean shift = Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) || Gdx.input.isKeyPressed(Input.Keys.SHIFT_RIGHT);
/*		if (keycode == Input.Keys.LEFT ||
			keycode == Input.Keys.RIGHT ||
			keycode == Input.Keys.UP ||
			keycode == Input.Keys.DOWN)*/ {
			for (Module m : allModules) {
				if (m.isRunning()) {
					if (m.keyDown(keycode, shift, ctrl, alt)) {
						return true;
					}
				}
			}
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
		for (Module m : allModules) {
			if (m.isRunning()) {
				if (m.keyTyped(character, ctrl, alt)) {
					return true;
				}
			}
		}
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

	@Override
	public boolean scrolled(int amount) {
		// TODO Auto-generated method stub
		return false;
	}
}
