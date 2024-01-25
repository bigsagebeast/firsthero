package com.bigsagebeast.hero.text;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.bigsagebeast.hero.module.RoguelikeModule;
import com.bigsagebeast.hero.GameLogic;
import com.bigsagebeast.hero.GameState;
import com.bigsagebeast.hero.Graphics;
import com.bigsagebeast.hero.GraphicsState;
import com.bigsagebeast.hero.engine.WindowEngine;
import com.bigsagebeast.hero.glyphtile.GlyphEngine;
import com.bigsagebeast.hero.glyphtile.GlyphTile;
import com.bigsagebeast.hero.text.effect.TextEffectFadeOut;
import com.bigsagebeast.hero.text.effect.TextEffectGranularity;
import com.bigsagebeast.hero.text.effect.TextEffectJitter;
import com.bigsagebeast.hero.text.effect.TextEffectSwap;
import com.bigsagebeast.hero.text.effect.TextEffectSwapLinear;

public class TextBlock implements GameLogic {
	private final Random random = new Random();
	
	private boolean closed;
	public Set<TextBlock> children = new HashSet<TextBlock>();

	public float x;
	public float y;
	public Color color;
	public float pixelOffsetX;
	public float pixelOffsetY;
	public boolean hidden;

	// tickStart: What tick to display the first letter in this block.
	// used during tick
	private Float secondStart;
	private Float secondsPerLetter;
	private Float secondsEnd;
	
	// calculated during tick, used during render
	private int lettersSoFar;
	
	private TextEffectJitter jitter;
	private TextEffectSwap swap;
	private List<TextEffect> effects = new ArrayList<TextEffect>();

	public GlyphLayout glyphLayout;

	public Float fontSize;
	public String text;
	public GlyphTile glyph;

	public String frameBufferKey;

	// locked = only render once
	private boolean wantLocked = false;
	private boolean isLocked = false;
	private FrameBuffer buffer;
	private TextureRegion texRegion;



	public TextBlock(GlyphTile glyph, String frameBufferKey, Float fontsize, Float x, Float y, float pixelOffsetX, float pixelOffsetY,
		TextEffectJitter jitter, TextEffectSwap swap) {
		this.glyph = glyph;
		this.frameBufferKey = frameBufferKey;
		this.fontSize = fontsize;
		this.x = x;
		this.y = y;
		this.pixelOffsetX = pixelOffsetX;
		this.pixelOffsetY = pixelOffsetY;
		this.jitter = jitter;
		this.swap = swap;
	}

	public TextBlock() { this(""); }

	public TextBlock(String text) {
		this(text, null, RoguelikeModule.FONT_SIZE, 0, 0, Color.WHITE);
	}

	public TextBlock(String text, Color color) {
		this(text, null, RoguelikeModule.FONT_SIZE, 0, 0, color);
	}

	public TextBlock(String text, int x, int y, Color color) {
		this(text, null, RoguelikeModule.FONT_SIZE, x, y, color);
	}

	public TextBlock(String text, int x, int y) {
		this(text, x, y, Color.WHITE);
	}

	public TextBlock(GlyphLayout glyphLayout, float fontSize, String frameBufferKey, float pixelOffsetX, float pixelOffsetY) {
		this.text = null;
		this.glyphLayout = glyphLayout;
		this.fontSize = fontSize;
		this.frameBufferKey = frameBufferKey;
		this.pixelOffsetX = pixelOffsetX;
		this.pixelOffsetY = pixelOffsetY;
	}

	public TextBlock(GlyphLayout glyphLayout, float fontSize) {
		this(glyphLayout, fontSize, null, 0, 0);
	}

