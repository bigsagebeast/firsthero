package com.bigsagebeast.hero;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class Graphics {
	OrthographicCamera cam;
	Viewport viewport;

	private static Texture fixedFontTexture;
	private static BitmapFont fixedFont;
	private static Texture proportionalFontTexture;
	private static BitmapFont proportionalFont;
	private static Float defaultFontSize = 96f;
	private static boolean fullscreen = false;

	ShapeRenderer shapeBatch;
	SpriteBatch spriteBatch;
	boolean batchInProgress = false;

	public static final int STARTING_WIDTH = 1920;
	public static final int STARTING_HEIGHT = 1080;
	public static int width = STARTING_WIDTH;
	public static int height = STARTING_HEIGHT;
	public static int lastWindowedWidth = width;
	public static int lastWindowedHeight = height;

	public Graphics() {
		cam = new OrthographicCamera();

		fixedFontTexture = new Texture(Gdx.files.internal("lucida-console-96.png"));
		fixedFont = new BitmapFont(Gdx.files.internal("lucida-console-96.fnt"), new TextureRegion(fixedFontTexture), false);
		proportionalFontTexture = new Texture(Gdx.files.internal("times-new-roman-96.png"));
		proportionalFont = new BitmapFont(Gdx.files.internal("times-new-roman-96.fnt"), new TextureRegion(proportionalFontTexture), false);

		fixedFontTexture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		proportionalFontTexture.setFilter(TextureFilter.Linear, TextureFilter.Linear);

		shapeBatch = new ShapeRenderer();
		spriteBatch = new SpriteBatch();
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
		if (batchInProgress) {
			throw new RuntimeException("Tried to create a SpriteBatch within a batch");
		}
		spriteBatch.setProjectionMatrix(cam.combined);
		spriteBatch.begin();
		spriteBatch.setColor(1, 1, 1, 1);
		batchInProgress = true;
	}

	public void startShapeBatch(ShapeRenderer.ShapeType shapeType) {
		shapeBatch.begin(shapeType);
	}

	public void endShapeBatch() {
		shapeBatch.end();
	}

	public ShapeRenderer shapeBatch() {
		return shapeBatch;
	}
	
	public SpriteBatch batch() {
		if (!batchInProgress) {
			throw new RuntimeException("SpriteBatch not started");
		}
		return spriteBatch;
	}
	
	public void endBatch() {
		if (!batchInProgress) {
			throw new RuntimeException("Can't end an unstarted SpriteBatch");
		}
		//currentSpriteBatch.setShader(null);
		spriteBatch.end();
		batchInProgress = false;
	}

	public void writeFixed(String text, float x, float y, float fontSize, float offsetX, float offsetY, Color color) {
		fixedFont.setColor(color);
		fixedFont.getData().setScale(1.1f * fontSize / defaultFontSize);
		//font.getData().setScale(1f * fontSize / defaultFontSize);
		fixedFont.draw(batch(), text, offsetX + x * fontSize, height - (offsetY + y * fontSize));
	}

	public void writeProportional(String text, float x, float y, float fontSize, float offsetX, float offsetY, Color color) {
		proportionalFont.setColor(color);
		proportionalFont.getData().setScale(1.1f * fontSize / defaultFontSize);
		//font.getData().setScale(1f * fontSize / defaultFontSize);
		proportionalFont.draw(batch(), text, offsetX + x * fontSize, height - (offsetY + y * fontSize));
	}

	public static GlyphLayout createProportionalGlyphLayout(String text, float fontSize, float width, int halign, boolean wrap, Color color) {
		GlyphLayout layout = new GlyphLayout();
		proportionalFont.getData().setScale(fontSize / defaultFontSize);
		layout.setText(proportionalFont, text, color, width, halign, wrap);
		return layout;
	}

	public void writeProportional(GlyphLayout layout, float fontSize, float x, float y) {
		proportionalFont.getData().setScale(fontSize / defaultFontSize);
		proportionalFont.draw(batch(), layout, x, y);
	}

}
