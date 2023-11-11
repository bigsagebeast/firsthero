package com.churchofcoyote.hero.engine.asciitile;

import java.util.List;

import com.badlogic.gdx.graphics.Color;
import com.churchofcoyote.hero.text.TextEffect;

public class Glyph {
	protected char symbol;
	protected Color color;
	protected List<TextEffect> effects;
	protected Glyph shadow;
	private static final Color SHADOW_COLOR = Color.DARK_GRAY;
	
	public Glyph() {
		this(' ', Color.WHITE, null);
	}
	
	public Glyph(char symbol) {
		this(symbol, Color.WHITE, null);
	}
	
	public Glyph(char symbol, Color color) {
		this(symbol, color, null);
	}
	
	public Glyph(char symbol, Color color, List<TextEffect> effects) {
		this.symbol = symbol;
		this.color = color;
		this.effects = effects;
		if (color == SHADOW_COLOR && effects == null) {
			shadow = this;
		} else {
			shadow = new Glyph(symbol, SHADOW_COLOR, null);
		}
	}
	
	
	
	public void symbol(char symbol) {
		this.symbol = symbol;
	}
	
	public void color(Color color) {
		this.color = color;
	}
	
	public void effects(List<TextEffect> effects) {
		this.effects = effects;
	}
	
	public char getSymbol() {
		return symbol;
	}
	
	public Color getColor() {
		return color;
	}
	
	public List<TextEffect> effects() {
		return effects;
	}
	
	public Glyph getShadow() {
		return shadow;
	}
	
	public static final Glyph BLANK = new StaticGlyph(' ', Color.BLACK, null);

}