	public TextBlock(String text, String frameBufferKey, Float fontSize,
					 Float x, Float y, float pixelOffsetX, float pixelOffsetY,
					 Color color,
					 Float secondStart, Float secondsPerLetter,
					 TextEffectJitter jitter, TextEffectSwap swap,
					 TextEffectGranularity granularity) {
		this.frameBufferKey = frameBufferKey;
		this.pixelOffsetX = pixelOffsetX;
		this.pixelOffsetY = pixelOffsetY;
		this.secondStart = secondStart;
		this.secondsPerLetter = secondsPerLetter;
		this.color = color;
		this.fontSize = fontSize;
		this.jitter = jitter;
		this.swap = swap;
		switch (granularity) {
			case BLOCK:
				this.text = text;
				this.x = x;
				this.y = y;
				if (jitter != null) {
					effects.add(jitter);
				}
				if (swap != null) {
					effects.add(swap);
				}
				break;
			case WORD:
				int letters = 0;
				String[] words = text.split(" ");
				for (int i=0; i<words.length; i++) {
					if (i > 0) {
						words[i] = " " + words[i];
					}
					TextBlock letterBlock = new TextBlock(
							words[i], frameBufferKey, fontSize,
							x + letters, y, pixelOffsetX, pixelOffsetY, color,
							secondStart + (secondsPerLetter * letters), 0.1f,
							jitter == null ? null : new TextEffectJitter(jitter),
							null, TextEffectGranularity.BLOCK);
					children.add(letterBlock);
					letters += words[i].length();
				}
				break;
			case LETTER:
				for (int i=0; i<text.length(); i++) {
					TextBlock letterBlock = new TextBlock(
							text.substring(i, i+1), frameBufferKey, fontSize,
							x + i, y, pixelOffsetX, pixelOffsetY, color,
							secondStart + (secondsPerLetter * i), 0.1f,
							jitter == null ? null : new TextEffectJitter(jitter),
							null, TextEffectGranularity.BLOCK);
					children.add(letterBlock);
				}
				break;
		}
	}

	public TextBlock(String text, String frameBufferKey, float fontSize,
					 int x, int y, Color color) {
		this(text, frameBufferKey, (float)fontSize, (float)x, (float)y, 0f, 0f, color, -1f, -1f,
				null, null, TextEffectGranularity.BLOCK);
	}

	public TextBlock(String text, String frameBufferKey, float fontSize,
					 int x, int y, float pixelOffsetX, float pixelOffsetY, Color color) {
		this(text, frameBufferKey, (float)fontSize, (float)x, (float)y, pixelOffsetX, pixelOffsetY, color, -1f, -1f,
				null, null, TextEffectGranularity.BLOCK);
	}

	public TextBlock(String text, String frameBufferKey, float fontSize,
					 int x, int y, float pixelOffsetX, float pixelOffsetY, Color color, GlyphTile[] glyphs) {
		this(text, frameBufferKey, (float)fontSize, (float)x, (float)y, pixelOffsetX, pixelOffsetY, color, -1f, -1f,
				null, null, TextEffectGranularity.BLOCK);
		char[] chars = text.toCharArray();
		int glyphIndex = 0;
		for (int i=0; i<chars.length; i++) {
			if (chars[i] == '`') {
				if (glyphIndex > glyphs.length) {
					throw new RuntimeException("Glyphs in string (" + (glyphIndex + 1) + ") exceeded glyphs provided (" + glyphs.length + ")");
				}
				TextBlock glyphBlock = new TextBlock(glyphs[glyphIndex], frameBufferKey, (float)fontSize, (float)i, 0f, 0f, 0f, null, null);
				addChild(glyphBlock);
				glyphIndex++;
			}
		}
		if (glyphIndex > 0) {
			this.text = this.text.replace('`', ' ');
		}
	}

	public TextBlock(String text, Color color, GlyphTile glyph) {
		this(text, null, RoguelikeModule.FONT_SIZE, 0, 0, 0, 0, color, new GlyphTile[] {glyph});
	}

	public TextBlock(String text, String frameBufferKey, int fontSize,
					 int x, int y, float pixelOffsetX, float pixelOffsetY, Color color,
					 Float secondStart, Float secondsPerLetter) {
		this(text, frameBufferKey, (float)fontSize, (float)x, (float)y, pixelOffsetX, pixelOffsetY, color, secondStart, secondsPerLetter,
				null, null, TextEffectGranularity.BLOCK);
	}

