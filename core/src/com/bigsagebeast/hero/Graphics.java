package com.bigsagebeast.hero;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class Graphics {
	OrthographicCamera cam;
	Viewport viewport;

	private Texture fontTexture;
	private BitmapFont font;
	private Float defaultFontSize = 96f;
	private static boolean fullscreen = false;

	SpriteBatch currentSpriteBatch;
	boolean batchInProgress = false;

	public static final int STARTING_WIDTH = 1920;
	public static final int STARTING_HEIGHT = 1080;
	public static int width = STARTING_WIDTH;
	public static int height = STARTING_HEIGHT;
	public static int lastWindowedWidth = width;
	public static int lastWindowedHeight = height;

	public Graphics() {
		cam = new OrthographicCamera();

		fontTexture = new Texture(Gdx.files.internal("lucida-console-96.png"));
		font = new BitmapFont(Gdx.files.internal("lucida-console-96.fnt"), new TextureRegion(fontTexture), false);
		fontTexture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
	}

	public static void swapFullscreen() {
		if (fullscreen) {
			fullscreen = false;
			Gdx.graphics.setWindowedMode(lastWindowedWidth, lastWindowedHeight);
		} else {
			fullscreen = true;
			Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
		}
	}

	public void resize(int x, int y) {
		if (!fullscreen) {
			lastWindowedWidth = x;
			lastWindowedHeight = y;
		}
		width = x;
		height = y;
		viewport = new FitViewport(width, height, cam);
		getViewport().update(width, height, true);
	}
	
	public Viewport getViewport() {
		return viewport;
	}
	
	public void startBatch() {
		// TODO necessary?  seems not
		currentSpriteBatch = new SpriteBatch();
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
		font.draw(batch(), text, offsetX + x * fontSize, height - (offsetY + y * fontSize));
	}
	
	public BitmapFont font() {
		return font;
	}

}
