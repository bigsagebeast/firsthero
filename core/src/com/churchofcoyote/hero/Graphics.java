package com.churchofcoyote.hero;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.churchofcoyote.hero.glyphtile.GlyphEngine;

public class Graphics {
	OrthographicCamera cam;
	Viewport viewport;

	private Texture fontTexture;
	private BitmapFont font;
	private Float defaultFontSize = 96f;

	SpriteBatch currentSpriteBatch;
	boolean batchInProgress = false;
	
	public static final int WIDTH = (12 * 110);
	public static final int HEIGHT = (12 * 62);
	
	public Graphics() {
		cam = new OrthographicCamera(WIDTH, HEIGHT);
		viewport = new FitViewport(WIDTH, HEIGHT, cam);

		fontTexture = new Texture(Gdx.files.internal("lucida-console-96.png"));
		font = new BitmapFont(Gdx.files.internal("lucida-console-96.fnt"), new TextureRegion(fontTexture), false);
		fontTexture.setFilter(TextureFilter.Linear, TextureFilter.Linear);

		currentSpriteBatch = new SpriteBatch();
		
	}
	
	public Viewport getViewport() {
		return viewport;
	}
	
	public void startBatch() {
		if (batchInProgress) {
			throw new RuntimeException("Tried to create a SpriteBatch within a batch");
		}
		currentSpriteBatch.setProjectionMatrix(cam.combined);
		currentSpriteBatch.begin();
		batchInProgress = true;
	}
	
	public SpriteBatch batch() {
		if (!batchInProgress) {
			throw new RuntimeException("SpriteBatch not started");
		}
		return currentSpriteBatch;
	}
	
	public void endBatch() {
		if (!batchInProgress) {
			throw new RuntimeException("Can't end an unstarted SpriteBatch");
		}
		//currentSpriteBatch.setShader(null);
		currentSpriteBatch.end();
		batchInProgress = false;
	}
	
	public void write(String text, float x, float y, float fontSize, float offsetX, float offsetY, Color color) {
		font.setColor(color);
		font.getData().setScale(1.1f * fontSize / defaultFontSize);
		//font.getData().setScale(1f * fontSize / defaultFontSize);
		font.draw(batch(), text, offsetX + x * fontSize, HEIGHT - (offsetY + y * fontSize));
	}
	
	public BitmapFont font() {
		return font;
	}

}