	public TextBlock(String text, String frameBufferKey, int fontSize,
					 int x, int y, Color color,
					 Float secondStart, Float secondsPerLetter) {
		this(text, frameBufferKey, (float)fontSize, (float)x, (float)y, 0f, 0f, color, secondStart, secondsPerLetter,
				null, null, TextEffectGranularity.BLOCK);
	}


	public TextBlock(String text, String frameBufferKey, Float fontSize,
					 Float x, Float y, float pixelOffsetX, float pixelOffsetY, Color color,
					 Float secondStart, Float secondsPerLetter,
					 TextEffectJitter jitter, TextEffectSwap swap) {
		this(text, frameBufferKey, fontSize, x, y, pixelOffsetX, pixelOffsetY, color, secondStart, secondsPerLetter,
				jitter, swap, TextEffectGranularity.BLOCK);
	}

	public TextBlock(String text, String frameBufferKey, Float fontSize,
					 Float x, Float y, Color color,
					 Float secondStart, Float secondsPerLetter,
					 TextEffectJitter jitter, TextEffectSwap swap) {
		this(text, frameBufferKey, fontSize, x, y, 0f, 0f, color, secondStart, secondsPerLetter,
				jitter, swap, TextEffectGranularity.BLOCK);
	}

	public void close() {
		closed = true;
	}
	
	public boolean isClosed() {
		return closed;
	}

	public void quickFinish() {
		secondStart = Float.NEGATIVE_INFINITY;
	}

	@Override
	public void update(GameState state) {
		if (closed) {
			return;
		}
		if (jitter != null) {
			jitter.update(state);
		}
		if (swap != null) {
			swap.update(state);
		}
		if (text != null) {
			lettersSoFar = (int)(1 + ((state.getSeconds() - secondStart) / secondsPerLetter));
			if (lettersSoFar > text.length()) lettersSoFar = text.length();
		}
		closed = (secondsEnd == null) ? false : state.getSeconds() >= secondsEnd;
		for (TextEffect effect : effects) {
			effect.update(state);
			if (effect.isClosed(state)) {
				closed = true;
			}
		}
		Set<TextBlock> closingChildren = new HashSet<TextBlock>();
		for (TextBlock child : children) {
			child.update(state);
			if (child.isClosed()) {
				closingChildren.add(child);
			}
		}
		for (TextBlock child : closingChildren) {
			children.remove(child);
		}
		
	}

	@Override
	public void render(Graphics g, GraphicsState gState) {
		render(g, gState, 0f, 0f, 0f, 0f);
	}
	
	public void render(Graphics g, GraphicsState gState, float offsetX, float offsetY, float pixelOffsetX, float pixelOffsetY) {
		if (frameBufferKey != null && !WindowEngine.isDirty(frameBufferKey)) {
			return;
		}
		if (hidden) {
			return;
		}
		if (!isLocked) {
			if (wantLocked) {
				if (buffer != null) {
					buffer.dispose();
				}
				g.endBatch();
				try {
					buffer = new FrameBuffer(Format.RGBA8888, Graphics.width, Graphics.height, false);
				} catch (Exception e) {
					buffer = null;
					return;
				}
				texRegion = new TextureRegion(buffer.getColorBufferTexture(), 0, 0, Graphics.width, Graphics.height);
				//texRegion.flip(false, true);
				

				g.startBatch();
				buffer.begin();
			}
			if (jitter != null) {
				offsetX += jitter.getX();
				offsetY += jitter.getY();
			}
			TextBlock alternate = null; 
			if (swap != null) {
				if (swap.isWaitForWord() && lettersSoFar == text.length()) {
					alternate = swap.getAlternate();
				}
			}
			if (alternate != null) {
				alternate.render(g, gState, x + offsetX, y + offsetY, pixelOffsetX + this.pixelOffsetX, pixelOffsetY + this.pixelOffsetY);
			} else {
				for (TextBlock child : children) {
					child.render(g, gState, x + offsetX, y + offsetY, pixelOffsetX + this.pixelOffsetX, pixelOffsetY + this.pixelOffsetY);
				}
				if (secondStart == null || secondStart < 0) {
					if (text != null) {
						lettersSoFar = text.length();
					}
				}
				if (text != null) {
					for (int i = 0; i < lettersSoFar; i++) {
						drawLetter(g, gState, text.substring(i, i + 1), x + i + offsetX + (pixelOffsetX / fontSize), y + offsetY + (pixelOffsetY / fontSize));
					}
				} else if (glyph != null) {
					drawGlyph(g, gState, glyph, x + offsetX + (pixelOffsetX / fontSize), y + offsetY + (pixelOffsetY / fontSize));
				}
			}
			if (glyphLayout != null) {
				drawGlyphLayout(g, gState, glyphLayout, offsetX + pixelOffsetX, offsetY + pixelOffsetY);
			}
			if (wantLocked) {
				g.endBatch();
				buffer.end();
				g.startBatch();
				isLocked = true;
			}
		}
		if (isLocked) {
			if (frameBufferKey != null) {
				g.endBatch();
				FrameBuffer buffer = WindowEngine.get(frameBufferKey);
				buffer.begin();
				g.startBatch();
				g.batch().draw(texRegion, 0, 0, g.width, g.height);
				g.endBatch();
				buffer.end();
				g.startBatch();
			} else {
				g.batch().draw(texRegion, 0, 0, g.width, g.height);
			}
		}
	}
	
