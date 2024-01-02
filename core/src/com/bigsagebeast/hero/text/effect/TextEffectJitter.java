package com.bigsagebeast.hero.text.effect;

import com.bigsagebeast.hero.GameState;
import com.bigsagebeast.hero.text.TextEffect;

public class TextEffectJitter extends TextEffect {

	public TextEffectJitter(float intensity, float restraint) {
		this.intensity = intensity;
		this.restraint = restraint;
	}
	
	public TextEffectJitter(TextEffectJitter from) {
		this(from.intensity, from.restraint);
	}
	
	float intensity;
	float restraint;
	
	// recalculated each tick
	private float x;
	private float y;
	
	public float getX() {
		return x;
	}
	
	public float getY() {
		return y;
	}
	
	@Override
	public void update(GameState state) {
		x /= restraint;
		y /= restraint;
		float angle = (float)(Math.random() * Math.PI * 2);
		x += (float) (Math.sin(angle) * intensity);
		y += (float) (Math.cos(angle) * intensity);
	}
}
