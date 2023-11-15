package com.churchofcoyote.hero.text.effect;

import com.badlogic.gdx.graphics.Color;
import com.churchofcoyote.hero.GameLoop;
import com.churchofcoyote.hero.GameState;
import com.churchofcoyote.hero.HeroGame;
import com.churchofcoyote.hero.text.TextEffect;

public class TextEffectFadeOut extends TextEffect {
	
	private float from;
	private float to;
	private Color currentColor;
	
	public TextEffectFadeOut(float from, float to) {
		this.from = HeroGame.getSeconds() + from;
		this.to = HeroGame.getSeconds() + to;
	}
	
	@Override
	public void update(GameState state) {
		if (state.getSeconds() <= from) {
			currentColor = Color.WHITE;
		} else if (state.getSeconds() >= to) {
			currentColor = Color.BLACK;
		} else {
			currentColor = new Color(Color.WHITE).mul(1.0f - ((state.getSeconds() - from) / (to - from)));
		}
	}

	@Override
	public boolean isClosed(GameState state) {
		return state.getSeconds() >= to;
	}
	
	@Override
	public Color getFade() {
		return currentColor;
	}
}