	public void addChild(TextBlock child) {
		children.add(child);
	}

	private void drawGlyph(Graphics g, GraphicsState gState, GlyphTile glyph, float x, float y) {
		g.batch().draw(glyph.getTexture(false), (x * fontSize) + pixelOffsetX, Graphics.height - (((y + 1) * fontSize) + pixelOffsetY),
				fontSize, fontSize * GlyphEngine.GLYPH_HEIGHT / GlyphEngine.GLYPH_WIDTH, 0, 0, GlyphEngine.GLYPH_WIDTH, GlyphEngine.GLYPH_HEIGHT, false, true);
	}

	private void drawLetter(Graphics g, GraphicsState gState, String letter, float x, float y) {
		Color blendedColor = new Color(color);
		for (TextEffect effect : effects) {
			blendedColor.mul(effect.getFade());
		}
		g.writeFixed(letter, x, y, fontSize, pixelOffsetX, pixelOffsetY, blendedColor);
	}

	private void drawGlyphLayout(Graphics g, GraphicsState gState, GlyphLayout glyphLayout, float x, float y) {
		g.writeProportional(glyphLayout, fontSize, x + pixelOffsetX, g.getViewport().getScreenHeight() - y - pixelOffsetY);
	}
	
	
	public TextBlock fade(Float from, Float to) {
		TextEffectFadeOut fade = new TextEffectFadeOut(from, to);
		effects.add(fade);
		return this;
	}
	
	public TextBlock endAt(Float end) {
		this.secondsEnd = end;
		return this;
	}

