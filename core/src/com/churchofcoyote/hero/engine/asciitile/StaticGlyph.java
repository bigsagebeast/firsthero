package com.churchofcoyote.hero.engine.asciitile;

import java.util.List;

import com.badlogic.gdx.graphics.Color;
import com.churchofcoyote.hero.text.TextEffect;

public final class StaticGlyph extends Glyph {
	public StaticGlyph(char symbol, Color color, List<TextEffect> effects) {
		this.symbol = symbol;
		this.color = color;
		this.effects = effects;
	}

	@Override
	public void symbol(char symbol) {
		throw new RuntimeException("Can't modify static glyph");
	}
	
	@Override
	public void color(Color color) {
		throw new RuntimeException("Can't modify static glyph");
	}
	
	@Override
	public void effects(List<TextEffect> effects) {
		throw new RuntimeException("Can't modify static glyph");
	}
}