	public TextBlock duration(Float duration) {
		this.secondsEnd = secondStart + duration;
		return this;
	}
/*	public TextBlock(String text, Float fontSize,
			Float x, Float y, Color color,
			Float secondStart, Float secondsPerLetter,
			TextEffectJitter jitter, TextEffectSwap swap) {
*/	
	public TextBlock flame(Float time) {
		for (int i=0; i < text.length(); i++) {
			TextEffectSwapLinear childSwap = new TextEffectSwapLinear(0.05f, 0.15f, time);
			int stageOneLength = random.nextInt(3)+1;
			for (int j=0; j<stageOneLength; j++) {
				int which = random.nextInt(5);
				switch (which) {
				case 0:
					childSwap.addAlternate(new TextBlock("#", frameBufferKey, fontSize, (float)0, 0f, Color.RED, secondStart, secondsPerLetter, null, null));
					break;
				case 1:
					childSwap.addAlternate(new TextBlock("$", frameBufferKey, fontSize, (float)0, 0f, Color.RED, secondStart, secondsPerLetter, null, null));
					break;
				case 2:
					childSwap.addAlternate(new TextBlock("@", frameBufferKey, fontSize, (float)0, 0f, Color.RED, secondStart, secondsPerLetter, null, null));
					break;
				case 3:
					childSwap.addAlternate(new TextBlock("&", frameBufferKey, fontSize, (float)0, 0f, Color.RED, secondStart, secondsPerLetter, null, null));
					break;
				case 4:
					childSwap.addAlternate(new TextBlock("~", frameBufferKey, fontSize, (float)0, 0f, Color.RED, secondStart, secondsPerLetter, null, null));
					break;
				}
			}
			int stageTwoLength = random.nextInt(3);
			for (int j=0; j<stageTwoLength; j++) {
				int which = random.nextInt(5);
				switch (which) {
				case 0:
					childSwap.addAlternate(new TextBlock("*", frameBufferKey, fontSize, (float)0, -1f, Color.ORANGE, secondStart, secondsPerLetter, null, null));
					break;
				case 1:
					childSwap.addAlternate(new TextBlock("*", frameBufferKey, fontSize, (float)0, -1f, Color.YELLOW, secondStart, secondsPerLetter, null, null));
					break;
				case 2:
					childSwap.addAlternate(new TextBlock("~", frameBufferKey, fontSize, (float)0, -1f, Color.ORANGE, secondStart, secondsPerLetter, null, null));
					break;
				case 3:
					childSwap.addAlternate(new TextBlock("@", frameBufferKey, fontSize, (float)0, -1f, Color.YELLOW, secondStart, secondsPerLetter, null, null));
					break;
				case 4:
					childSwap.addAlternate(new TextBlock("*", frameBufferKey, fontSize, (float)0, 0f, Color.ORANGE, secondStart, secondsPerLetter, null, null));
					break;
				}
			}
			childSwap.addAlternate(new TextBlock("*", frameBufferKey, fontSize, (float)0, -1f, Color.YELLOW, secondStart, secondsPerLetter, null, null));
			switch (random.nextInt(3)) {
			case 0:
				childSwap.addAlternate(new TextBlock(".", frameBufferKey, fontSize, (float)0, -2f, Color.WHITE, secondStart, secondsPerLetter, null, null));
				break;
			case 1:
				childSwap.addAlternate(new TextBlock("*", frameBufferKey, fontSize, (float)0, -2f, Color.YELLOW, secondStart, secondsPerLetter, null, null));
				childSwap.addAlternate(new TextBlock(".", frameBufferKey, fontSize, (float)0, -3f, Color.WHITE, secondStart, secondsPerLetter, null, null));
				break;
			case 2:
				childSwap.addAlternate(new TextBlock(".", frameBufferKey, fontSize, (float)0, -2f, Color.YELLOW, secondStart, secondsPerLetter, null, null));
				childSwap.addAlternate(new TextBlock("'", frameBufferKey, fontSize, (float)0, -2f, Color.WHITE, secondStart, secondsPerLetter, null, null));
				break;
			}
			TextBlock child = new TextBlock(text.substring(i, i+1), frameBufferKey, fontSize, (float)i, 0f, color, secondStart + (i * secondsPerLetter), 0f, null, childSwap);
			children.add(child);
		}
		text = "";
		return this;
	}
	
	public void compile() {
		wantLocked = true;
		isLocked = false;
	}

	// specific to appended strings
	public int findAppendedWidth() {
		if (children.isEmpty()) {
			if (text == null) {
				// glyph
				return (int)x + 1;
			}
			return (int)x + text.length();
		}
		float maxChildWidth = 0;
		for (TextBlock tb : children) {
			float childWidth = tb.findAppendedWidth();
			if (childWidth > maxChildWidth) {
				maxChildWidth = childWidth;
			}
		}
		return (int)x + (int)maxChildWidth;
	}

	public TextBlock append(TextBlock tb) {
		tb.x = text.length();
		addChild(tb);
		return tb;
	}

	public void normalizeFont(float fontSize) {
		this.fontSize = fontSize;
		children.forEach(tb -> tb.normalizeFont(fontSize));
	}

	public float getPixelX() {
		return pixelOffsetX + x * fontSize;
	}
	public float getPixelY() {
		return pixelOffsetY + y * fontSize;
	}
	public float getPixelWidth() {
		return text.length() * fontSize;
	}
	public float getPixelHeight() {
		return fontSize;
	}
}
